package org.example.analytics.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Employee {

    private String id;
    private String name;
    private int age;
    private int salary;

    @JsonProperty("PROFESSION")
    private String profession;
}
