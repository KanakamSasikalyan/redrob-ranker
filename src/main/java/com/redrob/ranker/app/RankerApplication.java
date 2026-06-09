package com.redrob.ranker.app;

import com.redrob.ranker.ingest.CandidateJsonlReader;
import com.redrob.ranker.ingest.JobDescriptionReader;
import com.redrob.ranker.jd.JobDescription;
import com.redrob.ranker.jd.JobDescriptionParser;
import com.redrob.ranker.jd.ParsedJobDescription;
import com.redrob.ranker.model.Candidate;
import com.redrob.ranker.output.RankedCandidate;
import com.redrob.ranker.output.SubmissionCsvWriter;
import com.redrob.ranker.scoring.RankingService;

import java.nio.file.Path;
import java.util.List;

public class RankerApplication {

    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            System.out.println("Usage: java -jar redrob-ranker.jar <candidates.jsonl> <job_description.(txt|docx)> <output.csv> [topK]");
            return;
        }

        Path candidatesPath = Path.of(args[0]);
        Path jdPath = Path.of(args[1]);
        Path outPath = Path.of(args[2]);
        int topK = args.length >= 4 ? Integer.parseInt(args[3]) : 100;

        CandidateJsonlReader candidateReader = new CandidateJsonlReader();
        JobDescriptionReader jdReader = new JobDescriptionReader();
        JobDescriptionParser jdParser = new JobDescriptionParser();
        RankingService rankingService = new RankingService();
        SubmissionCsvWriter csvWriter = new SubmissionCsvWriter();

        List<Candidate> candidates = candidateReader.readAll(candidatesPath);
        JobDescription jd = jdReader.read(jdPath);
        ParsedJobDescription parsedJd = jdParser.parse(jd);

        List<RankedCandidate> ranked = rankingService.rankTopK(parsedJd, candidates, topK);
        csvWriter.write(outPath, ranked);

        System.out.println("Done. Wrote " + ranked.size() + " rows to " + outPath.toAbsolutePath());
    }
}