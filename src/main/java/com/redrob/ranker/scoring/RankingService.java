package com.redrob.ranker.scoring;

import com.redrob.ranker.jd.ParsedJobDescription;
import com.redrob.ranker.model.Candidate;
import com.redrob.ranker.output.RankedCandidate;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class RankingService {

    private final CandidateFeatureExtractor featureExtractor;
    private final CandidateScorer scorer;

    public RankingService() {
        this.featureExtractor = new CandidateFeatureExtractor();
        this.scorer = new CandidateScorer();
    }

    public List<RankedCandidate> rankTopK(ParsedJobDescription jd, List<Candidate> candidates, int k) {
        if (jd == null) throw new IllegalArgumentException("Parsed JD cannot be null");
        if (candidates == null || candidates.isEmpty()) return List.of();
        if (k <= 0) throw new IllegalArgumentException("k must be > 0");

        List<ScoreBreakdown> scored = new ArrayList<>(candidates.size());

        for (Candidate c : candidates) {
            CandidateFeatures cf = featureExtractor.extract(c);
            ScoreBreakdown sb = scorer.score(jd, cf);
            scored.add(sb);
        }

        scored.sort(
                Comparator.comparingDouble(ScoreBreakdown::totalScore).reversed()
                        .thenComparing(ScoreBreakdown::candidateId)
        );

        int limit = Math.min(k, scored.size());
        List<ScoreBreakdown> top = scored.subList(0, limit);

        // Normalize to monotonic non-increasing range [0.2, 1.0]
        double max = top.stream().mapToDouble(ScoreBreakdown::totalScore).max().orElse(1.0);
        double min = top.stream().mapToDouble(ScoreBreakdown::totalScore).min().orElse(0.0);

        List<RankedCandidate> out = new ArrayList<>(limit);
        double prev = 1.0;

        for (int i = 0; i < limit; i++) {
            ScoreBreakdown sb = top.get(i);
            double normalized = normalize(sb.totalScore(), min, max);
            normalized = Math.min(normalized, prev);
            prev = normalized;

            out.add(new RankedCandidate(
                    sb.candidateId(),
                    i + 1,
                    round6(normalized),
                    shortenReasoning(sb.reasoning())
            ));
        }

        return out;
    }

    private double normalize(double value, double min, double max) {
        if (Math.abs(max - min) < 1e-12) {
            return 1.0;
        }
        double z = (value - min) / (max - min); // [0,1]
        return 0.2 + (0.8 * z); // [0.2,1.0]
    }

    private double round6(double v) {
        return Math.round(v * 1_000_000d) / 1_000_000d;
    }

    private String shortenReasoning(String s) {
        if (s == null || s.isBlank()) return "Profile matched by hybrid scoring.";
        String trimmed = s.trim();
        return trimmed.length() <= 240 ? trimmed : trimmed.substring(0, 237) + "...";
    }
}