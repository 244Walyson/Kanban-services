package com.waly.notificationservice.repositories;

import com.waly.notificationservice.entities.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {

    @Query("""
            SELECT obj FROM UserNotification obj
            WHERE obj.receiver.email = :email
            AND obj.accepted = false
            ORDER BY obj.createdAt DESC
            """)
    List<UserNotification> findByReceiverUsername(String email);

    @Query("""
            SELECT obj FROM UserNotification obj
            WHERE (obj.receiver.nickname = :nickname AND obj.sender.nickname = :nickname1)
            OR (obj.receiver.nickname = :nickname1 AND obj.sender.nickname = :nickname)
            """)
    Optional<UserNotification> findByReceiverAndSenderNickname(String nickname, String nickname1);
}
