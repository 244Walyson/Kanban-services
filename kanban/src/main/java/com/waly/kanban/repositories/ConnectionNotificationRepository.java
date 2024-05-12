package com.waly.kanban.repositories;

import com.waly.kanban.entities.ConnectionNotification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConnectionNotificationRepository extends JpaRepository<ConnectionNotification, Long> {
}
