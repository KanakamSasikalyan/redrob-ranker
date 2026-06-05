package io.candidate.ranker.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record Candidate(
        @JsonProperty("candidate_id") String candidateId,
        @JsonProperty("profile") Profile profile,
        @JsonProperty("career_history") List<CareerHistoryItem> careerHistory,
        @JsonProperty("education") List<EducationItem> education,
        @JsonProperty("skills") List<SkillItem> skills,
        @JsonProperty("certifications") List<CertificationItem> certifications,
        @JsonProperty("languages") List<LanguageItem> languages,
        @JsonProperty("redrob_signals") RedrobSignals redrobSignals
) {}