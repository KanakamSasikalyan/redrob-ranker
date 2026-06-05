package com.redrob.ranker.ingest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.redrob.ranker.common.MapperFactory;
import com.redrob.ranker.model.Candidate;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class CandidateJsonlSmokeTest {

    @Test
    void shouldParseFirstCandidateFromJsonl() throws IOException {
        Path jsonlPath = Path.of("[PUB] India_runs_data_and_ai_challenge",
                "[PUB] India_runs_data_and_ai_challenge",
                "India_runs_data_and_ai_challenge",
                "candidates.jsonl");

        assertTrue(Files.exists(jsonlPath), "candidates.jsonl not found at: " + jsonlPath.toAbsolutePath());

        String firstLine;
        try (BufferedReader reader = Files.newBufferedReader(jsonlPath)) {
            firstLine = reader.readLine();
        }

        assertNotNull(firstLine, "First line in candidates.jsonl is null/empty");

        ObjectMapper mapper = MapperFactory.defaultMapper();
        Candidate candidate = mapper.readValue(firstLine, Candidate.class);

        assertNotNull(candidate);
        assertNotNull(candidate.candidateId());
        assertFalse(candidate.candidateId().isBlank());

        assertNotNull(candidate.profile());
        assertNotNull(candidate.profile().headline());

        assertNotNull(candidate.careerHistory());
        assertFalse(candidate.careerHistory().isEmpty());

        assertNotNull(candidate.redrobSignals());
        assertTrue(candidate.profile().yearsOfExperience() >= 0.0);
    }
}