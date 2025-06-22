package com.kshitij.IntervuLog.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Round {
    private String roundType;
    private List<String> questions;
    private String otherRoundType;
    // Getters and setters
}

