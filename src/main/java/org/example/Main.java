package org.example;

import org.example.analytics.AnalyticsRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;

public class Main {

    private static final Logger log = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.err.println("Usage: java -jar analytics.jar <inputDir> <outputDir> <emailToken> <toEmail>");
            System.exit(1);
        }

        Path inputDir  = Path.of(args[0]);
        Path outputDir = Path.of(args[1]);
        String emailToken = args[2];
        String toEmail    = args[3];

        log.info("Starting analytics. inputDir={}, outputDir={}, toEmail={}", inputDir, outputDir, toEmail);

        AnalyticsRunner runner = new AnalyticsRunner(inputDir, outputDir, emailToken, toEmail);
        runner.run();
    }
}