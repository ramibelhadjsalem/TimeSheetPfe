package com.tunisys.TimeSheetPfe.DTOs.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateConnectionDto {

    @NotBlank(message = "fcmToken is required")
    @Size(min = 3, message = "fcmToken must be between 3 and 100 characters")
    private String fcmToken;
}
