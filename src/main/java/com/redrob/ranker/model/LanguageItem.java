package io.candidate.ranker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public record LanguageItem(
        @JsonProperty("language") String language,
        @JsonProperty("proficiency") String proficiency
) {}
