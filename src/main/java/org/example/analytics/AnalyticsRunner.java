package org.example.analytics;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.EmailNotificationService;
import org.example.analytics.model.Employee;
import org.example.analytics.model.ProfessionStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Stream;

public class AnalyticsRunner {

    private static final Logger log = LoggerFactory.getLogger(AnalyticsRunner.class);
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT);
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final Path inputDir;
    private final Path outputDir;
    private final EmailNotificationService emailService;
    private final String toEmail;
    private final DataParser dataParser;
    private final AggregationService aggregationService;
    private final FileTracker fileTracker;

    public AnalyticsRunner(Path inputDir, Path outputDir,
                           String emailToken, String toEmail) throws IOException {
        this.inputDir = inputDir;
        this.outputDir = outputDir;
        this.emailService = new EmailNotificationService(emailToken);
        this.toEmail = toEmail;
        this.dataParser = new DataParser();
        this.aggregationService = new AggregationService();

        Files.createDirectories(outputDir);
        this.fileTracker = new FileTracker(outputDir);
    }

    /**
     * Scans the input directory for new (unprocessed) files, aggregates data by profession,
     * saves results and sends an email notification for each new file.
     */
    public void run() throws IOException {
        log.info("Scanning input directory: {}", inputDir);

        List<Path> inputFiles;
        try (Stream<Path> stream = Files.list(inputDir)) {
            inputFiles = stream
                    .filter(Files::isRegularFile)
                    .filter(p -> !p.getFileName().toString().startsWith("."))
                    .sorted()
                    .toList();
        }

        if (inputFiles.isEmpty()) {
            log.info("No files found in input directory.");
            return;
        }

        int newFilesCount = 0;
        for (Path inputFile : inputFiles) {
            String fileName = inputFile.getFileName().toString();

            if (fileTracker.isAlreadyProcessed(fileName)) {
                log.info("Skipping already processed file: {}", fileName);
                continue;
            }

            log.info("Processing new file: {}", fileName);
            processFile(inputFile);
            newFilesCount++;
        }

        if (newFilesCount == 0) {
            log.info("No new files to process.");
        } else {
            log.info("Done. Processed {} new file(s).", newFilesCount);
        }
    }

    private void processFile(Path inputFile) throws IOException {
        String fileName = inputFile.getFileName().toString();
        String analysisTime = LocalDateTime.now().format(FORMATTER);

        // Parse and aggregate
        List<Employee> employees = dataParser.parse(inputFile);
        List<ProfessionStats> stats = aggregationService.aggregate(employees);

        // Save result JSON
        String resultFileName = fileName.replaceFirst("(\\.[^.]+)?$", "_analytics.json");
        Path resultFile = outputDir.resolve(resultFileName);
        objectMapper.writeValue(resultFile.toFile(), stats);
        log.info("Analytics result saved to: {}", resultFile);

        // Build email body
        String emailBody = String.format(
                "Analytics Report%n%n" +
                "Analyzed file : %s%n" +
                "Analysis time : %s%n%n" +
                "Results are attached.",
                fileName, analysisTime
        );

        // Read result bytes for attachment
        byte[] resultBytes = Files.readAllBytes(resultFile);

        // Send email
        log.info("Sending email to {} for file {}", toEmail, fileName);
        emailService.sendEmail(toEmail, emailBody, resultBytes, resultFileName);

        // Mark as processed only after successful email
        fileTracker.markAsProcessed(fileName);
    }
}
