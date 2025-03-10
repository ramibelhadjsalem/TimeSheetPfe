package com.tunisys.TimeSheetPfe.DTOs.response;

import com.fasterxml.jackson.annotation.JsonView;
import com.tunisys.TimeSheetPfe.DTOs.view.View;
import com.tunisys.TimeSheetPfe.models.Task;
import com.tunisys.TimeSheetPfe.models.UserModel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProjectControllerResponseDto {

    private Long id;
    private String name;
    private String description;
    private LocalDate deadline;
    private User manager;
    private Set<User> employees ;
    private Set<TaskProjectDto> tasks ;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class TaskProjectDto {
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
    private static  class User {
        private Long id;  // Employee ID
        private String name;  // Employee Name
        private String email;  // Employee Email
        private String phone;  // Employee Phone Number
        private String imgUrl;  // Employee Image URL (Optional)
    }
}


