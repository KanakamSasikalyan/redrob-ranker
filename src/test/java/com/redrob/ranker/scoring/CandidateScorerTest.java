package com.redrob.ranker.scoring;

import com.redrob.ranker.jd.ParsedJobDescription;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class CandidateScorerTest {

    @Test
    void shouldGiveHigherScoreToStrongMatch() {
        ParsedJobDescription jd = new ParsedJobDescription(
                "inline",
                "we need ml engineer with python sql pytorch model deployment and 4 years experience",
                "ML_ENGINEER",
                4.0,
                List.of("python", "sql", "pytorch", "model deployment"),
                List.of("nlp")
        );

        CandidateFeatures strong = new CandidateFeatures(
                "CAND_A",
                "Machine Learning Engineer",
                5.5,
                "machine learning engineer building model deployment pipelines in python sql and pytorch",
                List.of("python", "sql", "pytorch", "model deployment", "mlops"),
                List.of("machine learning engineer", "ai engineer")
        );

        CandidateFeatures weak = new CandidateFeatures(
                "CAND_B",
                "Operations Manager",
                3.0,
                "operations and customer support leadership with process improvements",
                List.of("excel", "communication"),
                List.of("operations manager")
        );

        CandidateScorer scorer = new CandidateScorer();
        ScoreBreakdown strongScore = scorer.score(jd, strong);
        ScoreBreakdown weakScore = scorer.score(jd, weak);

        assertTrue(strongScore.totalScore() > weakScore.totalScore());
        assertTrue(strongScore.skillEvidenceScore() > weakScore.skillEvidenceScore());
        assertTrue(strongScore.roleFitScore() > weakScore.roleFitScore());
    }

    @Test
    void scoreShouldStayInRange() {
        ParsedJobDescription jd = new ParsedJobDescription(
                "inline",
                "python machine learning",
                "GENERAL_AI",
                0.0,
                List.of("python"),
                List.of("machine learning")
        );

        CandidateFeatures cf = new CandidateFeatures(
                "CAND_X",
                "Developer",
                2.0,
                "python developer machine learning basics",
                List.of("python"),
                List.of("developer")
        );

        CandidateScorer scorer = new CandidateScorer();
        ScoreBreakdown s = scorer.score(jd, cf);

        assertTrue(s.semanticScore() >= 0.0 && s.semanticScore() <= 1.0);
        assertTrue(s.skillEvidenceScore() >= 0.0 && s.skillEvidenceScore() <= 1.0);
        assertTrue(s.roleFitScore() >= 0.0 && s.roleFitScore() <= 1.0);
        assertTrue(s.experienceFitScore() >= 0.0 && s.experienceFitScore() <= 1.0);
        assertTrue(s.totalScore() >= 0.0 && s.totalScore() <= 1.0);
    }
}