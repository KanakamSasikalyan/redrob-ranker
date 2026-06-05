package com.redrob.ranker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public record EducationItem(
        @JsonProperty("institution") String institution,
        @JsonProperty("degree") String degree,
        @JsonProperty("field_of_study") String fieldOfStudy,
        @JsonProperty("start_year") int startYear,
        @JsonProperty("end_year") int endYear,
        @JsonProperty("grade") String grade,
        @JsonProperty("tier") String tier
) {}