package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.model.Employee;
import org.example.model.ProfessionAnalytics;
import org.example.service.EmailNotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final ObjectMapper MAPPER = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
    private static final String TARGET_EMAIL = "dmitriy.kst2@gmail.com";

    public static void main(String[] args) {
        if (args.length < 3) {
            log.error("Укажите параметры: <папка с файлами> <папка для отчетов> <токен>");
            return;
        }

        Path inputDir = Path.of(args[0]);
        Path outputDir = Path.of(args[1]);
        String token = args[2];

        try {
            Files.createDirectories(outputDir);
            EmailNotificationService emailService = new EmailNotificationService(token);

            try (Stream<Path> files = Files.list(inputDir)) {
                List<Path> jsonFiles = files
                        .filter(Files::isRegularFile)
                        .filter(p -> p.getFileName().toString().endsWith(".json"))
                        .toList();

                for (Path file : jsonFiles) {
                    Path reportFile = outputDir.resolve("analytics_" + file.getFileName());

                    if (Files.exists(reportFile)) {
                        log.info("Файл {} уже был обработан ранее, пропускаем.", file.getFileName());
                        continue;
                    }

                    processAndSend(file, reportFile, emailService);
                }
            }
        } catch (Exception e) {
            log.error("Критическая ошибка работы программы", e);
        }
    }

    private static void processAndSend(Path file, Path reportFile, EmailNotificationService emailService) {
        try {
            log.info("Обрабатываем: {}", file.getFileName());

            List<Employee> employees = MAPPER.readValue(file.toFile(), new TypeReference<>() {});

            Map<String, List<Employee>> byProfession = employees.stream()
                    .filter(e -> e.profession() != null)
                    .collect(Collectors.groupingBy(Employee::profession));

            List<ProfessionAnalytics> report = byProfession.entrySet().stream()
                    .map(entry -> {
                        List<Employee> group = entry.getValue();
                        int avgSalary = (int) Math.round(group.stream().mapToInt(Employee::salary).average().orElse(0));
                        int avgAge = (int) Math.round(group.stream().mapToInt(Employee::age).average().orElse(0));

                        return new ProfessionAnalytics(entry.getKey(), avgSalary, avgAge);
                    })
                    .toList();

            byte[] reportBytes = MAPPER.writeValueAsBytes(report);

            String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String body = "Обработан файл: " + file.getFileName() + "\nВремя обработки: " + time;
            emailService.sendEmail(TARGET_EMAIL, body, reportBytes);

            Files.write(reportFile, reportBytes);

            log.info("Успешно обработан и отправлен: {}", file.getFileName());

        } catch (Exception e) {
            log.error("Ошибка при обработке файла: {}", file.getFileName(), e);
        }
    }
}