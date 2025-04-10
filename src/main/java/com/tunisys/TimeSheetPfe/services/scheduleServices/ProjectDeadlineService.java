package com.tunisys.TimeSheetPfe.services.scheduleServices;

import com.tunisys.TimeSheetPfe.models.NotificationType;
import com.tunisys.TimeSheetPfe.models.Project;
import com.tunisys.TimeSheetPfe.models.UserModel;
import com.tunisys.TimeSheetPfe.services.notificationService.NotificationService;
import com.tunisys.TimeSheetPfe.services.projectService.ProjectService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProjectDeadlineService {

    private final ProjectService projectService;
    private final NotificationService notificationService;

    @Scheduled(cron = "0 0 * 8-16 * MON-FRI")
//    @Scheduled(cron = "0 * * * * *")
    public void checkProjectDeadlines() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (!isWithinWorkingHours(now, today)) {
            log.info("Skipping project deadline check: Outside working hours or weekend.");
            return;
        }

        log.info("Checking for project deadlines on {}", today);

        // Calculate the date range: today to 7 days from now
        LocalDate endDate = today.plusDays(7);
        List<Project> upcomingProjects = projectService.findByDeadlineBetween(today, endDate);

        for (Project project : upcomingProjects) {
            long daysUntilDeadline = ChronoUnit.DAYS.between(today, project.getDeadline());
            NotificationType notificationType = determineNotificationType(daysUntilDeadline);
            String deadlineMessage = getDeadlineMessage(daysUntilDeadline, project.getName(),project);

            // Notify all employees
            Set<UserModel> employees = project.getEmployees();
            for (UserModel employee : employees) {
                String title = getNotificationTitle(notificationType, false);
                String body = String.format(
                        "%s Please ensure all tasks are on track.",
                        deadlineMessage
                );
                String actionUrl = "/projects/" + project.getId();

                log.info("Sending {} project deadline reminder to employee {} for project {} ({} days remaining)",
                        notificationType, employee.getId(), project.getId(), daysUntilDeadline);
                notificationService.createAndSendNotification(employee.getId(), title, body, actionUrl,
                        notificationType);
            }

            // Notify the manager (if exists)
            UserModel manager = project.getManager();
            if (manager != null) {
                String title = getNotificationTitle(notificationType, true);
                String body = String.format(
                        "%s Ensure all team members are completing their tasks.",
                        deadlineMessage
                );
                String actionUrl = "/projects/" + project.getId();

                log.info("Sending {} project deadline reminder to manager {} for project {} ({} days remaining)",
                        notificationType, manager.getId(), project.getId(), daysUntilDeadline);
                notificationService.createAndSendNotification(manager.getId(), title, body, actionUrl,
                        notificationType);
            }
        }
    }

    private boolean isWithinWorkingHours(LocalTime now, LocalDate today) {
        return !(now.isBefore(LocalTime.of(8, 0)) || now.isAfter(LocalTime.of(16, 0)) ||
                Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(today.getDayOfWeek()));
    }

    private NotificationType determineNotificationType(long daysUntilDeadline) {
        if (daysUntilDeadline <= 2) {
            return NotificationType.WARNING; // Last 3 days (0, 1, 2 days remaining)
        } else {
            return NotificationType.INFO; // 7 to 3 days remaining
        }
    }

    private String getDeadlineMessage(long daysUntilDeadline, String projectName,Project project) {
        if (daysUntilDeadline == 0) {
            return String.format("The project '%s' has a deadline today.", projectName);
        } else {
            return String.format("The project '%s' will end in %d days (due on %s).",
                    projectName, daysUntilDeadline, project.getDeadline().toString());
        }
    }

    private String getNotificationTitle(NotificationType type, boolean isManager) {
        String baseTitle = isManager ? "Project Deadline Reminder (Manager)" : "Project Deadline Reminder";
        return type == NotificationType.WARNING ? baseTitle + " - Urgent" : baseTitle;
    }


}