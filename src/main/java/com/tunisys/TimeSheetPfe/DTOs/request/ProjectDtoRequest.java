package com.tunisys.TimeSheetPfe.DTOs.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
public class ProjectDtoRequest {
    @NotBlank(message = "Project name is required")
    private String name;
    @NotBlank(message = "Project description is required")
    private String description;
    private Long managerId;
    private Set<Long> employeeIds;
    @NotNull(message = "Deadline is required")
    private LocalDate deadline;
}
