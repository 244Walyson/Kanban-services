package com.waly.notificationservice.repositories;

import com.waly.notificationservice.entities.FcmToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FcmTokenRepository extends JpaRepository<FcmToken, Long> {

  FcmToken findByUserId(Long id);
}
