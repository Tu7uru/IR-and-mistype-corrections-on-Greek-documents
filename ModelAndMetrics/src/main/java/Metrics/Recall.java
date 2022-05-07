package Metrics;

import Model.Document;

import java.util.HashSet;
import java.util.List;

public class Recall {

    private double score;

    public Recall() {
        this.score = 0;
    }

    public double calculateRecall(List<Document> docsRetrieved, HashSet<Integer> relevantDocIds ) {
        int numRelevant= relevantDocIds.size();
        int numRelevantRetrieved = 0;

        if(numRelevant == 0) {
            return 0.0;
        }

        for(Document doc : docsRetrieved) {
            if(relevantDocIds.contains(doc.getID())) {
                numRelevantRetrieved++;
            }
        }
        return ((double) numRelevantRetrieved) / numRelevant;
    }

    public void setScore(double value) {
        this.score = value;
    }

    public double getScore() {
        return this.score;
    }
}
