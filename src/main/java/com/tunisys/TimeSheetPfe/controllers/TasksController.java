package com.tunisys.TimeSheetPfe.controllers;

import com.tunisys.TimeSheetPfe.models.*;
import com.tunisys.TimeSheetPfe.services.projectService.ProjectService;
import com.tunisys.TimeSheetPfe.services.taskService.TaskService;
import com.tunisys.TimeSheetPfe.services.userService.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
public class TasksController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_MANAGER')")
    public ResponseEntity<Task> createNewTask(@Valid @RequestBody Task taskRequest) {
        Project project = projectService.getById(taskRequest.getProject().getId());
        Task task = Task.builder()
                .title(taskRequest.getTitle())
                .description(taskRequest.getDescription())
                .status(taskRequest.getStatus())
                .priority(taskRequest.getPriority())
                .difficulty(taskRequest.getDifficulty())
                .attachments(taskRequest.getAttachments() != null ? taskRequest.getAttachments() : Collections.emptyList())
                .employees(new HashSet<>())
                .project(project)// Empty employee list
                .build();

        return ResponseEntity.ok(taskService.Save(task));
    }
}
