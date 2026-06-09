package com.redrob.ranker.jd;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class JobDescriptionParser {

    private static final Map<String, List<String>> ROLE_HINTS = Map.of(
            "DATA_SCIENTIST", List.of("data scientist", "applied scientist"),
            "ML_ENGINEER", List.of("ml engineer", "machine learning engineer", "ai engineer"),
            "DATA_ENGINEER", List.of("data engineer", "analytics engineer", "etl engineer"),
            "BACKEND_ENGINEER", List.of("backend engineer", "software engineer")
    );

    private static final Map<String, List<String>> SKILL_ALIASES = Map.ofEntries(
            Map.entry("python", List.of("python")),
            Map.entry("machine learning", List.of("machine learning", "ml")),
            Map.entry("deep learning", List.of("deep learning")),
            Map.entry("nlp", List.of("nlp", "natural language processing")),
            Map.entry("llm", List.of("llm", "large language model", "generative ai", "genai")),
            Map.entry("sql", List.of("sql")),
            Map.entry("pytorch", List.of("pytorch")),
            Map.entry("tensorflow", List.of("tensorflow")),
            Map.entry("scikit-learn", List.of("scikit-learn", "sklearn")),
            Map.entry("airflow", List.of("airflow")),
            Map.entry("spark", List.of("spark", "pyspark")),
            Map.entry("feature engineering", List.of("feature engineering")),
            Map.entry("model deployment", List.of("model deployment", "mlops", "model serving")),
            Map.entry("vector databases", List.of("vector database", "milvus", "faiss", "pinecone"))
    );

    private static final Pattern YEARS_PATTERN_A = Pattern.compile("(\\d+(?:\\.\\d+)?)\\+?\\s*years");
    private static final Pattern YEARS_PATTERN_B = Pattern.compile("minimum\\s*(\\d+(?:\\.\\d+)?)\\s*years");
    private static final Pattern YEARS_PATTERN_C = Pattern.compile("at\\s*least\\s*(\\d+(?:\\.\\d+)?)\\s*years");

    public ParsedJobDescription parse(JobDescription jd) {
        if (jd == null || jd.rawText() == null || jd.rawText().isBlank()) {
            throw new IllegalArgumentException("JobDescription is empty");
        }

        String normalized = normalize(jd.rawText());
        String roleFamily = inferRoleFamily(normalized);
        List<String> matchedSkills = extractSkills(normalized);
        double minYears = extractMinYearsExperience(normalized);

//        int splitIndex = Math.max(1, (int) Math.ceil(matchedSkills.size() * 0.9));  //90 percent skill match is ideal scenario, will minimize the skill metrics to 0.6
        int splitIndex = Math.max(1, (int) Math.ceil(matchedSkills.size() * 0.6));
        List<String> mustHave = matchedSkills.subList(0, Math.min(splitIndex, matchedSkills.size()));
        List<String> niceToHave = matchedSkills.subList(Math.min(splitIndex, matchedSkills.size()), matchedSkills.size());

        return new ParsedJobDescription(
                jd.sourcePath(),
                normalized,
                roleFamily,
                minYears,
                mustHave,
                niceToHave
        );
    }

    private String normalize(String text) {
        return text.toLowerCase(Locale.ROOT)
                .replace("\r\n", "\n")
                .replace('\r', '\n')
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String inferRoleFamily(String normalizedText) {
        String bestRole = "GENERAL_AI";
        int bestScore = 0;

        for (Map.Entry<String, List<String>> entry : ROLE_HINTS.entrySet()) {
            int score = 0;
            for (String hint : entry.getValue()) {
                if (normalizedText.contains(hint)) {
                    score++;
                }
            }
            if (score > bestScore) {
                bestScore = score;
                bestRole = entry.getKey();
            }
        }
        return bestRole;
    }

    private List<String> extractSkills(String normalizedText) {
        Set<String> found = new LinkedHashSet<>();

        for (Map.Entry<String, List<String>> entry : SKILL_ALIASES.entrySet()) {
            String canonical = entry.getKey();
            for (String alias : entry.getValue()) {
                if (containsWholeTerm(normalizedText, alias)) {
                    found.add(canonical);
                    break;
                }
            }
        }

        return found.stream().sorted().collect(Collectors.toList());
    }

    private boolean containsWholeTerm(String text, String term) {
        String escaped = Pattern.quote(term);
        Pattern p = Pattern.compile("\\b" + escaped + "\\b");
        return p.matcher(text).find();
    }

    private double extractMinYearsExperience(String normalizedText) {
        List<Double> values = new ArrayList<>();
        collectYears(values, YEARS_PATTERN_A, normalizedText);
        collectYears(values, YEARS_PATTERN_B, normalizedText);
        collectYears(values, YEARS_PATTERN_C, normalizedText);

        if (values.isEmpty()) {
            return 0.0;
        }
        return values.stream().min(Double::compareTo).orElse(0.0);
    }

    private void collectYears(List<Double> out, Pattern pattern, String text) {
        Matcher m = pattern.matcher(text);
        while (m.find()) {
            try {
                out.add(Double.parseDouble(m.group(1)));
            } catch (NumberFormatException ignored) {
            }
        }
    }
}