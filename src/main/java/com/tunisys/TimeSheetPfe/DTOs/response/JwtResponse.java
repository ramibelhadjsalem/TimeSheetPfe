package com.tunisys.TimeSheetPfe.DTOs.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
public class JwtResponse {
    private String token,refreshToken;
    private List<String> roles;
}
