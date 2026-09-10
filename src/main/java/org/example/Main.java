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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final ObjectMapper MAPPER = new ObjectMapper(); //.enable(SerializationFeature.INDENT_OUTPUT)
    private static final String TARGET_EMAIL = "dmitriy.kst2@gmail.com";

    public static void main(String[] args) {
        if (args.length < 3) {
            log.error("Parametres: <input directory> <output directory> <TOKEN>");
            return;
        }

        Path inputDir = Path.of(args[0]); // путь к файлам .txt
        Path outputDir = Path.of(args[1]); // путь к отчетам
        String token = args[2];

        try {
            Files.createDirectories(outputDir);
            EmailNotificationService emailService = new EmailNotificationService(token);

            try (Stream<Path> files = Files.list(inputDir)) {
                List<Path> txtFiles = files
                        .filter(Files::isRegularFile)
                        .filter(p -> p.getFileName().toString().endsWith(".txt"))
                        .toList();

                for (Path file : txtFiles) {
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

            // 1. Читаем JSON https://www.baeldung.com/jackson-object-mapper-tutorial#4-creating-a-java-list-from-a-json-array-string
            List<Employee> employees = MAPPER.readValue(file.toFile(), new TypeReference<>() {});

            Map<String, List<Employee>> employeesByProfession = new HashMap<>();

            for (Employee emp : employees) {
                if (emp.profession() == null) {
                    continue;
                }

                if (!employeesByProfession.containsKey(emp.profession())) {
                    employeesByProfession.put(emp.profession(), new ArrayList<>());
                }

                employeesByProfession.get(emp.profession()).add(emp);
            }

            List<ProfessionAnalytics> report = new ArrayList<>();

            for (Map.Entry<String, List<Employee>> entry : employeesByProfession.entrySet()) {
                String profession = entry.getKey();
                List<Employee> group = entry.getValue();

                long totalSalary = 0;
                long totalAge = 0;

                for (Employee emp : group) {
                    totalSalary += emp.salary();
                    totalAge += emp.age();
                }

                int count = group.size();
                int avgSalary = Math.round((float) totalSalary / count);
                int avgAge = Math.round((float) totalAge / count);

                report.add(new ProfessionAnalytics(profession, avgSalary, avgAge));
            }

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