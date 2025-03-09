package com.tunisys.TimeSheetPfe.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.tunisys.TimeSheetPfe.DTOs.request.ProjectDtoRequest;
import com.tunisys.TimeSheetPfe.DTOs.view.View;
import com.tunisys.TimeSheetPfe.models.Project;
import com.tunisys.TimeSheetPfe.models.UserModel;
import com.tunisys.TimeSheetPfe.services.projectService.ProjectService;
import com.tunisys.TimeSheetPfe.services.userService.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/projects")
public class ProjectsController {

    @Autowired
    ProjectService projectService;


    @Autowired UserService userService;


    @GetMapping
    @JsonView(View.External.class)
    public List<Project> findAll(){
        return projectService.getAll();
    }
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<?> createNewProject(@Valid @RequestBody ProjectDtoRequest dto){
        Project project = Project.builder()
                .name(dto.getName())
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

        return ResponseEntity.ok(projectService.save(project));
    }

}
