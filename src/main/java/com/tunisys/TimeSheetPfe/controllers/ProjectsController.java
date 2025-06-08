package com.tunisys.TimeSheetPfe.controllers;

import com.tunisys.TimeSheetPfe.DTOs.request.AddStaffDto;
import com.tunisys.TimeSheetPfe.DTOs.request.ProjectDtoRequest;
import com.tunisys.TimeSheetPfe.DTOs.response.CurrentProjectInfo;
import com.tunisys.TimeSheetPfe.DTOs.response.MessageResponse;
import com.tunisys.TimeSheetPfe.DTOs.response.ProjectControllerResponseDto;
import com.tunisys.TimeSheetPfe.models.NotificationType;
import com.tunisys.TimeSheetPfe.models.Project;
import com.tunisys.TimeSheetPfe.models.UserModel;
import com.tunisys.TimeSheetPfe.services.notificationService.NotificationService;
import com.tunisys.TimeSheetPfe.services.projectService.ProjectService;
import com.tunisys.TimeSheetPfe.services.userService.UserService;
import com.tunisys.TimeSheetPfe.utils.TokenUtils;
import com.tunisys.TimeSheetPfe.utils.NotificationMessages;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projects")
public class ProjectsController {

    @Autowired
    ProjectService projectService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    UserService userService;
    @Autowired
    private TokenUtils tokenUtils;

    @GetMapping
    public List<ProjectControllerResponseDto> findAll() {
        List<Project> projects = projectService.getAll();
        UserModel currentUser = userService.findById(tokenUtils.ExtractId());

        return projects.stream()
                .map(project -> ProjectControllerResponseDto.fromProject(project, currentUser))
                .collect(Collectors.toList());
    }

    @GetMapping("/current/{userId}")
    public ResponseEntity<?> getCurrentProject(@PathVariable Long userId) {
        UserModel userModel = userService.findById(userId);
        if (userModel.getCurrentProject() != null) {
            ProjectControllerResponseDto projectDto = ProjectControllerResponseDto
                    .fromProject(userModel.getCurrentProject(), userModel);
            // This is definitely the current project for the user
            projectDto.setCurrentProject(true);
            return ResponseEntity.ok(projectDto);
        } else {
            return ResponseEntity.noContent().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> createNewProject(@Valid @RequestBody ProjectDtoRequest dto) {
        Project project = Project.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .deadline(dto.getDeadline())
                .tasks(Collections.emptySet())
                .employees(new HashSet<>())
                .build();
        if (dto.getManagerId() != null) {
            UserModel manager = userService.findById(dto.getManagerId());
            manager.setCurrentProject(project);
            project.setManager(manager);
        }
        if (dto.getEmployeeIds() != null) {
            Set<UserModel> employees = new HashSet<>();
            for (Long employeeId : dto.getEmployeeIds()) {
                UserModel employee = userService.findById(employeeId);
                employee.setCurrentProject(project);
                employees.add(employee);
            }
            project.setEmployees(employees);
        }
        Project newProject = projectService.save(project);
        newProject.getEmployees().forEach(employee -> {
            notificationService.createAndSendNotification(
                    employee.getId(),
                    NotificationMessages.Projects.NEW_PROJECT_ASSIGNED_TITLE,
                    NotificationMessages.Projects.newProjectAssignedBody(project.getName(), project.getDescription()),
                    NotificationMessages.ActionUrls.projectUrl(newProject.getId()),
                    NotificationType.INFO);
        });

        UserModel currentUser = userService.findById(tokenUtils.ExtractId());
        ProjectControllerResponseDto responseDto = ProjectControllerResponseDto.fromProject(newProject, currentUser);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/{id}/add-stuff")
    public ResponseEntity<?> addStuffToProject(@PathVariable Long id, @Valid @RequestBody AddStaffDto dto) {
        Project project = projectService.getById(id);
        // Assign manager if not already set and managerId is provided
        if (project.getManager() == null && dto.getManagerId() != null) {
            UserModel manager = userService.findById(dto.getManagerId());
            if (project.getManager() != null) {
                MessageResponse.badRequest("can not set the projet manager"); // Could return a custom error message
                                                                              // instead
            }
            project.setManager(manager);
        }

        // Add employees to the project
        if (dto.getEmployeesIds() != null && !dto.getEmployeesIds().isEmpty()) {
            List<UserModel> employees = dto.getEmployeesIds().stream()
                    .map(employeeId -> userService.findById(employeeId))
                    .toList();
            project.getEmployees().addAll(employees);
            employees.forEach(employee -> employee.setCurrentProject(project));
            project.getEmployees().forEach(employee -> {
                notificationService.createAndSendNotification(
                        employee.getId(),
                        NotificationMessages.Projects.ADDED_TO_PROJECT_TITLE,
                        NotificationMessages.Projects.addedToProjectBody(project.getName(), project.getDescription()),
                        NotificationMessages.ActionUrls.projectUrl(project.getId()),
                        NotificationType.INFO);
            });
        }

        Project savedProject = projectService.save(project);
        UserModel currentUser = userService.findById(tokenUtils.ExtractId());
        ProjectControllerResponseDto responseDto = ProjectControllerResponseDto.fromProject(savedProject, currentUser);

        return ResponseEntity.ok(responseDto);
    }

    @GetMapping("/current")
    public ResponseEntity<?> getCurrentProjectTask() {
        UserModel userModel = userService.findById(tokenUtils.ExtractId());
        if (userModel.getCurrentProject() == null) {
            return ResponseEntity.noContent().build(); // Return 204 No Content when user has no current project
        }
        return ResponseEntity.ok(CurrentProjectInfo.fromProject(userModel.getCurrentProject(), userModel));
    }

    @GetMapping("/manager")
    public ResponseEntity<?> getProjectByManager() {
        UserModel userModel = userService.findById(tokenUtils.ExtractId());
        List<Project> projects = projectService.findByManagerId(userModel.getId());

        return ResponseEntity.ok(projects.stream()
                .map(project -> ProjectControllerResponseDto.fromProject(project, userModel))
                .collect(Collectors.toList()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<Void> deleteProject(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.noContent().build();
    }

}
