package com.tunisys.TimeSheetPfe.DTOs.response;

import com.tunisys.TimeSheetPfe.models.Project;
import com.tunisys.TimeSheetPfe.models.Task;
import com.tunisys.TimeSheetPfe.models.UserModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectControllerResponseDto {

    private Long id;
    private String name;
    private String description;
    private LocalDate deadline;
    private User manager;
    private Set<User> employees;
    private Set<TaskProjectDto> tasks;
    private boolean isCurrentProject;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class TaskProjectDto {
        private Long id; // Task ID
        private String title; // Task Title
        private String description; // Task Description
        private String status; // Task Status
        private String priority; // Task Priority
        private String difficulty;
        private Long projectId; // Project ID
        private List<User> employees; // Task Employees
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class User {
        private Long id; // Employee ID
        private String name; // Employee Name
        private String email; // Employee Email
        private String phone; // Employee Phone Number
        private String imgUrl;
        private String cin = ""; // Employee CIN
        private String firstName = "";
        private String lastName = "";
        private String department = "";
        private Integer experience;
        private Long currentProject; // Current Project ID

        public static User fromUserModel(UserModel user) {
            User dto = new User();
            dto.setId(user.getId());
            dto.setName(user.getName());
            dto.setEmail(user.getEmail());
            dto.setPhone(user.getPhone());
            dto.setImgUrl(user.getImgUrl());
            dto.setCin(user.getCin() != null ? user.getCin() : "");
            dto.setFirstName(user.getFirstName() != null ? user.getFirstName() : "");
            dto.setLastName(user.getLastName() != null ? user.getLastName() : "");
            dto.setDepartment(user.getDepartment() != null ? user.getDepartment() : "");
            dto.setExperience(user.getExperience());
            dto.setCurrentProject(user.getCurrentProject() != null ? user.getCurrentProject().getId() : null);
            return dto;
        }
    }

    public static ProjectControllerResponseDto fromProject(Project project, UserModel currentUser) {
        ProjectControllerResponseDto dto = new ProjectControllerResponseDto();
        dto.setId(project.getId());
        dto.setName(project.getName());
        dto.setDescription(project.getDescription());
        dto.setDeadline(project.getDeadline());

        // Set manager if exists
        if (project.getManager() != null) {
            dto.setManager(User.fromUserModel(project.getManager()));
        }

        // Convert employees
        if (project.getEmployees() != null) {
            dto.setEmployees(project.getEmployees().stream()
                    .map(User::fromUserModel)
                    .collect(Collectors.toSet()));
        }

        // Convert tasks
        if (project.getTasks() != null) {
            dto.setTasks(project.getTasks().stream()
                    .map(task -> {
                        TaskProjectDto taskDto = new TaskProjectDto();
                        taskDto.setId(task.getId());
                        taskDto.setTitle(task.getTitle());
                        taskDto.setDescription(task.getDescription());
                        taskDto.setStatus(task.getStatus().name());
                        taskDto.setPriority(task.getPriority().name());
                        taskDto.setDifficulty(task.getDifficulty().name());
                        taskDto.setProjectId(project.getId());

                        // Convert task employees
                        if (task.getEmployees() != null) {
                            taskDto.setEmployees(task.getEmployees().stream()
                                    .map(User::fromUserModel)
                                    .collect(Collectors.toList()));
                        }

                        return taskDto;
                    })
                    .collect(Collectors.toSet()));
        }

        // Set isCurrentProject based on whether this is the current user's current
        // project
        dto.setCurrentProject(currentUser != null && currentUser.getCurrentProject() != null &&
                currentUser.getCurrentProject().getId().equals(project.getId()));

        return dto;
    }
}
