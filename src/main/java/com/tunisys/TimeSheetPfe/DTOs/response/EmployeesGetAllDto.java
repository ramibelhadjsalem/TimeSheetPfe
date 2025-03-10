package com.tunisys.TimeSheetPfe.DTOs.response;

import com.tunisys.TimeSheetPfe.models.ERole;
import com.tunisys.TimeSheetPfe.models.Project;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EmployeesGetAllDto {
    private Long id;  // Employee ID
    private String name;  // Employee Name
    private String email;  // Employee Email
    private String phone;  // Employee Phone Number
    private String imgUrl;  // Employee Image URL (Optional)
    private List<TaskDto> tasks;
    private List<Role> roles ;


}
@Data
@AllArgsConstructor
@NoArgsConstructor
class TaskDto {
    private Long id;  // Task ID
    private String title;  // Task Title
    private String description;  // Task Description
    private String status;  // Task Status
    private String priority;  // Task Priority
    private String difficulty;  // Task Difficulty
}
@Data
@AllArgsConstructor
@NoArgsConstructor
class Role {
    private ERole name;
}
