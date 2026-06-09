package com.redrob.ranker.scoring;

import com.redrob.ranker.model.Candidate;
import com.redrob.ranker.model.CareerHistoryItem;
import com.redrob.ranker.model.SkillItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;

public class CandidateFeatureExtractor {

    public CandidateFeatures extract(Candidate c) {
        String currentTitle = safeLower(c.profile() != null ? c.profile().currentTitle() : "");
        double years = c.profile() != null ? c.profile().yearsOfExperience() : 0.0;

        List<String> skills = c.skills() == null ? List.of() :
                c.skills().stream()
                        .map(SkillItem::name)
                        .filter(Objects::nonNull)
                        .map(this::safeLower)
                        .toList();

        List<String> titles = new ArrayList<>();
        if (currentTitle != null && !currentTitle.isBlank()) {
            titles.add(currentTitle);
        }
        if (c.careerHistory() != null) {
            titles.addAll(c.careerHistory().stream()
                    .map(CareerHistoryItem::title)
                    .filter(Objects::nonNull)
                    .map(this::safeLower)
                    .toList());
        }

        String text = buildCandidateText(c);

        return new CandidateFeatures(
                c.candidateId(),
                currentTitle,
                years,
                text,
                skills,
                titles
        );
    }

    private String buildCandidateText(Candidate c) {
        List<String> parts = new ArrayList<>();

        if (c.profile() != null) {
            add(parts, c.profile().headline());
            add(parts, c.profile().summary());
            add(parts, c.profile().currentTitle());
            add(parts, c.profile().currentIndustry());
        }

        if (c.careerHistory() != null) {
            for (CareerHistoryItem h : c.careerHistory()) {
                add(parts, h.title());
                add(parts, h.description());
                add(parts, h.industry());
            }
        }

        if (c.skills() != null) {
            String skillLine = c.skills().stream()
                    .map(SkillItem::name)
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining(" "));
            add(parts, skillLine);
        }

        return parts.stream()
                .map(this::safeLower)
                .collect(Collectors.joining(" "));
    }

    private void add(List<String> out, String value) {
        if (value != null && !value.isBlank()) {
            out.add(value.trim());
        }
    }

    private String safeLower(String s) {
        return s == null ? "" : s.toLowerCase(Locale.ROOT).trim();
    }
}