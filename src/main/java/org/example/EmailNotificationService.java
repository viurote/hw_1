package org.example;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class EmailNotificationService {

    private String authToken;

    public EmailNotificationService(String authToken) {
        this.authToken = authToken;
    }

    public void sendEmail(String toEmail, String content, byte[] reportBytes) {

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        headers.set(
                "Authorization",
                authToken
        );

        String base64Content = Base64.getEncoder()
                .encodeToString(reportBytes);

        Map<String, Object> attachment = new LinkedHashMap<>();
        attachment.put("ContentType", "text/plain");
        attachment.put("Filename", "hi.txt");
        attachment.put("Base64Content", base64Content);

        Map<String, Object> from = new LinkedHashMap<>();
        from.put("Email", "isicju@gmail.com");
        from.put("Name", "Automated bot");

        Map<String, Object> to = new LinkedHashMap<>();
        to.put("Email", toEmail);

        Map<String, Object> message = new LinkedHashMap<>();
        message.put("From", from);
        message.put("To", List.of(to));
        message.put("Subject", "Results of analytics");
        message.put("TextPart", content);
        message.put("Attachments", List.of(attachment));

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("Messages", List.of(message));

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(
                "https://api.mailjet.com/v3.1/send",
                request,
                String.class
        );

        System.out.println(response.getBody());
        System.out.println(response.getStatusCode());
    }


}