package com.tunisys.TimeSheetPfe.DTOs.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class TaskDto {

    @NotBlank(message = "Title is required")
    @Size(min = 3, max = 100, message = "Title must be between 3 and 100 characters")
    private String name;

    @NotBlank(message = "Description is required")
    @Size(min = 5, max = 500, message = "Description must be between 5 and 500 characters")
    private String description;

    @NotNull(message = "Project ID is required")
    private Long projectId;
    @NotNull(message = "ManagerId ID is required")
    private Long managerId;
    private Set<Long> employeeIds; // List of employees assigned to the task (can be empty)

    @Builder.Default
    private List<String> attachments = Collections.emptyList(); // Default empty list

    @Builder.Default
    private String status = "NOT_STARTED"; // Default status

    @Builder.Default
    private String priority = "LOW"; // Default priority

    @Builder.Default
    private String difficulty = "EASY"; // Default
}
