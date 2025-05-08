package com.tunisys.TimeSheetPfe.DTOs.response;

import com.tunisys.TimeSheetPfe.models.ERole;
import com.tunisys.TimeSheetPfe.models.Task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EmployeesGetAllDto {
    private Long id; // Employee ID
    private String name; // Employee Name
    private String email; // Employee Email
    private String phone; // Employee Phone Number
    private String imgUrl; // Employee Image URL (Optional)
    private Long currentProject; // Current Project ID
    private List<TaskDto> tasks;
    private List<Role> roles;

    public static EmployeesGetAllDto fromUser(com.tunisys.TimeSheetPfe.models.UserModel user) {
        return EmployeesGetAllDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phone(user.getPhone())
                .imgUrl(user.getImgUrl())
                .currentProject(user.getCurrentProject() != null ? user.getCurrentProject().getId() : null)
                .tasks(user.getTasks().stream()
                        .map(TaskDto::fromTask)
                        .toList())
                .roles(user.getRoles().stream()
                        .map(Role::fromRole)
                        .toList())
                .build();
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
class TaskDto {
    private Long id; // Task ID
    private String title; // Task Title
    private String description; // Task Description
    private String status; // Task Status
    private String priority; // Task Priority
    private String difficulty; // Task Difficulty
    private Long projectId; // Project ID

    public static TaskDto fromTask(Task task) {
        return TaskDto.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus().name())
                .priority(task.getPriority().name())
                .difficulty(task.getDifficulty().name())
                .projectId(task.getProject() != null ? task.getProject().getId() : null)
                .build();
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
class Role {
    private ERole name;

    public static Role fromRole(com.tunisys.TimeSheetPfe.models.Role role) {
        return Role.builder()
                .name(role.getName())
                .build();
    }

}
