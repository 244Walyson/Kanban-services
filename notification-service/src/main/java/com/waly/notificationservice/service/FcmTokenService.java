package com.waly.notificationservice.service;

import com.waly.notificationservice.dtos.FcmTokenDTO;
import com.waly.notificationservice.entities.FcmToken;
import com.waly.notificationservice.repositories.FcmTokenRepository;
import com.waly.notificationservice.utils.CustomUserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FcmTokenService {

  private final FcmTokenRepository fcmTokenRepository;
  private final CustomUserUtil customUserUtil;

  public FcmTokenService(FcmTokenRepository fcmTokenRepository, CustomUserUtil customUserUtil) {
    this.fcmTokenRepository = fcmTokenRepository;
    this.customUserUtil = customUserUtil;
  }

  public void saveFcmToken(FcmTokenDTO fcmTokenDTO) {
    Long userId = customUserUtil.getLoggedUserId();
    FcmToken fcmToken = new FcmToken();
    fcmToken.setUserId(userId);
    fcmToken.setToken(fcmTokenDTO.getToken());
    fcmTokenRepository.save(fcmToken);
  }
}
