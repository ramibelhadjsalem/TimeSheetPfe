package com.tunisys.TimeSheetPfe.utils;


import com.tunisys.TimeSheetPfe.DTOs.request.StaffDTO;
import com.tunisys.TimeSheetPfe.models.UserModel;

public class Mapper {
    public static StaffDTO toDto(UserModel user) {
        return StaffDTO.builder()
                .id(user.getId())
                .phone(user.getPhone())
                .email(user.getEmail())
                .build();
    }

}
