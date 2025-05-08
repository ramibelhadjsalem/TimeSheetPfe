package com.tunisys.TimeSheetPfe.controllers;

import com.tunisys.TimeSheetPfe.DTOs.request.CreateConnectionDto;
import com.tunisys.TimeSheetPfe.DTOs.response.MessageResponse;
import com.tunisys.TimeSheetPfe.models.ERole;
import com.tunisys.TimeSheetPfe.models.Notification;
import com.tunisys.TimeSheetPfe.models.NotificationType;
import com.tunisys.TimeSheetPfe.models.UserModel;
import com.tunisys.TimeSheetPfe.services.userService.UserService;
import com.tunisys.TimeSheetPfe.services.notificationService.NotificationService;
import com.tunisys.TimeSheetPfe.utils.TokenUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private TokenUtils tokenUtils;
    @Autowired
    private UserService userService;

    @GetMapping
    public List<Notification> findAllByUser() {
        Long userId = tokenUtils.ExtractId();
        return notificationService.findAllByUserId(userId);
    }

    @PostMapping("/connection")
    public ResponseEntity<String> addConnection(@Valid @RequestBody CreateConnectionDto dto) {
        notificationService.createConnection(tokenUtils.ExtractId(), dto.getFcmToken());

        return ResponseEntity.ok("success");
    }

    @DeleteMapping("/connection")
    public ResponseEntity<String> deleteConnection(@Valid @RequestBody CreateConnectionDto dto) {
        notificationService.deleteConnection(tokenUtils.ExtractId(), dto.getFcmToken());
        return ResponseEntity.ok("success");
    }

    @PostMapping("/send-test")
    public ResponseEntity<String> sendTestNotification(@RequestParam String token, @RequestParam String title,
            @RequestParam String body) {
        String response = notificationService.sendFcmNotification(token, title, body);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/seen")
    public List<Notification> markAsSeen() {
        Long userId = tokenUtils.ExtractId();
        return notificationService.markAsSeen(userId);
    }

    @PostMapping("/reclamation/project")
    public ResponseEntity<MessageResponse> sendProjectReclamation() {
        // Get the current user
        Long userId = tokenUtils.ExtractId();
        UserModel user = userService.findById(userId);

        // Check if user already has a project
        if (user.getCurrentProject() != null) {
            return ResponseEntity.badRequest().body(
                    new MessageResponse("Vous avez déjà un projet assigné."));
        }

        // Find all admin users
        List<UserModel> admins = userService.getUsersByRoles(List.of(ERole.ROLE_ADMIN));

        // Find all manager users
        List<UserModel> managers = userService.getUsersByRoles(List.of(ERole.ROLE_MANAGER));

        // Count of notifications sent
        int notificationsSent = 0;

        // Send notification to all admins
        for (UserModel admin : admins) {
            notificationService.createAndSendNotification(
                    admin.getId(),
                    "Demande d'assignation à un projet",
                    "L'employé " + user.getName() + " (" + user.getEmail() + ") demande à être assigné à un projet.",
                    "/admin/employees/" + userId,
                    NotificationType.WARNING);
            notificationsSent++;
        }

        // Send notification to all managers
        for (UserModel manager : managers) {
            notificationService.createAndSendNotification(
                    manager.getId(),
                    "Demande d'assignation à un projet",
                    "L'employé " + user.getName() + " (" + user.getEmail() + ") demande à être assigné à un projet. " +
                            "Vous pouvez l'ajouter à l'un de vos projets si vous avez besoin de personnel supplémentaire.",
                    "/manager/employees/" + userId,
                    NotificationType.INFO);
            notificationsSent++;
        }

        return ResponseEntity.ok(new MessageResponse("Votre demande a été envoyée à " + notificationsSent +
                " administrateur(s) et manager(s)."));
    }

}
