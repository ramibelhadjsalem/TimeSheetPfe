package com.tunisys.TimeSheetPfe.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.google.cloud.storage.Acl.User;
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
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api/projects")
public class ProjectsController {

    @Autowired
    ProjectService projectService;

    @Autowired private NotificationService notificationService;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    UserService userService;
    @Autowired
    private TokenUtils tokenUtils;

    @GetMapping
    public List<ProjectControllerResponseDto> findAll() {

        List<Project> projects = projectService.getAll();

        return projects.stream()
                .map(project -> modelMapper.map(project, ProjectControllerResponseDto.class))
                .collect(Collectors.toList());
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> createNewProject(@Valid @RequestBody ProjectDtoRequest dto) {
        Project project = Project.builder()
                .description(dto.getDescription())
                .deadline(dto.getDeadline())
                .tasks(Collections.emptySet())
                .employees(new HashSet<>())
                .build();
        if (dto.getManagerId() != null) {
            project.setManager(userService.findById(dto.getManagerId()));
        }
        if (dto.getEmployeeIds() != null) {
            Set<UserModel> employees = new HashSet<>();
            for (Long employeeId : dto.getEmployeeIds()) {
                UserModel employee = userService.findById(employeeId);
                employees.add(employee);
            }
            project.setEmployees(employees);
        }
        Project newProject = projectService.save(project);
        newProject.getEmployees().forEach(employee -> {
            notificationService.createAndSendNotification(
                    employee.getId(),
                    "New project assigned with id: " +newProject.getId(),
                    "You have been assigned to a new project: " + project.getDescription(),
                    "project/"+newProject.getId(),
                    NotificationType.INFO
                    );
        });

        return ResponseEntity.ok(newProject);
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

            project.getEmployees().forEach(employee -> {
                notificationService.createAndSendNotification(
                        employee.getId(),
                        "New project assigned with id: " +project.getId(),
                        "You have been assigned to a new project: " + project.getDescription(),
                        "project/"+project.getId(),
                        NotificationType.INFO
                );
            });
        }

        return ResponseEntity.ok(projectService.save(project));
    }

    @GetMapping("/current")
    public ResponseEntity<CurrentProjectInfo> getCurrentProjectTask() {
        UserModel userModel = userService.findById(tokenUtils.ExtractId());
        return ResponseEntity.ok(CurrentProjectInfo.fromProject(userModel.getCurrentProject(), userModel));

    }



    @GetMapping("/manager")
    public ResponseEntity<?> getProjectByManager() {
        UserModel userModel = userService.findById(tokenUtils.ExtractId());
        List<Project> projects = projectService.findByManagerId(userModel.getId());

        return ResponseEntity.ok(projects.stream()
                .map(project -> modelMapper.map(project, ProjectControllerResponseDto.class))
                .collect(Collectors.toList()));
    }

}
