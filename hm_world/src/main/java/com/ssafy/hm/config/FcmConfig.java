package com.ssafy.hm.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;

@Configuration
public class FcmConfig {

    @Value("${fcm.credentials.path:}")
    private String credentialsPath;

    @Bean
    public FirebaseApp firebaseApp() throws IOException {
        if (!FirebaseApp.getApps().isEmpty()) {
            return FirebaseApp.getInstance();
        }

        InputStream in = null;
        try {
            if (credentialsPath != null && !credentialsPath.isBlank()) {
                if (credentialsPath.startsWith("classpath:")) {
                    String cp = credentialsPath.replace("classpath:", "");
                    in = new ClassPathResource(cp).getInputStream();
                } else {
                    in = new FileInputStream(credentialsPath);
                }
            } else {
                String envPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
                if (envPath != null && !envPath.isBlank()) {
                    in = new FileInputStream(envPath);
                } else {
                    throw new IllegalStateException("FCM credentials not configured.");
                }
            }

            FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(in))
                .build();

            return FirebaseApp.initializeApp(options);
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    @Bean
    public FirebaseMessaging firebaseMessaging(FirebaseApp firebaseApp) {
        return FirebaseMessaging.getInstance(firebaseApp);
    }
}
