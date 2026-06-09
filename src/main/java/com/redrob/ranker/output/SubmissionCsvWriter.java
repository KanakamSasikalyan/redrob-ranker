package com.redrob.ranker.output;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Locale;

public class SubmissionCsvWriter {

    public void write(Path outPath, List<RankedCandidate> ranked) throws IOException {
        if (outPath == null) throw new IllegalArgumentException("outPath cannot be null");
        if (ranked == null) throw new IllegalArgumentException("ranked list cannot be null");

        Files.createDirectories(outPath.toAbsolutePath().getParent());

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader("candidate_id", "rank", "score", "reasoning")
                .build();

        try (Writer writer = Files.newBufferedWriter(outPath, StandardCharsets.UTF_8);
             CSVPrinter printer = new CSVPrinter(writer, format)) {

            for (RankedCandidate r : ranked) {
                printer.printRecord(
                        r.candidateId(),
                        r.rank(),
                        String.format(Locale.ROOT, "%.6f", r.score()),
                        r.reasoning()
                );
            }
        }
    }
}