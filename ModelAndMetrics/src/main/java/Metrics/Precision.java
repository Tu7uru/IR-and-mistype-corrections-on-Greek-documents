package Metrics;

import Model.Document;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Precision {

    private double score;

    public Precision() {
        this.score = 0;
    }

    public double calculatePrecision(List<Document> docsRetrieved, HashSet<Integer> relevantDocIds ) {
        int numRetrieved = docsRetrieved.size();
        int numRelevantRetrieved = 0;

        for(Document doc : docsRetrieved) {
            if(relevantDocIds.contains(doc.getID())) {
                numRelevantRetrieved++;
            }
        }
        return ((double) numRelevantRetrieved) / numRetrieved;
    }

    public void setScore(double value) {
        this.score = value;
    }

    public double getScore() {
        return this.score;
    }


}
