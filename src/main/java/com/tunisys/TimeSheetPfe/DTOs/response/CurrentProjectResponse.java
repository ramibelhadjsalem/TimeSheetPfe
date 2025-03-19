package com.tunisys.TimeSheetPfe.DTOs.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrentProjectResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDate deadline;
    private CurrentProjectResponse.User manager;
    private Set<CurrentProjectResponse.User> employees ;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    private static class TaskProjectDto {
        private Long id;  // Task ID
        private String title;  // Task Title
        private String description;  // Task Description
        private String status;  // Task Status
        private String priority;  // Task Priority
        private String difficulty;
        private Set<CurrentProjectResponse.User> employees ;
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
