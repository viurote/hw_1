package org.example.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.example.model.EmployeeRecord;
import org.example.model.ProfessionAnalytics;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class DataParserService {

    private final ObjectMapper objectMapper;

    public DataParserService() {
        this.objectMapper = new ObjectMapper()
                .enable(SerializationFeature.INDENT_OUTPUT);
    }

    public List<EmployeeRecord> readEmployees(Path filePath) throws IOException {
        return objectMapper.readValue(filePath.toFile(), new TypeReference<>() {});
    }

    public byte[] writeReport(Path outputPath, List<ProfessionAnalytics> analytics) throws IOException {
        byte[] bytes = objectMapper.writeValueAsBytes(analytics);
        Files.write(outputPath, bytes);
        return bytes;
    }
}