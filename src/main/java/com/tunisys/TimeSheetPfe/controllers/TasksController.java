package com.tunisys.TimeSheetPfe.controllers;

import com.fasterxml.jackson.annotation.JsonView;
import com.tunisys.TimeSheetPfe.DTOs.request.TaskDto;
import com.tunisys.TimeSheetPfe.DTOs.view.View;
import com.tunisys.TimeSheetPfe.models.*;
import com.tunisys.TimeSheetPfe.services.projectService.ProjectService;
import com.tunisys.TimeSheetPfe.services.taskService.TaskService;
import com.tunisys.TimeSheetPfe.services.userService.UserService;
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
    private UserService userService;

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
                .build();
        if (taskRequest.getManagerId()!=null){
            UserModel manager = userService.findById(taskRequest.getManagerId());
            task.setManager(manager);
        }

        return ResponseEntity.ok(taskService.Save(task));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @JsonView(View.Base.class)
    public List<Task> findAll(){
        return taskService.getAll();
    }
}
