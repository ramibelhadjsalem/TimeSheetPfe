package com.tunisys.TimeSheetPfe.DTOs.response;

import com.tunisys.TimeSheetPfe.models.Task;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskResponseDto {
    private Long id; // Task ID
    private String title; // Task Title
    private String description; // Task Description
    private String status; // Task Status
    private String priority; // Task Priority
    private String difficulty;
    private LocalDate deadline;
    private LocalDateTime startAt; // Date and time when the task was started
    private LocalDateTime finishedAt; // Date and time when the task was finished
    private Long projectId; // Project ID
    private List<User> employees;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class User {
        private Long id; // Employee ID
        private String name; // Employee Name
        private String email; // Employee Email
        private String phone; // Employee Phone Number
        private String imgUrl; // Employee Image URL (Optional)
        private Long currentProject; // Current Project ID
    }

    public static TaskResponseDto from(Task task) {
        TaskResponseDto dto = new TaskResponseDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus().name());
        dto.setPriority(task.getPriority().name());
        dto.setDifficulty(task.getDifficulty().name());
        dto.setDeadline(task.getDeadline());
        dto.setStartAt(task.getStartAt());
        dto.setFinishedAt(task.getFinishedAt());

        // Set project ID if project exists
        if (task.getProject() != null) {
            dto.setProjectId(task.getProject().getId());
        }

        List<User> employees = task.getEmployees().stream()
                .map(employee -> new User(employee.getId(), employee.getName(), employee.getEmail(),
                        employee.getPhone(), employee.getImgUrl(),
                        employee.getCurrentProject() != null ? employee.getCurrentProject().getId() : null))
                .toList();

        dto.setEmployees(employees);

        return dto;
    }
}
