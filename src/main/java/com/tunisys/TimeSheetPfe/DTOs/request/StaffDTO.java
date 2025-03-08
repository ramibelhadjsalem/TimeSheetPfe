package com.tunisys.TimeSheetPfe.DTOs.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StaffDTO {
    private Long id;
    private String firstname;
    private String lastname;
    private String phone;
    private String email;
    private String adresse;
    private String role;
}
