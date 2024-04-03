package com.kanban.chat.configs;

import com.kanban.chat.services.AuthService;
import com.kanban.chat.utils.CustomUserUtil;
import com.mongodb.lang.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.SessionAttribute;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/connect")
                .addInterceptors(new WebSocketInterceptor())
                .setAllowedOriginPatterns("*");
    }

    @Override
    public void configureMessageBroker(org.springframework.messaging.simp.config.MessageBrokerRegistry registry) {
        registry.enableSimpleBroker("/user", "/chat");
        registry.setApplicationDestinationPrefixes("/app", "/user", "/chat");
        registry.setUserDestinationPrefix("/user");
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new Interceptor());
    }
    @Override
    public void configureClientOutboundChannel(ChannelRegistration registration) {
        //registration.interceptors(new Interceptor());
    }

    private static class Interceptor implements ChannelInterceptor {

        private String CONNECT = "CONNECT";
        @Override
        public Message<?> preSend(Message<?> message, MessageChannel channel) {

            String simpMessageType = message.getHeaders().get("simpMessageType").toString();

            if(!simpMessageType.equals(CONNECT)){
                log.info("CONNECTED");
                String nickName = message.getHeaders().get("simpSessionAttributes").toString().split("=")[1].split("}")[0];
                log.info("NickName: " + nickName);

                log.info("PASSANDO NO HANDLERRRRRRRRR");
                String[] chatRoomId = message.getHeaders().get("nativeHeaders").toString().split("/");
                chatRoomId = chatRoomId[chatRoomId.length - 1].split("]");

                Authentication authenticationAfter = SecurityContextHolder.getContext().getAuthentication();

                System.out.println("Authentication After: " + authenticationAfter);
                log.info("THREAD HANDLER " + Thread.currentThread().getId());
                SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(nickName, chatRoomId[0]));
                 authenticationAfter = SecurityContextHolder.getContext().getAuthentication();

                System.out.println("Authentication setada: " + authenticationAfter);

            }
            return message;
        }
    }
}
