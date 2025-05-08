// package com.tunisys.TimeSheetPfe.services.scheduleServices;

// import com.tunisys.TimeSheetPfe.models.*;
// import com.tunisys.TimeSheetPfe.services.notificationService.NotificationService;
// import com.tunisys.TimeSheetPfe.services.taskService.TaskService;
// import com.tunisys.TimeSheetPfe.repositories.TimeSheetRepository;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.scheduling.annotation.Scheduled;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;

// import javax.annotation.PostConstruct;
// import java.time.DayOfWeek;
// import java.time.LocalDate;
// import java.time.LocalTime;
// import java.util.List;
// import java.util.Set;

// //background job 

// @Slf4j
// @Service
// @RequiredArgsConstructor
// public class TimesheetCheckService {

//    private final TaskService taskService;
//    private final TimeSheetRepository timeSheetRepository;
//    private final NotificationService notificationService;

//    @PostConstruct
//    @Transactional
//    public void init() {
//        log.info("Running initial timesheet check on startup...");
//        checkMissingTimesheets();
//    }

// //    @Scheduled(cron = "0 0 * 8-16 * MON-FRI")
//    @Transactional(readOnly = true)
//    @Scheduled(cron = "0 * * * * *")
//    public void checkMissingTimesheets() {
//        LocalDate today = LocalDate.now();
//        LocalTime now = LocalTime.now();

//        if (!isWithinWorkingHours(now, today)) {
//            log.info("Skipping timesheet check: Outside working hours or weekend.");
//            return;
//        }

//        log.info("Checking for missing timesheets on {}", today);

//        List<Task> tasksDueToday = taskService.findByDeadlineBetween(today, today); // Only today

//        for (Task task : tasksDueToday) {
//            Project project = task.getProject();
//            Set<UserModel> users = task.getEmployees();

//            for (UserModel user : users) {
//                boolean hasTimesheet = timeSheetRepository.existsByUserAndTaskAndDate(user, task, today);

//                if (!hasTimesheet) {
//                    String title = "Timesheet Reminder - Urgent";
//                    String body = String.format(
//                            "You have a task '%s' due today in project '%s' but no timesheet has been added. Please log your hours.",
//                            task.getTitle(), project != null ? project.getName() : "No Project"
//                    );
//                    String actionUrl = "/timesheet/add";

//                    log.info("Sending timesheet reminder to user {} for task {}", user.getId(), task.getId());
//                    sendNotification(user.getId(), title, body, actionUrl, NotificationType.WARNING);
//                }
//            }
//        }
//    }

//    private boolean isWithinWorkingHours(LocalTime now, LocalDate today) {
//        return !(now.isBefore(LocalTime.of(8, 0)) || now.isAfter(LocalTime.of(16, 0)) ||
//                Set.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY).contains(today.getDayOfWeek()));
//    }

//    private void sendNotification(Long userId, String title, String body, String actionUrl, NotificationType type) {
//        notificationService.createAndSendNotification(userId, title, body, actionUrl,type);
//    }
// }

