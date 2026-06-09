package com.redrob.ranker.output;

public record RankedCandidate(
        String candidateId,
        int rank,
        double score,
        String reasoning
) {}