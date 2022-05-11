package service;

import Metrics.F_Measure;
import Metrics.Fall_Out;
import Metrics.Precision;
import Metrics.Recall;
import Model.Document;
import Model.Query;
import org.apache.solr.common.SolrDocumentList;

import java.io.FileReader;
import java.io.IOException;
import org.json.simple.parser.ParseException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static LexicalAnalysis.JsonReader.parseJsonFileDocuments;
import static LexicalAnalysis.JsonReader.parseJsonFileQueries;
import static service.SolrParser.parseSolrDocuments;

public class Search {

    /* Do not forget to
        Create cores    : solr create -c [name]
        Start solr      : Solr start -p 8983
        Stop solr       : Solr stop -all
     */

    public static void main(String[] args) throws IOException, ParseException {

        ArrayList<Query> queries  = parseJsonFileQueries("src/main/resources/dataset.json");
        ArrayList<Document> documents = parseJsonFileDocuments("src/main/resources/dataset.json");

        Boolean removeStopwords = Boolean.TRUE;
        Boolean applyStemming = Boolean.TRUE;
        Boolean allQueryTermsMustExist = Boolean.FALSE;

        ArrayList<Document> documentsRetrieved = new ArrayList<>();
        Set<Integer> keys = new HashSet<>();

        Double avPrec = 0.0;
        Double avRec = 0.0;
        Double avFout = 0.0;
        Double avFmeas = 0.0;


        SolrQueryBuilder builder = new SolrQueryBuilder();
        for (Query q : queries) {
            SolrDocumentList results = builder.solrQuery(q.getQuery(),removeStopwords,applyStemming,allQueryTermsMustExist);

            documentsRetrieved = parseSolrDocuments(results);
            Precision precision = new Precision();
            Recall recall = new Recall();
            F_Measure f_measure = new F_Measure();
            Fall_Out fall_out = new Fall_Out();
            HashSet<Integer> query_relIds = new HashSet<>();

            keys = q.getHashMap().keySet();
            for (Integer key : keys) {
                query_relIds.add(key);
            }
            //query_relIds = (HashSet<Integer>) q.getHashMap().keySet();

            System.out.println(q.getQuery());
            precision.setScore(precision.calculatePrecision(documentsRetrieved,query_relIds));
            System.out.println("precision:" + precision.getScore());

            recall.setScore(recall.calculateRecall(documentsRetrieved,query_relIds));
            System.out.println("recall:" + recall.getScore());

            f_measure.setScore(f_measure.calculateRecall(precision,recall));
            System.out.println("f_measure:" + f_measure.getScore());
            fall_out.setScore(fall_out.calculateFallOut(documentsRetrieved,query_relIds,documents.size()));
            System.out.println("fall out:" + fall_out.getScore());

            avPrec += precision.getScore();
            avRec += recall.getScore();
            avFout += fall_out.getScore();
            avFmeas += f_measure.getScore();
        }
        System.out.println("avPrec:" + avPrec/queries.size());
        System.out.println("avRec:" + avRec/queries.size());
        System.out.println("avFmeas:" + avFmeas/queries.size());
        System.out.println("avFout:" + avFout/queries.size());
    }
}