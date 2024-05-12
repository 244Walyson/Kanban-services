package com.waly.notificationservice.repositories;

import com.waly.notificationservice.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
