package com.redrob.ranker.ingest;

import com.redrob.ranker.jd.JobDescription;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JobDescriptionReaderTest {

    @Test
    void shouldReadDocxJobDescription() throws Exception {
        Path docxPath = Path.of("[PUB] India_runs_data_and_ai_challenge",
                "[PUB] India_runs_data_and_ai_challenge",
                "India_runs_data_and_ai_challenge",
                "job_description.docx");

        JobDescriptionReader reader = new JobDescriptionReader();
        JobDescription jd = reader.read(docxPath);

        assertNotNull(jd);
        assertNotNull(jd.rawText());
        assertFalse(jd.rawText().isBlank());
        assertTrue(jd.rawText().length() > 100);
    }

    @Test
    void shouldFailForUnsupportedExtension() {
        Path badPath = Path.of("sample.pdf");
        JobDescriptionReader reader = new JobDescriptionReader();

        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> reader.read(badPath)
        );

        assertTrue(ex.getMessage().contains("Unsupported JD file format")
                || ex.getMessage().contains("JD file not found"));
    }
}