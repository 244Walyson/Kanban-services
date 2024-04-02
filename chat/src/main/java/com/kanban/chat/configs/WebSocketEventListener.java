package com.kanban.chat.configs;

import com.kanban.chat.models.entities.ChatMessageEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.springframework.web.socket.messaging.SessionConnectEvent;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;
import org.springframework.web.socket.messaging.SessionSubscribeEvent;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class WebSocketEventListener {

    private final SimpUserRegistry userRegistry;
    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public WebSocketEventListener(SimpUserRegistry userRegistry) {
        this.userRegistry = userRegistry;
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionConnectedEvent event) throws InterruptedException {

        Message<byte[]> message = event.getMessage();
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        String sessionId = accessor.getSessionId();
        String authorizationHeader = accessor.getLogin();
        log.info("Connected: " + sessionId + " - " + authorizationHeader);

    }

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String clientId = accessor.getSessionId();
        // Lidar com a desconexão do cliente, se necessário
    }

    @EventListener
    public void handleWebSocketConnectListener(SessionDisconnectEvent event) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(event.getMessage());
        String clientId = accessor.getSessionId();
    }

    @EventListener
    public void handleSubscribeEvent(SessionSubscribeEvent event) throws InterruptedException {

    }
}