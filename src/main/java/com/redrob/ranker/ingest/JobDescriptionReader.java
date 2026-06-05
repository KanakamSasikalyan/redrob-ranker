package com.redrob.ranker.ingest;

import com.redrob.ranker.jd.JobDescription;
import org.apache.poi.xwpf.usermodel.XWPFDocument;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

public class JobDescriptionReader {

    public JobDescription read(Path jdPath) throws IOException {
        if (jdPath == null) {
            throw new IllegalArgumentException("jdPath cannot be null");
        }
        if (!Files.exists(jdPath)) {
            throw new IllegalArgumentException("Job description file not found: " + jdPath.toAbsolutePath());
        }

        String fileName = jdPath.getFileName().toString().toLowerCase(Locale.ROOT);

        String text;
        if (fileName.endsWith(".txt")) {  //if it is text file we need to read from text file
            text = readTxt(jdPath);
        } else if (fileName.endsWith(".docx")) {  //if its docx (microsoft) need to read it from here
            text = readDocx(jdPath);
        } else {
            throw new IllegalArgumentException("Unsupported Job description file format. Use .txt or .docx");
        }

        String normalized = normalizeWhitespace(text);
        if (normalized.isBlank()) {
            throw new IllegalArgumentException("Job description content is empty after reading: " + jdPath.toAbsolutePath());
        }

        return new JobDescription(jdPath.toString(), normalized);
    }

    private String readTxt(Path path) throws IOException {
        return Files.readString(path, StandardCharsets.UTF_8);
    }

    private String readDocx(Path path) throws IOException {
        StringBuilder sb = new StringBuilder();

        try (InputStream in = Files.newInputStream(path);
             XWPFDocument doc = new XWPFDocument(in)) {

            doc.getParagraphs().forEach(p -> {
                String t = p.getText();
                if (t != null && !t.isBlank()) {
                    if (!sb.isEmpty()) {
                        sb.append('\n');
                    }
                    sb.append(t.trim());
                }
            });
        }

        return sb.toString();
    }

    private String normalizeWhitespace(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("\r\n", "\n")
                .replace('\r', '\n')
                .trim();
    }
}