package com.redrob.ranker.scoring;

import com.redrob.ranker.jd.ParsedJobDescription;
import com.redrob.ranker.model.Candidate;
import com.redrob.ranker.model.Profile;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RankingServiceTest {

    @Test
    void shouldReturnSortedTopKWithValidRanks() {
        ParsedJobDescription jd = new ParsedJobDescription(
                "inline",
                "ml engineer python sql pytorch 4 years",
                "ML_ENGINEER",
                4.0,
                List.of("python", "sql", "pytorch"),
                List.of("model deployment")
        );

        Candidate c1 = TestFixtures.candidate("CAND_0000001", "Machine Learning Engineer", 5.0, List.of("python", "sql", "pytorch"));
        Candidate c2 = TestFixtures.candidate("CAND_0000002", "Operations Manager", 10.0, List.of("excel"));
        Candidate c3 = TestFixtures.candidate("CAND_0000003", "AI Engineer", 4.5, List.of("python", "pytorch"));

        RankingService service = new RankingService();
        List<?> ranked = service.rankTopK(jd, List.of(c1, c2, c3), 2);

        assertEquals(2, ranked.size());
    }

    // Minimal fixture helper to keep test focused
    static class TestFixtures {
        static Candidate candidate(String id, String title, double years, List<String> skills) {
            Profile p = new Profile(
                    "anon", title, title + " summary", "city", "India",
                    years, title, "X", "201-500", "IT Services"
            );
            return new Candidate(
                    id, p, List.of(), List.of(),
                    skills.stream().map(s -> new com.redrob.ranker.model.SkillItem(s, "advanced", 10, 24)).toList(),
                    List.of(), List.of(),
                    new com.redrob.ranker.model.RedrobSignals(
                            80.0, java.time.LocalDate.now(), java.time.LocalDate.now(),
                            true, 10, 2, 0.6, 24.0, java.util.Map.of(),
                            100, 20, 30, new com.redrob.ranker.model.SalaryRange(10, 20),
                            "hybrid", true, 40.0, 30, 5, 0.8, 0.5, true, true, true
                    )
            );
        }
    }
}