package io.candidate.ranker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonIgnoreProperties(ignoreUnknown = true)
public record CertificationItem(
        @JsonProperty("name") String name,
        @JsonProperty("issuer") String issuer,
        @JsonProperty("year") int year
) {}