package com.tunisys.TimeSheetPfe.DTOs.request;

import com.tunisys.TimeSheetPfe.models.ERole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class CreateUserDto {

    @NotBlank(message = "email is required")
    private String email;

//    @Pattern(regexp = "ROLE_MANAGER|ROLE_ADMIN|ROE_EMPLOYEE", message = "Invalid role")
    @NotNull
    private ERole role;

}
