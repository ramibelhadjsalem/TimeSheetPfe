package com.tunisys.TimeSheetPfe.DTOs.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
@Data
public class SignupRequest {
    @NotNull
    @Length(min = 3, max = 20)
    private  String firstname;
    @NotNull
    @Length(min = 3, max = 20)
    private  String lastname;
    @NotNull
    @Email
    private String email;
    @NotNull
    @Length(min = 3, max = 6)
    private String password;
    private String address;
    private String phone;
    private String roles;
}
