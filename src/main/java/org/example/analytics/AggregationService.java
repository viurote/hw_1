package org.example.analytics;

import org.example.analytics.model.Employee;
import org.example.analytics.model.ProfessionStats;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AggregationService {

    private static final Logger log = LoggerFactory.getLogger(AggregationService.class);

    /**
     * Groups employees by profession and computes average salary and age for each group.
     *
     * @param employees list of employees to aggregate
     * @return list of profession statistics, sorted alphabetically by profession name
     */
    public List<ProfessionStats> aggregate(List<Employee> employees) {
        Map<String, List<Employee>> byProfession = employees.stream()
                .collect(Collectors.groupingBy(Employee::getProfession));

        List<ProfessionStats> stats = byProfession.entrySet().stream()
                .map(entry -> {
                    String profession = entry.getKey();
                    List<Employee> group = entry.getValue();

                    int avgSalary = (int) Math.round(
                            group.stream().mapToInt(Employee::getSalary).average().orElse(0)
                    );
                    int avgAge = (int) Math.round(
                            group.stream().mapToInt(Employee::getAge).average().orElse(0)
                    );

                    log.info("Profession: {}, count: {}, avgSalary: {}, avgAge: {}",
                            profession, group.size(), avgSalary, avgAge);

                    return new ProfessionStats(profession, avgSalary, avgAge);
                })
                .sorted(Comparator.comparing(ProfessionStats::getProfession))
                .collect(Collectors.toList());

        return stats;
    }
}
