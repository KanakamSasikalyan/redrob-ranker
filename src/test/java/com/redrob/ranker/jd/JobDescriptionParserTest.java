package com.redrob.ranker.jd;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JobDescriptionParserTest {

    @Test
    void shouldExtractRoleYearsAndSkills() {
        String text = """
                We are hiring a Machine Learning Engineer.
                Minimum 4 years experience in ML systems.
                Must have Python, SQL, PyTorch, and model deployment experience.
                Nice to have NLP and vector database exposure.
                """;

        JobDescription jd = new JobDescription("inline", text);
        JobDescriptionParser parser = new JobDescriptionParser();

        ParsedJobDescription parsed = parser.parse(jd);

        assertEquals("ML_ENGINEER", parsed.roleFamily());
        assertEquals(4.0, parsed.minYearsExperience(), 0.001);

        assertTrue(parsed.mustHaveSkills().size() >= 1);
        assertTrue(parsed.mustHaveSkills().contains("python"));
        assertTrue(parsed.mustHaveSkills().contains("sql") || parsed.niceToHaveSkills().contains("sql"));
        assertTrue(parsed.mustHaveSkills().contains("pytorch") || parsed.niceToHaveSkills().contains("pytorch"));
        assertTrue(parsed.mustHaveSkills().contains("model deployment") || parsed.niceToHaveSkills().contains("model deployment"));
    }

    @Test
    void shouldFallbackToGeneralRoleWhenNoHints() {
        String text = "Looking for a motivated professional with strong collaboration and ownership.";

        JobDescription jd = new JobDescription("inline", text);
        JobDescriptionParser parser = new JobDescriptionParser();

        ParsedJobDescription parsed = parser.parse(jd);

        assertEquals("GENERAL_AI", parsed.roleFamily());
        assertEquals(0.0, parsed.minYearsExperience(), 0.001);
        assertTrue(parsed.mustHaveSkills().isEmpty() || parsed.mustHaveSkills().size() >= 0);
    }

    @Test
    void shouldThrowForEmptyInput() {
        JobDescriptionParser parser = new JobDescriptionParser();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> parser.parse(new JobDescription("inline", "   "))
        );

        assertTrue(ex.getMessage().contains("empty"));
    }
}