package com.redrob.ranker.scoring;

import java.util.List;

public record CandidateFeatures(
        String candidateId,
        String currentTitle,
        double yearsExperience,
        String candidateText,
        List<String> skillNames,
        List<String> titleHistory
) {}