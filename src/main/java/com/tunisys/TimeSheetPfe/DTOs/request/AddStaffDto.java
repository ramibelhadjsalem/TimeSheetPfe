package com.tunisys.TimeSheetPfe.DTOs.request;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AddStaffDto {
    private Long managerId;
    private List<Long> employeesIds = new ArrayList<>();
}
