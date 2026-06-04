package io.candidate.ranker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
@JsonIgnoreProperties(ignoreUnknown = true)
public record CareerHistoryItem(
        @JsonProperty("company") String company,
        @JsonProperty("title") String title,
        @JsonProperty("start_date") LocalDate startDate,
        @JsonProperty("end_date") LocalDate endDate,
        @JsonProperty("duration_months") int durationMonths,
        @JsonProperty("is_current") boolean isCurrent,
        @JsonProperty("industry") String industry,
        @JsonProperty("company_size") String companySize,
        @JsonProperty("description") String description
) {}
