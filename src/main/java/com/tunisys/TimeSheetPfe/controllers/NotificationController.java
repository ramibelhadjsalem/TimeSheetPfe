package com.tunisys.TimeSheetPfe.controllers;

import com.tunisys.TimeSheetPfe.DTOs.request.CreateConnectionDto;
import com.tunisys.TimeSheetPfe.models.Notification;
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
    @Autowired private NotificationService notificationService;
    @Autowired private TokenUtils tokenUtils;


    @GetMapping
    public List<Notification>  findAllByUser(){
        Long userId = tokenUtils.ExtractId();
        return notificationService.findAllByUserId(userId);
    }

    @PostMapping("/connection")
    public ResponseEntity addConnection(@Valid @RequestBody CreateConnectionDto dto){
        notificationService.createConnection(tokenUtils.ExtractId(),dto.getFcmToken());

        return ResponseEntity.ok("success");
    }

    @DeleteMapping("/connection")
    public ResponseEntity deleteConnection(@Valid @RequestBody CreateConnectionDto dto){
        notificationService.deleteConnection(tokenUtils.ExtractId(),dto.getFcmToken());
        return ResponseEntity.ok("success");
    }

    @PostMapping("/send-test")
    public ResponseEntity sendTestNotification(@RequestParam String token, @RequestParam String title, @RequestParam String body) {
        String response = notificationService.sendFcmNotification(token, title, body);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/seen")
    public List<Notification> markAsSeen() {
        Long userId = tokenUtils.ExtractId();
        return notificationService.markAsSeen(userId);
    }

}
