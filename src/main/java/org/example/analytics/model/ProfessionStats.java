package org.example.analytics.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfessionStats {

    @JsonProperty("PROFESSION")
    private String profession;

    @JsonProperty("average_salary")
    private int averageSalary;

    @JsonProperty("average_age")
    private int averageAge;
}
