package com.tunisys.TimeSheetPfe.repositories;

import com.tunisys.TimeSheetPfe.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification,Long> {

    @Query("SELECT n FROM Notification n WHERE n.userId = ?1 ORDER BY n.createdAt DESC")
    List<Notification> findByUserId(Long userId);
}
