import Model.Document;
import com.algolia.search.DefaultSearchClient;
import com.algolia.search.SearchClient;
import com.algolia.search.SearchIndex;
import com.algolia.search.models.indexing.ActionEnum;
import com.algolia.search.models.indexing.BatchOperation;
import com.algolia.search.models.indexing.Query;
import com.algolia.search.models.indexing.SearchResult;
import com.algolia.search.models.settings.IndexSettings;
import com.algolia.search.models.settings.RemoveStopWords;
import org.json.simple.parser.ParseException;

import javax.print.Doc;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static LexicalAnalysis.JsonReader.parseJsonFileDocuments;

public class BatchIndex {


    public static List<BatchOperation<Document>> parseDocsToBatch(ArrayList<Document> docs) {

        List<BatchOperation<Document>> tmp = new ArrayList<>();

        for(Document doc : docs) {
            BatchOperation<Document> entry = new BatchOperation<Document>("test_index",ActionEnum.ADD_OBJECT,doc);
            tmp.add(entry);
        }
        return tmp;
    }

    public static void main(String[] args) throws IOException, ParseException {
        ArrayList<Document> documents = parseJsonFileDocuments("./src/main/resources/dataset.json");

        String indexName;
        Boolean removeStopwords = Boolean.TRUE;

        if(removeStopwords) {
            indexName = "dataset_WithoutStopwords";
            //LoggedIn in Algolia
            //https://www.algolia.com/users/sign_in
            // Log in Algolia and go to API keys and replace string with the specific values
            SearchClient client = DefaultSearchClient.create("ApplicationID", "AdminAPIKey");


            // Create a new index and add a record (using the `Record` class)
            SearchIndex<Document> index = client.initIndex(indexName, Document.class);
            // One by one Insertion
            /*for(Document doc : docs) {
                index.saveObject(doc).waitTask();
            }*/

            index.setSettings(
                    new IndexSettings()
                            .setQueryLanguages(Collections.singletonList("el"))
                            .setRemoveStopWords(RemoveStopWords.of(true))
                            .setRemoveStopWords(true)
            );

            index.saveObjects(documents).waitTask();
        }
        else {

            indexName = "dataset";
            //LoggedIn in Algolia
            //https://www.algolia.com/users/sign_in
            // Log in Algolia and go to API keys and replace string with the specific values
            SearchClient client = DefaultSearchClient.create("ApplicationID", "AdminAPIKey");


            // Create a new index and add a record (using the `Record` class)
            SearchIndex<Document> index = client.initIndex(indexName, Document.class);
            index.saveObjects(documents).waitTask();
            // One by one Insertion
            /*for(Document doc : docs) {
                index.saveObject(doc).waitTask();
            }*/
        }
        // Search the index and print the results
        //SearchResult<Document> results = index.search(new Query("κάρδαμο"));
        //System.out.println(results.getHits().get(0).getBody());
    }
}
