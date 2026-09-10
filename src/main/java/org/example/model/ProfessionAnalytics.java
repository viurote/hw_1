package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ProfessionAnalytics(
        @JsonProperty("PROFESSION") String profession,
        @JsonProperty("average_salary") int averageSalary,
        @JsonProperty("average_age") int averageAge
) {}