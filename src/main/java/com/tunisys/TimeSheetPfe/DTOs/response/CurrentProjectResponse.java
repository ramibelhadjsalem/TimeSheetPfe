package com.tunisys.TimeSheetPfe.DTOs.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;
import java.util.stream.Collectors;

import com.tunisys.TimeSheetPfe.models.Project;
import com.tunisys.TimeSheetPfe.models.Task;
import com.tunisys.TimeSheetPfe.models.UserModel;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrentProjectResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDate deadline;
    private CurrentProjectResponse.User manager;
    private Set<CurrentProjectResponse.User> employees;

    public static CurrentProjectResponse fromProject(Project project) {
        return CurrentProjectResponse.builder()
                .id(project.getId())
                .name(project.getName())
                .description(project.getDescription())
                .deadline(project.getDeadline())
                .manager(project.getManager() != null ? User.fromUser(project.getManager()) : null)
                .employees(project.getEmployees().stream()
                        .map(User::fromUser)
                        .collect(Collectors.toSet()))
                .build();
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    private static class TaskProjectDto {
        private Long id; // Task ID
        private String title; // Task Title
        private String description; // Task Description
        private String status; // Task Status
        private String priority; // Task Priority
        private String difficulty;
        private Long projectId; // Project ID
        private Set<CurrentProjectResponse.User> employees;

        public static TaskProjectDto fromTask(Task task) {
            return TaskProjectDto.builder()
                    .id(task.getId())
                    .title(task.getTitle())
                    .description(task.getDescription())
                    .status(task.getStatus().name())
                    .priority(task.getPriority().name())
                    .difficulty(task.getDifficulty().name())
                    .projectId(task.getProject() != null ? task.getProject().getId() : null)
                    .employees(task.getEmployees().stream()
                            .map(CurrentProjectResponse.User::fromUser)
                            .collect(Collectors.toSet()))
                    .build();
        }
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    private static class User {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String imgUrl;
        private Long currentProject;

        public static User fromUser(UserModel user) {
            return User.builder()
                    .id(user.getId())
                    .name(user.getName())
                    .email(user.getEmail())
                    .phone(user.getPhone())
                    .imgUrl(user.getImgUrl())
                    .currentProject(user.getCurrentProject() != null
                            ? user.getCurrentProject().getId()
                            : null)
                    .build();
        }
    }
}
