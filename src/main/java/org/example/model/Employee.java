package org.example.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public record Employee(
        UUID id,
        String name,
        int age,
        int salary,
        @JsonProperty("PROFESSION") String profession
) {}