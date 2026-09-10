package org.example.service;

import org.example.model.EmployeeRecord;
import org.example.model.Profession;
import org.example.model.ProfessionAnalytics;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AnalyticsService {

    public List<ProfessionAnalytics> aggregate(List<EmployeeRecord> employees) {
        if (employees == null || employees.isEmpty()) {
            return List.of();
        }

        Map<Profession, List<EmployeeRecord>> byProfession = employees.stream()
                .filter(e -> e.profession() != null)
                .collect(Collectors.groupingBy(EmployeeRecord::profession));

        return byProfession.entrySet().stream()
                .map(entry -> {
                    Profession profession = entry.getKey();
                    List<EmployeeRecord> group = entry.getValue();

                    double avgSalary = group.stream()
                            .mapToInt(EmployeeRecord::salary)
                            .average()
                            .orElse(0.0);

                    double avgAge = group.stream()
                            .mapToInt(EmployeeRecord::age)
                            .average()
                            .orElse(0.0);

                    return new ProfessionAnalytics(
                            profession,
                            (int) Math.round(avgSalary),
                            (int) Math.round(avgAge)
                    );
                })
                .toList();
    }
}