package com.waly.kanban.services;

import com.waly.kanban.repositories.ConnectionNotificationRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class ConnectionNotificationService {

    @Autowired
    private ConnectionNotificationRepository repository;


}
