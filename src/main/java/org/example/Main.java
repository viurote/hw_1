package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        String content = """
                File content
                """;

        byte[] fileBytes = content.getBytes(StandardCharsets.UTF_8);

        EmailNotificationService emailNotificationService = new EmailNotificationService("TOKEN!");
        emailNotificationService.sendEmail("isicjua@gmail.com", "body text", fileBytes);
    }

}