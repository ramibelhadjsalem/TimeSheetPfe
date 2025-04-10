package com.tunisys.TimeSheetPfe.services.scheduleServices;
import com.tunisys.TimeSheetPfe.models.NotificationType;
import com.tunisys.TimeSheetPfe.models.Project;
import com.tunisys.TimeSheetPfe.models.Task;
import com.tunisys.TimeSheetPfe.models.UserModel;
import com.tunisys.TimeSheetPfe.services.notificationService.NotificationService;
import com.tunisys.TimeSheetPfe.services.taskService.TaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskDeadlineService {
    private final TaskService taskService;
    private final NotificationService notificationService;

    @PostConstruct
    @Transactional
    public void init() {
        log.info("Running initial task deadline check on startup...");
        checkTaskDeadlines();
    }

//    @Scheduled(cron = "0 0 * 8-16 * MON-FRI")
@Scheduled(cron = "0 * * * * *")
    @Transactional(readOnly = true)
    public void checkTaskDeadlines() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        if (!isWithinWorkingHours(now, today)) {
            log.info("Skipping task deadline check: Outside working hours or weekend.");
            return;
        }

        log.info("Checking for task deadlines on {}", today);

        LocalDate endDate = today.plusDays(7);
        List<Task> upcomingTasks = taskService.findByDeadlineBetween(today, endDate);

        for (Task task : upcomingTasks) {
            long daysUntilDeadline = ChronoUnit.DAYS.between(today, task.getDeadline());
            NotificationType notificationType = determineNotificationType(daysUntilDeadline);
            String deadlineMessage = getDeadlineMessage(daysUntilDeadline, task.getTitle());
            Project project = task.getProject();

            // Notify all employees assigned to the task
            Set<UserModel> employees = task.getEmployees();
            for (UserModel employee : employees) {
                String title = getNotificationTitle(notificationType, false);
                String body = String.format(
                        "%s Please complete or update the status in project '%s'.",
                        deadlineMessage, project != null ? project.getName() : "No Project"
                );
                String actionUrl = "/tasks/" + task.getId();

                log.info("Sending {} task deadline reminder to employee {} for task {} ({} days remaining)",
                        notificationType, employee.getId(), task.getId(), daysUntilDeadline);
                sendNotification(employee.getId(), title, body, actionUrl, notificationType);
            }

            // Notify the manager (if exists)
            UserModel manager = task.getManager();
            if (manager != null) {
                String title = getNotificationTitle(notificationType, true);
                String body = String.format(
                        "%s Ensure your team completes this task in project '%s'.",
                        deadlineMessage, project != null ? project.getName() : "No Project"
                );
                String actionUrl = "/tasks/" + task.getId();

                log.info("Sending {} task deadline reminder to manager {} for task {} ({} days remaining)",
                        notificationType, manager.getId(), task.getId(), daysUntilDeadline);
                sendNotification(manager.getId(), title, body, actionUrl, notificationType);
            }
        }
    }

    private boolean isWithinWorkingHours(LocalTime now, LocalDate today) {
        return !(now.isBefore(LocalTime.of(8, 0)) || now.isAfter(LocalTime.of(16, 0)) ||
                Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(today.getDayOfWeek()));
    }

    private NotificationType determineNotificationType(long daysUntilDeadline) {
        if (daysUntilDeadline <= 2) {
            return NotificationType.WARNING;
        } else {
            return NotificationType.INFO;
        }
    }

    private String getDeadlineMessage(long daysUntilDeadline, String taskTitle) {
        if (daysUntilDeadline == 0) {
            return String.format("The task '%s' has a deadline today.", taskTitle);
        } else {
            return String.format("The task '%s' will end in %d days (due on %s).",
                    taskTitle, daysUntilDeadline, taskTitle);
        }
    }

    private String getNotificationTitle(NotificationType type, boolean isManager) {
        String baseTitle = isManager ? "Task Deadline Reminder (Manager)" : "Task Deadline Reminder";
        return type == NotificationType.WARNING ? baseTitle + " - Urgent" : baseTitle;
    }

    private void sendNotification(Long userId, String title, String body, String actionUrl, NotificationType type) {
        notificationService.createAndSendNotification(userId, title, body, actionUrl,type);
    }
}
