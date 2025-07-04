package com.tunisys.TimeSheetPfe.DTOs.response;

import com.tunisys.TimeSheetPfe.models.Project;
import com.tunisys.TimeSheetPfe.models.Task;
import com.tunisys.TimeSheetPfe.models.UserModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrentProjectInfo {
        private Long id;
        private String name;
        private String description;
        private LocalDate deadline;
        private User manager;
        private List<User> team;
        private Set<TaskProjectDto> allTasks;
        private Set<TaskProjectDto> ownTasks;

        @Data
        @AllArgsConstructor
        @NoArgsConstructor
        @Builder
        private static class TaskProjectDto {
                private Long id;
                private String title;
                private String description;
                private String status;
                private String priority;
                private String difficulty;
                private Set<User> employees;
                private LocalDate deadline;
                private LocalDateTime startAt;
                private LocalDateTime finishedAt;
                private Long projectId; // Project ID

                public static TaskProjectDto fromTask(Task task) {
                        return TaskProjectDto.builder()
                                        .id(task.getId())
                                        .title(task.getTitle())
                                        .description(task.getDescription())
                                        .status(task.getStatus().name())
                                        .priority(task.getPriority().name())
                                        .difficulty(task.getDifficulty().name())
                                        .employees(task.getEmployees().stream()
                                                        .map(User::fromUser)
                                                        .collect(Collectors.toSet()))
                                        .deadline(task.getDeadline())
                                        .startAt(task.getStartAt())
                                        .finishedAt(task.getFinishedAt())
                                        .projectId(task.getProject() != null ? task.getProject().getId() : null)
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

        public static CurrentProjectInfo fromProject(Project project, UserModel user) {
                return CurrentProjectInfo.builder()
                                .id(project.getId())
                                .name(project.getName())
                                .description(project.getDescription())
                                .deadline(project.getDeadline())
                                .manager(project.getManager() != null ? User.fromUser(project.getManager()) : null)
                                .allTasks(project.getTasks().stream()
                                                .map(TaskProjectDto::fromTask)
                                                .collect(Collectors.toSet()))
                                .ownTasks(user.getTasks().stream()
                                                .map(TaskProjectDto::fromTask)
                                                .collect(Collectors.toSet()))
                                .team(project.getEmployees().stream()
                                                .map(User::fromUser)
                                                .collect(Collectors.toList()))
                                .build();
        }
}
