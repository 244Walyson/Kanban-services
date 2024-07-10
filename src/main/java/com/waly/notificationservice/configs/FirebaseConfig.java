package com.waly.notificationservice.configs;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Slf4j
@Configuration
public class FirebaseConfig {

    @Value("${app.firebase-configuration-json}")
    private String firebaseConfigJson;

    @Bean
    public FirebaseMessaging firebaseMessaging() throws IOException {
        ByteArrayInputStream serviceAccountStream = new ByteArrayInputStream(firebaseConfigJson.getBytes(StandardCharsets.UTF_8));
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccountStream))
                .build();
        if (FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
            log.info("Firebase application initialized");
        }
        return FirebaseMessaging.getInstance();
    }
}
