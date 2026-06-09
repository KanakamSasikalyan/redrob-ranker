package com.redrob.ranker.jd;

import java.util.List;

public record ParsedJobDescription(
        String sourcePath,
        String normalizedText,
        String roleFamily,
        double minYearsExperience,
        List<String> mustHaveSkills,
        List<String> niceToHaveSkills
) {}