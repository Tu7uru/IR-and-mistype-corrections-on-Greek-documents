package Metrics;

import Model.Document;

import java.util.HashSet;
import java.util.List;

public class F_Measure {

    private double score;

    public F_Measure() {
        this.score = 0;
    }

    public double calculateRecall(Precision precision,Recall recall ) {
        double precisionScore = precision.getScore();
        double recallScore = recall.getScore();

        double numerator = 2 * precisionScore * recallScore;
        double denominator = precisionScore + recallScore;
        return numerator / denominator;
    }

    public void setScore(double value) {
        this.score = value;
    }

    public double getScore() {
        return this.score;
    }

}
