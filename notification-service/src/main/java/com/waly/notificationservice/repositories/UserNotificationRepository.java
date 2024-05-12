package com.waly.notificationservice.repositories;

import com.waly.notificationservice.entities.UserNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserNotificationRepository extends JpaRepository<UserNotification, Long> {
}
