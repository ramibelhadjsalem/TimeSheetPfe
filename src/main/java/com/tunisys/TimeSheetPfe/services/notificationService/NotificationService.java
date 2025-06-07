package com.tunisys.TimeSheetPfe.services.notificationService;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.tunisys.TimeSheetPfe.models.Connection;
import com.tunisys.TimeSheetPfe.models.Notification;
import com.tunisys.TimeSheetPfe.models.NotificationType;
import com.tunisys.TimeSheetPfe.models.UserModel;
import com.tunisys.TimeSheetPfe.repositories.ConnectionRepository;
import com.tunisys.TimeSheetPfe.repositories.NotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
@Slf4j
@Service
public class NotificationService {

    @Autowired private NotificationRepository notificationRepository;
    @Autowired private ConnectionRepository connectionRepository;
    public List<Notification> findAll(){

        return notificationRepository.findAll();
    }
    public List<Notification> findAllByUserId(Long userId){

        return notificationRepository.findByUserId(userId);
    }
    // Create and send a notification to a specific user
    public void createAndSendNotification(Long userId, String title, String body, String actionUrl , NotificationType type) {
        // Create a new notification
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setBody(body);
        notification.setActionUrl(actionUrl);
        notification.setUserId(userId);
        notification.setReceived(false);
        notification.setType(type);

        // Save the notification to the database
        notificationRepository.save(notification);

        // Get all FCM tokens for the user
        List<Connection> connections = connectionRepository.findByUserId(userId);

        if (connections.isEmpty()) {
            // If no tokens are found, just save to DB with received = false (already done)
            return;
        }

        // Send notification to all tokens
        for (Connection connection : connections) {
            sendFcmNotification(connection.getToken(), title, body);
        }
    }

    public String sendFcmNotification(String token, String title, String body) {
        try {
            Message message = Message.builder()
                    .setToken(token)
                    .setNotification(com.google.firebase.messaging.Notification.builder()
                            .setTitle(title)
                            .setBody(body)
                            .build())
                    .build();

            return FirebaseMessaging.getInstance().send(message);
        } catch (Exception e) {
            e.printStackTrace();
            return "Error sending notification";
        }
    }
    public void createConnection(Long userId, String fcmToken) {
        if (connectionRepository.existsByUserIdAndToken(userId, fcmToken)) {
            log.info("FCM token already exists for user: {}", userId);
            return;
        }

        connectionRepository.save(new Connection(userId, fcmToken));
        log.info("New FCM connection saved for user: {}", userId);
    }

    public void deleteConnection(Long userId, String fcmToken) {
        if (!connectionRepository.existsByUserIdAndToken(userId, fcmToken)) {
            log.warn("FCM token not found for user: {}", userId);
            return;
        }

        connectionRepository.deleteByUserIdAndToken(userId, fcmToken);
        log.info("FCM connection removed for user: {}", userId);
    }

    public List<Notification> markAsSeen(Long userId) {
        // 1. Fetch all notifications for the user
        List<Notification> notifications = notificationRepository.findByUserId(userId);

        // 2. Update each notification's 'received' field to true
        for (Notification notification : notifications) {
            notification.setReceived(true);
        }

        // 3. Save all updated notifications
        notificationRepository.saveAll(notifications);

        // 4. Return updated notifications, ordered by createdAt descending
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void sendNotification(UserModel user, String title, String body, NotificationType type) {
        createAndSendNotification(user.getId(), title, body, null, type);
    }
}
