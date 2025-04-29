package com.tunisys.TimeSheetPfe.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.tunisys.TimeSheetPfe.DTOs.request.TaskDto;
import com.tunisys.TimeSheetPfe.DTOs.request.UpdateTaskStatusDto;
import com.tunisys.TimeSheetPfe.DTOs.response.ProjectControllerResponseDto;
import com.tunisys.TimeSheetPfe.DTOs.response.TaskResponseDto;
import com.tunisys.TimeSheetPfe.DTOs.view.View;
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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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
    @JsonView(View.Base.class)
    public ResponseEntity<Task> createNewTask(@Valid @RequestBody TaskDto taskRequest) {
        Project project = projectService.getById(taskRequest.getProjectId());
        Task task = Task.builder()
                .title(taskRequest.getName())
                .description(taskRequest.getDescription())
                .status(EStatus.fromString(taskRequest.getStatus()))
                .priority(EPriority.fromString(taskRequest.getPriority()))
                .difficulty(EDifficulty.fromString(taskRequest.getDifficulty()))
                .attachments(taskRequest.getAttachments() != null ? taskRequest.getAttachments() : Collections.emptyList())
                .employees(new HashSet<>())
                .project(project)
                .deadline(taskRequest.getDeadline())
                .build();

        // Map employee IDs to UserModels and add them to the task
        if (taskRequest.getEmployeeIds() != null && !taskRequest.getEmployeeIds().isEmpty()) {
            taskRequest.getEmployeeIds().stream()
                    .map(userService::findById) // Find each user by ID
                    .forEach(task::addEmployee); // Add the user to the task
        }
        Task newTask =taskService.Save(task);
        newTask.getEmployees().forEach(employee -> {
            notificationService.createAndSendNotification(
                    employee.getId(),
                    "New Task assigned with id: " +newTask.getId(),
                    "You have been assigned to a new task: " + newTask.getDescription(),
                    "task/"+newTask.getId(),
                    NotificationType.INFO
            );
        });

        return ResponseEntity.ok(newTask);
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @JsonView(View.Base.class)
    public List<Task> findAll() {
        return taskService.getAll();
    }


    @GetMapping("/employee")
    public List<TaskResponseDto> getTaskByCurrentUserAndProject() {
        UserModel user = userService.findById(tokenUtils.ExtractId());

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
        // 🔥 Check if employee is part of assigned employees
        boolean isAssigned = task.getEmployees().stream()
                .anyMatch(user -> user.getId().equals(userId));

        if (!isAssigned) {
            return ResponseEntity.status(403).body("You are not assigned to this task.");
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
        } else {
            return ResponseEntity.badRequest().body("Invalid status transition for employee.");
        }

        taskService.save(task);
        UserModel model =task.getProject().getManager();
        if(model != null){
            notificationService.createAndSendNotification(
                    model.getId(),
                    "Task status updated with id: " + task.getId(),
                    "Task status updated to: " + task.getStatus(),
                    "task/" + task.getId(),
                    NotificationType.INFO
            );
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
            taskService.save(task);


            task.getEmployees().forEach(employee -> {
                notificationService.createAndSendNotification(
                        employee.getId(),
                        "Task status updated with id: " + task.getId(),
                        "Task status updated to: " + task.getStatus(),
                        "task/" + task.getId(),
                        task.getStatus().equals(EStatus.IMPROVED) ? NotificationType.INFO : NotificationType.WARNING
                );
            });
            return ResponseEntity.ok("Task status updated successfully by manager.");
        } else {
            return ResponseEntity.badRequest().body("Manager can only approve or disapprove a task.");
        }
    }
}
