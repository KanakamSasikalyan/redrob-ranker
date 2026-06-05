package com.redrob.ranker.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.util.Map;
@JsonIgnoreProperties(ignoreUnknown = true)
public record RedrobSignals(
        @JsonProperty("profile_completeness_score") double profileCompletenessScore,
        @JsonProperty("signup_date") LocalDate signupDate,
        @JsonProperty("last_active_date") LocalDate lastActiveDate,
        @JsonProperty("open_to_work_flag") boolean openToWorkFlag,
        @JsonProperty("profile_views_received_30d") int profileViewsReceived30d,
        @JsonProperty("applications_submitted_30d") int applicationsSubmitted30d,
        @JsonProperty("recruiter_response_rate") double recruiterResponseRate,
        @JsonProperty("avg_response_time_hours") double avgResponseTimeHours,
        @JsonProperty("skill_assessment_scores") Map<String, Double> skillAssessmentScores,
        @JsonProperty("connection_count") int connectionCount,
        @JsonProperty("endorsements_received") int endorsementsReceived,
        @JsonProperty("notice_period_days") int noticePeriodDays,
        @JsonProperty("expected_salary_range_inr_lpa") SalaryRange expectedSalaryRangeInrLpa,
        @JsonProperty("preferred_work_mode") String preferredWorkMode,
        @JsonProperty("willing_to_relocate") boolean willingToRelocate,
        @JsonProperty("github_activity_score") double githubActivityScore,
        @JsonProperty("search_appearance_30d") int searchAppearance30d,
        @JsonProperty("saved_by_recruiters_30d") int savedByRecruiters30d,
        @JsonProperty("interview_completion_rate") double interviewCompletionRate,
        @JsonProperty("offer_acceptance_rate") double offerAcceptanceRate,
        @JsonProperty("verified_email") boolean verifiedEmail,
        @JsonProperty("verified_phone") boolean verifiedPhone,
        @JsonProperty("linkedin_connected") boolean linkedinConnected
) {}