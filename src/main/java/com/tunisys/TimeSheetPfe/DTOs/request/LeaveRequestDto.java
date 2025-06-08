package com.tunisys.TimeSheetPfe.DTOs.request;

import com.tunisys.TimeSheetPfe.models.LeaveType;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Future;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaveRequestDto {
    @NotNull(message = "La date de début est obligatoire")
    private LocalDate startDate;

    @NotNull(message = "La date de fin est obligatoire")
    private LocalDate endDate;

    @NotNull(message = "Le type de congé est obligatoire")
    private LeaveType leaveType;

    @NotBlank(message = "Le motif est obligatoire")
    @Size(max = 500, message = "Le motif ne peut pas dépasser 500 caractères")
    private String reason;
}
