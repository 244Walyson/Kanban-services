package com.waly.notificationservice.repositories;

import com.waly.notificationservice.entities.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    @Query("""
            SELECT obj FROM UserNotification obj
            WHERE obj.receiver.email = :email
            ORDER BY obj.createdAt DESC
            """)
    List<UserNotification> findByReceiverUsername(String email);
}
