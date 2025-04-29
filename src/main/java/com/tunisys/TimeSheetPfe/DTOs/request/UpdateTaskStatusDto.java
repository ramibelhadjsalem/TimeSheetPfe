package com.tunisys.TimeSheetPfe.DTOs.request;

import com.tunisys.TimeSheetPfe.models.EStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UpdateTaskStatusDto {

    @NotNull(message = "Status cannot be null")
    private EStatus status;  // 👈 use Enum directly

}
