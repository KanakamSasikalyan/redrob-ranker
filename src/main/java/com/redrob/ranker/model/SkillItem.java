package com.redrob.ranker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public record SkillItem(
        @JsonProperty("name") String name,
        @JsonProperty("proficiency") String proficiency,
        @JsonProperty("endorsements") int endorsements,
        @JsonProperty("duration_months") Integer durationMonths
) {}