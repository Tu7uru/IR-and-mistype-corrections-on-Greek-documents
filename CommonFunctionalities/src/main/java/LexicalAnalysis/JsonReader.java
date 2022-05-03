package LexicalAnalysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import Model.Document;
import Model.Query;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import static java.lang.Integer.parseInt;

public class JsonReader {

    ArrayList<Document> documents;
    ArrayList<Query> queries;

    public JsonReader() {
        this.documents = new ArrayList<>();
        this.queries = new ArrayList<>();
    }

    public ArrayList<Document> getDocuments(){
        return this.documents;
    }

    public void setDocuments(ArrayList<Document> docs) {
        this.documents = docs;
    }

    public ArrayList<Query> getQueries() {
        return this.queries;
    }

    public void setQueries(ArrayList<Query> queries) {
        this.queries = queries;
    }

    public static List<Document> parseJsonFileDocuments(String path) throws IOException, ParseException {

        int id;
        String title;
        String body;

        List<Document> documents = new ArrayList<>();

        JSONParser parser = new JSONParser();
        Object jsonData  = parser.parse(new FileReader(path));
        JSONArray jsonDocuments = (JSONArray) ((JSONObject) jsonData).get("Documents");

        for(Object documentObj : jsonDocuments) {
            Document doc = new Document();
            id = ((Long)((JSONObject) documentObj).get("ID")).intValue();
            title = (String) ((JSONObject) documentObj).get("Title");
            body = (String) ((JSONObject) documentObj).get("Body");

            doc.setID(id);
            doc.setTitle(title);
            doc.setBody(body);
            documents.add(doc);
        }

        /*JSONArray a = (JSONArray) parser.parse(new FileReader("./src/main/resources/datasetDocuments.json"));
        for(Object o : a) {
            System.out.println(o);
        }*/

        //JSON parser object to parse read file
        //JSONParser jsonParser = new JSONParser();

        /*try (FileReader reader = new FileReader("./src/main/resources/datasetDocuments.json"))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);

            JSONArray employeeList = (JSONArray) obj;
            System.out.println(employeeList);

            //Iterate over employee array
            employeeList.forEach( emp -> parseEmployeeObject( (JSONObject) emp ) );

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
*/
        return documents;
    }

    public static List<Query> parseJsonFileQueries(String path) throws IOException, ParseException {
        int id;
        String query;
        HashMap<Integer, Integer> relDocs_relPosition;

        int docId = -1;
        int relevanceRank = -1;
        Boolean isDocId = true;

        List<Query> queries = new ArrayList<>();

        JSONParser parser = new JSONParser();
        JSONParser relDocs_parser = new JSONParser();
        Object jsonData = parser.parse(new FileReader(path));
        JSONArray jsonQueries = (JSONArray) ((JSONObject) jsonData).get("Queries");

        for (Object queryObj : jsonQueries) {
            Query newQ = new Query();
            id = ((Long) ((JSONObject) queryObj).get("ID")).intValue();
            query = (String) ((JSONObject) queryObj).get("Query");
            relDocs_relPosition = new HashMap<>();

            JSONArray tmpRelevantDocs = (JSONArray) ((JSONObject) queryObj).get("RelevantDocs");

            String[] tokensOnComma = (tmpRelevantDocs.toString()).split(",");
            for (int x = 0; x < tokensOnComma.length; x++) {
                String[] tokensOnSemiColon = tokensOnComma[x].split(";");
                for (int y = 0; y < tokensOnSemiColon.length; y++) {
                    if (isDocId) {
                        docId = getTokenValue(tokensOnSemiColon[y]);
                        isDocId = false;
                    } else {
                        relevanceRank = getTokenValue(tokensOnSemiColon[y]);
                        isDocId = true;
                    }
                }
                relDocs_relPosition.put(docId, relevanceRank);
            }
            newQ.setID(id);
            newQ.setQuery(query);
            newQ.setMapOfRelevantDocs(relDocs_relPosition);
            queries.add(newQ);
        }
        return queries;
    }

    private static Integer getTokenValue(String token) {
        String[] result = token.split("=");
        String valueToken = result[1];
        int i = 0;
        valueToken = valueToken.replace("}\"","");
        valueToken = valueToken.replace("]","");
        return parseInt(valueToken);
    }

    public static void main(String[] args) throws IOException, ParseException {
        for(Document Doc : parseJsonFileDocuments("./src/main/resources/dataset.json")) {
            System.out.println("{" + Doc.getID() + "," + Doc.getTitle() + "," + Doc.getBody() + "}");
        }
        for(Query q: parseJsonFileQueries("./src/main/resources/dataset.json")) {
            System.out.println("{" + q.getID() + "," + q.getQuery() + "," + q.getHashMap()+ "}");
        }
    }
}
