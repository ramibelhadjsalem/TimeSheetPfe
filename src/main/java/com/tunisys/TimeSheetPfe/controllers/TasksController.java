package com.tunisys.TimeSheetPfe.controllers;

import com.tunisys.TimeSheetPfe.DTOs.request.TaskDto;
import com.tunisys.TimeSheetPfe.DTOs.request.UpdateTaskStatusDto;
import com.tunisys.TimeSheetPfe.DTOs.response.TaskResponseDto;
import com.tunisys.TimeSheetPfe.models.*;
import com.tunisys.TimeSheetPfe.services.notificationService.NotificationService;
import com.tunisys.TimeSheetPfe.services.projectService.ProjectService;
import com.tunisys.TimeSheetPfe.services.taskService.TaskService;
import com.tunisys.TimeSheetPfe.services.userService.UserService;
import com.tunisys.TimeSheetPfe.utils.TokenUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.tunisys.TimeSheetPfe.exceptions.EntityNotFoundException;

@RestController
@RequestMapping("/api/tasks")
public class TasksController {

    @Autowired
    private TaskService taskService;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserService userService;
    @Autowired
    private TokenUtils tokenUtils;

    @Autowired
    private ProjectService projectService;

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<TaskResponseDto> createNewTask(@Valid @RequestBody TaskDto taskRequest) {
        Project project = projectService.getById(taskRequest.getProjectId());

        // Create the task with all properties except employees
        Task task = Task.builder()
                .title(taskRequest.getName())
                .description(taskRequest.getDescription())
                .status(EStatus.fromString(taskRequest.getStatus()))
                .priority(EPriority.fromString(taskRequest.getPriority()))
                .difficulty(EDifficulty.fromString(taskRequest.getDifficulty()))
                .attachments(
                        taskRequest.getAttachments() != null ? taskRequest.getAttachments() : Collections.emptyList())
                .project(project)
                .deadline(taskRequest.getDeadline())
                .build();

        // Save the task first to get an ID
        Task savedTask = taskService.Save(task);

        // Now add employees to the saved task
        if (taskRequest.getEmployeeIds() != null && !taskRequest.getEmployeeIds().isEmpty()) {
            // Create a final reference to the task for use in lambda
            final Task taskWithEmployees = savedTask;

            taskRequest.getEmployeeIds().forEach(employeeId -> {
                UserModel employee = userService.findById(employeeId);
                taskWithEmployees.addEmployee(employee); // This now updates both sides of the relationship
            });

            // Save the task again with the employees
            savedTask = taskService.Save(taskWithEmployees);
        }

        // Create a final reference for use in lambda
        final Task finalTask = savedTask;

        // Send notifications to employees
        finalTask.getEmployees().forEach(employee -> {
            notificationService.createAndSendNotification(
                    employee.getId(),
                    "Nouvelle tâche assignée",
                    "Vous avez été assigné à la tâche \"" + finalTask.getTitle() + "\" - " + finalTask.getDescription(),
                    "task/" + finalTask.getId(),
                    NotificationType.INFO);
        });

        return ResponseEntity.ok(TaskResponseDto.from(savedTask));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<TaskResponseDto> findAll() {
        return taskService.getAll().stream()
                .map(TaskResponseDto::from)
                .toList();
    }

    @GetMapping("/employee")
    public List<TaskResponseDto> getTaskByCurrentUserAndProject() {
        UserModel user = userService.findById(tokenUtils.ExtractId());

        if (user.getCurrentProject() == null) {
            return Collections.emptyList(); // Return empty list if user has no current project
        }

        return taskService.getTasksByUserIdAndProjectId(user.getId(), user.getCurrentProject().getId())
                .stream()
                .map(TaskResponseDto::from)
                .toList();
    }

    @GetMapping("/user/{id}")
    public List<TaskResponseDto> getTasksByUser(@PathVariable Long id) {
        UserModel user = userService.findById(id);

        if (user.getCurrentProject() == null) {
            return Collections.emptyList(); // Return empty list if user has no current project
        }

        return taskService.getTasksByUserIdAndProjectId(user.getId(), user.getCurrentProject().getId())
                .stream()
                .map(TaskResponseDto::from)
                .toList();
    }

    // Route for EMPLOYEE to update own task status
    @PutMapping("/{taskId}/employee")
    public ResponseEntity<?> updateTaskStatusByEmployee(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusDto dto) {
        Long userId = tokenUtils.ExtractId();
        Task task = taskService.getById(taskId);
        UserModel currentUser = userService.findById(userId);

        // 🔥 Check if employee is part of assigned employees
        boolean isAssigned = task.getEmployees().stream()
                .anyMatch(user -> user.getId().equals(userId));

        if (!isAssigned) {
            // If not assigned, add the user to the task's employees
            task.addEmployee(currentUser);
            task = taskService.save(task);

            // Log that we've added the user to the task
            System.out.println("Added user " + userId + " to task " + taskId + " employees list");
        }

        EStatus currentStatus = task.getStatus();
        EStatus requestedStatus = dto.getStatus();

        // 🔥 Business logic for employee:
        if (currentStatus == EStatus.IMPROVED) {
            return ResponseEntity.badRequest().body("Task is already approved. Cannot modify.");
        }

        if (currentStatus == EStatus.DISAPPROVED) {
            if (requestedStatus == EStatus.PROGRESS) {
                task.setStatus(EStatus.PROGRESS);
            } else {
                return ResponseEntity.badRequest().body("When disapproved, you can only set to PROGRESS.");
            }
        } else if (currentStatus == EStatus.NOT_STARTED && requestedStatus == EStatus.PROGRESS) {
            task.setStatus(EStatus.PROGRESS);
        } else if (currentStatus == EStatus.PROGRESS && requestedStatus == EStatus.FINISHED) {
            task.setStatus(EStatus.FINISHED);
            // Set finishedAt to current date when task is marked as finished
            if (dto.getFinishedAt() != null) {
                task.setFinishedAt(dto.getFinishedAt());
            } else {
                task.setFinishedAt(LocalDate.now());
            }
        } else {
            return ResponseEntity.badRequest().body("Invalid status transition for employee.");
        }

        taskService.save(task);
        UserModel model = task.getProject().getManager();
        if (model != null) {
            notificationService.createAndSendNotification(
                    model.getId(),
                    "🔄 Statut de tâche mis à jour",
                    "Le statut de la tâche \"" + task.getTitle() + "\" a été mis à jour à " + task.getStatus(),
                    "task/" + task.getId(),
                    NotificationType.INFO);
        }
        return ResponseEntity.ok("Task status updated successfully by employee.");
    }

    // Route for MANAGER to approve/disapprove
    @PutMapping("/{taskId}/manager")
    public ResponseEntity<?> updateTaskStatusByManager(
            @PathVariable Long taskId,
            @Valid @RequestBody UpdateTaskStatusDto dto) {

        Task task = taskService.getById(taskId);
        if (task == null) {
            return ResponseEntity.notFound().build();
        }

        EStatus requestedStatus = dto.getStatus();

        // 🔥 Business logic for manager:
        if (requestedStatus == EStatus.IMPROVED || requestedStatus == EStatus.DISAPPROVED) {
            task.setStatus(requestedStatus);

            // If task is being approved and finishedAt is not set, set it to now
            if (requestedStatus == EStatus.IMPROVED && task.getFinishedAt() == null) {
                task.setFinishedAt(LocalDate.now());
            }

            taskService.save(task);

            task.getEmployees().forEach(employee -> {
                String emoji = task.getStatus().equals(EStatus.IMPROVED) ? "✅" : "⚠️";
                String statusText = task.getStatus().equals(EStatus.IMPROVED) ? "APPROUVÉE" : "DÉSAPPROUVÉE";

                notificationService.createAndSendNotification(
                        employee.getId(),
                        emoji + " Tâche " + statusText,
                        "Votre tâche \"" + task.getTitle() + "\" a été " +
                                (task.getStatus().equals(EStatus.IMPROVED) ? "approuvée" : "désapprouvée") +
                                " par le manager.",
                        "task/" + task.getId(),
                        task.getStatus().equals(EStatus.IMPROVED) ? NotificationType.INFO : NotificationType.WARNING);
            });
            return ResponseEntity.ok("Task status updated successfully by manager.");
        } else {
            return ResponseEntity.badRequest().body("Manager can only approve or disapprove a task.");
        }
    }

    /**
     * Supprime une tâche en préservant les utilisateurs associés.
     * Cette méthode détache proprement la tâche des utilisateurs et du projet avant
     * de la supprimer,
     * et supprime également les feuilles de temps associées.
     *
     * @param taskId L'identifiant de la tâche à supprimer
     * @return ResponseEntity avec un message de succès ou d'erreur
     */
    @DeleteMapping("/{taskId}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId) {
        try {
            Task task = taskService.getById(taskId);
            String taskTitle = task.getTitle();

            // Récupérer les employés avant de supprimer la tâche pour les notifications
            Set<UserModel> employees = new HashSet<>(task.getEmployees());

            // Supprimer la tâche en préservant les utilisateurs
            taskService.deleteTaskSafely(taskId);

            // Envoyer des notifications aux employés concernés
            employees.forEach(employee -> {
                notificationService.createAndSendNotification(
                        employee.getId(),
                        "Tache supprimee",
                        "La tache \"" + taskTitle + "\" a laquelle vous étiez assigné a ete supprimee.",
                        "dashboard",
                        NotificationType.WARNING);
            });

            return ResponseEntity.ok("Tâche supprimée avec succes");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la suppression de la tache: " + e.getMessage());
        }
    }
}
