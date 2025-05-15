package com.tunisys.TimeSheetPfe.DTOs.request;

import com.tunisys.TimeSheetPfe.models.EStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class UpdateTaskStatusDto {

    @NotNull(message = "Status cannot be null")
    private EStatus status; // 👈 use Enum directly

    private LocalDateTime startAt; // Date and time when the task was started (can be null)

    private LocalDateTime finishedAt; // Date and time when the task was finished (can be null)
}
