package com.redrob.ranker.scoring;

public record ScoreBreakdown(
        String candidateId,
        double semanticScore,
        double skillEvidenceScore,
        double roleFitScore,
        double experienceFitScore,
        double totalScore,
        String reasoning
) {}