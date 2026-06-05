package io.candidate.ranker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public record Profile(
        @JsonProperty("anonymized_name") String anonymizedName,
        @JsonProperty("headline") String headline,
        @JsonProperty("summary") String summary,
        @JsonProperty("location") String location,
        @JsonProperty("country") String country,
        @JsonProperty("years_of_experience") double yearsOfExperience,
        @JsonProperty("current_title") String currentTitle,
        @JsonProperty("current_company") String currentCompany,
        @JsonProperty("current_company_size") String currentCompanySize,
        @JsonProperty("current_industry") String currentIndustry
) {}