package com.redrob.ranker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public record SalaryRange(
        @JsonProperty("min") double min,
        @JsonProperty("max") double max
) {}
