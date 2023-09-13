import LexicalAnalysis.JsonReader;
import Metrics.F_Measure;
import Metrics.Fall_Out;
import Metrics.Precision;
import Metrics.Recall;
import Model.Document;
import com.algolia.search.DefaultSearchClient;
import com.algolia.search.SearchClient;
import com.algolia.search.SearchIndex;
import com.algolia.search.models.indexing.Query;
import com.algolia.search.models.indexing.SearchResult;
import com.algolia.search.models.settings.IndexSettings;
import com.algolia.search.models.settings.RemoveStopWords;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.*;

import static LexicalAnalysis.JsonReader.parseJsonFileDocuments;

public class Search {

    public static void main(String[] args) throws IOException, ParseException {
        ArrayList<Document> documents = parseJsonFileDocuments("./src/main/resources/dataset.json");
        ArrayList<Model.Query> queries = JsonReader.parseJsonFileQueries("./src/main/resources/dataset.json");
        List<String> stopwords = JsonReader.parseJsonStopwords("./src/main/resources/el-stopwords.json");
        Set<Integer> keys = new HashSet<>();

        String indexName;
        Boolean removeStopwords = Boolean.FALSE;

        List<Document> documentsRetrieved = new ArrayList<>();

        Double avPrec = 0.0;
        Double avRec = 0.0;
        Double avFout = 0.0;
        Double avFmeas = 0.0;

        if(removeStopwords) {
            indexName = "dataset_WithoutStopwords";
            //LoggedIn in Algolia
            //https://www.algolia.com/users/sign_in
            // Log in Algolia and go to API keys and replace string with the specific values
            SearchClient client = DefaultSearchClient.create("ApplicationID", "AdminAPIKey");

            // Create a new index and add a record (using the `Record` class)
            SearchIndex<Document> index = client.initIndex(indexName, Document.class);
            for(Model.Query query : queries) {

                SearchResult<Document> results = index.search(new Query(query.getQuery())
                        .setQueryLanguages(Collections.singletonList("el"))
                        .setRemoveStopWords(RemoveStopWords.of(stopwords))
                        .setHitsPerPage(150));
                System.out.println(results.getHits().size());

                Precision precision = new Precision();
                Recall recall = new Recall();
                F_Measure f_measure = new F_Measure();
                Fall_Out fall_out = new Fall_Out();
                HashSet<Integer> query_relIds = new HashSet<>();

                keys = query.getHashMap().keySet();
                for (Integer key : keys) {
                    query_relIds.add(key);
                }

                documentsRetrieved = results.getHits();

                System.out.println(query.getQuery());
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
        }
        else {

            indexName = "dataset";
            //LoggedIn in Algolia
            //https://www.algolia.com/users/sign_in
            // Log in Algolia and go to API keys and replace string with the specific values
            SearchClient client = DefaultSearchClient.create("ApplicationID", "AdminAPIKey");


            // Create a new index and add a record (using the `Record` class)
            SearchIndex<Document> index = client.initIndex(indexName, Document.class);
            for(Model.Query query : queries) {

                SearchResult<Document> results = index.search(new Query(query.getQuery())
                        .setQueryLanguages(Collections.singletonList("el"))
                        .setHitsPerPage(150));
                System.out.println(results.getHits().size());

                Precision precision = new Precision();
                Recall recall = new Recall();
                F_Measure f_measure = new F_Measure();
                Fall_Out fall_out = new Fall_Out();
                HashSet<Integer> query_relIds = new HashSet<>();

                keys = query.getHashMap().keySet();
                for (Integer key : keys) {
                    query_relIds.add(key);
                }

                documentsRetrieved = results.getHits();

                System.out.println(query.getQuery());
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
        }
        // Search the index and print the results
        //SearchResult<Document> results = index.search(new Query("κάρδαμο"));
        //System.out.println(results.getHits().get(0).getBody());
        System.out.println("avPrec:" + avPrec/queries.size());
        System.out.println("avRec:" + avRec/queries.size());
        System.out.println("avFmeas:" + avFmeas/queries.size());
        System.out.println("avFout:" + avFout/queries.size());
    }
}
