package com.tunisys.TimeSheetPfe.DTOs.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimeSheetResponseDto {
    private Long id;
    private String description;
    private String startTime;
    private String endTime;
    private LocalDate date;
    private User user;
    private TaskDto task;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class User {  // Must be static
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String imgUrl;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TaskDto {
        private Long id;  // Task ID
        private String title;  // Task Title
        private String description;  // Task Description
        private String status;  // Task Status
        private String priority;  // Task Priority
        private String difficulty;  // Task Difficulty
    }
}
