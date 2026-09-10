package org.example;

import org.example.model.EmployeeRecord;
import org.example.model.ProfessionAnalytics;
import org.example.service.AnalyticsService;
import org.example.service.DataParserService;
import org.example.service.EmailNotificationService;
import org.example.service.FileTrackerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);
    private static final String TARGET_EMAIL = "dmitriy.kst2@gmail.com";

    public static void main(String[] args) {
        if (args.length < 3) {
            log.error("Usage: java -jar app.jar <inputDir> <outputDir> <mailToken>");
            System.exit(1);
        }

        Path inputDir = Path.of(args[0]);
        Path outputDir = Path.of(args[1]);
        String mailToken = args[2];

        try {
            if (!Files.exists(inputDir) || !Files.isDirectory(inputDir)) { // Если указанный путь не существует ИЛИ если по пути находится не папка
                log.error("Input directory does not exist: {}", inputDir);
                return;
            }
            Files.createDirectories(outputDir); //если папка есть просто false

            FileTrackerService trackerService = new FileTrackerService(outputDir);
            DataParserService parserService = new DataParserService();
            AnalyticsService analyticsService = new AnalyticsService();
            EmailNotificationService emailService = new EmailNotificationService(mailToken);

            List<Path> unprocessedFiles = trackerService.findUnprocessedFiles(inputDir);
            log.info("Found {} new file(s) for processing.", unprocessedFiles.size());

            for (Path rawFile : unprocessedFiles) {
                processFile(rawFile, outputDir, trackerService, parserService, analyticsService, emailService);
            }

        } catch (Exception e) {
            log.error("Fatal error during batch processing", e);
        }
    }

    private static void processFile(
            Path rawFile,
            Path outputDir,
            FileTrackerService trackerService,
            DataParserService parserService,
            AnalyticsService analyticsService,
            EmailNotificationService emailService
    ) {
        String fileName = rawFile.getFileName().toString();
        log.info("Processing file: {}", fileName);

        try {
            // 1. Читаем исходные данные
            List<EmployeeRecord> employees = parserService.readEmployees(rawFile);

            // 2. Агрегируем по профессиям
            List<ProfessionAnalytics> analytics = analyticsService.aggregate(employees);

            // 3. Записываем отчет на диск
            Path reportPath = outputDir.resolve("analytics_" + fileName);
            byte[] reportBytes = parserService.writeReport(reportPath, analytics);

            // 4. Формируем тело письма и отправляем
            String processedAt = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
            String emailBody = String.format("Processed file: %s%nProcessed at: %s", fileName, processedAt);

            emailService.sendEmail(TARGET_EMAIL, emailBody, reportBytes);
            log.info("Email notification sent for: {}", fileName);

            // 5. Фиксируем успешную обработку
            trackerService.markAsProcessed(rawFile);

        } catch (Exception e) {
            log.error("Error processing file: {}", fileName, e);
        }
    }
}