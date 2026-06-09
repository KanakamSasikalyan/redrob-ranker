package com.redrob.ranker.scoring;

import com.redrob.ranker.jd.ParsedJobDescription;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class CandidateScorer {

    private static final Set<String> STOP_WORDS = Set.of(
            "the", "a", "an", "and", "or", "for", "to", "of", "in", "on", "with", "is", "are", "be", "as", "by"
    );

    private static final Map<String, List<String>> ROLE_HINTS = Map.of(
            "DATA_SCIENTIST", List.of("data scientist", "applied scientist"),
            "ML_ENGINEER", List.of("ml engineer", "machine learning engineer", "ai engineer"),
            "DATA_ENGINEER", List.of("data engineer", "analytics engineer", "etl engineer"),
            "BACKEND_ENGINEER", List.of("backend engineer", "software engineer")
    );

    public ScoreBreakdown score(ParsedJobDescription jd, CandidateFeatures cf) {
        double semantic = semanticBaseline(jd.normalizedText(), cf.candidateText());
        double skill = skillEvidence(jd, cf.skillNames());
        double role = roleFit(jd.roleFamily(), cf.titleHistory());
        double exp = experienceFit(jd.minYearsExperience(), cf.yearsExperience());

        double total = (0.50 * semantic) + (0.25 * skill) + (0.15 * role) + (0.10 * exp);

        String reason = String.format(
                Locale.ROOT,
                "%s, %.1f yrs; sem=%.2f skill=%.2f role=%.2f exp=%.2f",
                emptyToUnknown(cf.currentTitle()),
                cf.yearsExperience(),
                semantic, skill, role, exp
        );

        return new ScoreBreakdown(
                cf.candidateId(),
                round6(semantic),
                round6(skill),
                round6(role),
                round6(exp),
                round6(total),
                reason
        );
    }

    private double semanticBaseline(String jdText, String candidateText) {
        Set<String> jdTokens = tokenize(jdText);
        Set<String> candTokens = tokenize(candidateText);

        if (jdTokens.isEmpty() || candTokens.isEmpty()) {
            return 0.0;
        }

        Set<String> intersection = new HashSet<>(jdTokens);
        intersection.retainAll(candTokens);

        Set<String> union = new HashSet<>(jdTokens);
        union.addAll(candTokens);

        return (double) intersection.size() / union.size();
    }

    private double skillEvidence(ParsedJobDescription jd, List<String> candidateSkills) {
        if (candidateSkills == null || candidateSkills.isEmpty()) {
            return 0.0;
        }

        int mustTotal = jd.mustHaveSkills().size();
        int niceTotal = jd.niceToHaveSkills().size();

        int mustMatch = matchCount(jd.mustHaveSkills(), candidateSkills);
        int niceMatch = matchCount(jd.niceToHaveSkills(), candidateSkills);

        double mustScore = mustTotal == 0 ? 0.0 : (double) mustMatch / mustTotal;
        double niceScore = niceTotal == 0 ? 0.0 : (double) niceMatch / niceTotal;

        return clamp01((0.75 * mustScore) + (0.25 * niceScore));
    }

    private int matchCount(List<String> jdSkills, List<String> candidateSkills) {
        int count = 0;
        for (String target : jdSkills) {
            String t = normalize(target);
            boolean matched = candidateSkills.stream()
                    .map(this::normalize)
                    .anyMatch(cs -> cs.contains(t) || t.contains(cs));
            if (matched) {
                count++;
            }
        }
        return count;
    }

    private double roleFit(String roleFamily, List<String> titleHistory) {
        List<String> hints = ROLE_HINTS.getOrDefault(roleFamily, List.of());
        if (hints.isEmpty()) {
            return 0.5;
        }

        int hits = 0;
        for (String title : titleHistory) {
            String t = normalize(title);
            for (String hint : hints) {
                if (t.contains(normalize(hint))) {
                    hits++;
                }
            }
        }

        return clamp01(0.2 + (0.25 * hits));
    }

    private double experienceFit(double minYears, double candidateYears) {
        if (minYears <= 0.0) {
            return clamp01(candidateYears / 8.0);
        }
        if (candidateYears >= minYears) {
            return clamp01(0.8 + ((candidateYears - minYears) * 0.03));
        }
        double gap = minYears - candidateYears;
        return clamp01(1.0 - (0.2 * gap));
    }

    private Set<String> tokenize(String text) {
        if (text == null || text.isBlank()) return Set.of();

        String cleaned = text.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9\\s]", " ");

        return Pattern.compile("\\s+")
                .splitAsStream(cleaned)
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .filter(s -> !STOP_WORDS.contains(s))
                .collect(Collectors.toSet());
    }

    private String normalize(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT).trim();
    }

    private String emptyToUnknown(String s) {
        return (s == null || s.isBlank()) ? "unknown-title" : s;
    }

    private double clamp01(double v) {
        if (v < 0.0) return 0.0;
        if (v > 1.0) return 1.0;
        return v;
    }

    private double round6(double v) {
        return Math.round(v * 1_000_000d) / 1_000_000d;
    }
}