package Metrics;

import Model.Document;

import java.util.HashSet;
import java.util.List;

public class Fall_Out {
    private double score;

    public Fall_Out() {
        this.score = 0;
    }

    public double calculateFallOut(List<Document> docsRetrieved, HashSet<Integer> relevantDocIds,Integer totalNumOfDocs) {
        int numNonRelevant = totalNumOfDocs - relevantDocIds.size();
        int numNonRelevantRetrieved = 0;

        for(Document doc : docsRetrieved) {
            if(!relevantDocIds.contains(doc.getID())) {
                numNonRelevantRetrieved++;
            }
        }
        return ((double) numNonRelevantRetrieved) / numNonRelevant;
    }

    public void setScore(double value) {
        this.score = value;
    }

    public double getScore() {
        return this.score;
    }
}
