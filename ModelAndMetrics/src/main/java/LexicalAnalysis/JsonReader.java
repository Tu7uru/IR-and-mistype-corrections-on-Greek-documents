package LexicalAnalysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;

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
    ArrayList<Query> consonants;

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

    public static List<String> parseJsonStopwords(String path) throws IOException, ParseException {
        List<String> stopwords = new ArrayList<>();

        ArrayList<Query> queries = new ArrayList<>();

        JSONParser parser = new JSONParser();
        //JSONParser relDocs_parser = new JSONParser();
        Object jsonData = parser.parse(new FileReader(path));
        //JSONArray jsonQueries = (JSONArray) ((JSONObject) jsonData).get("Queries");
        for(Object x : (JSONArray)jsonData) {
            stopwords.add(x.toString().toLowerCase());
        }
        return stopwords;
    }

    public static ArrayList<Document> parseJsonFileDocuments(String path) throws IOException, ParseException {

        int id;
        String title;
        String body;

        ArrayList<Document> documents = new ArrayList<>();

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
        return documents;
    }

    public static ArrayList<Query> parseJsonFileQueries(String path) throws IOException, ParseException {
        int id;
        String query;
        HashMap<Integer, Integer> relDocs_relPosition;

        int docId = -1;
        int relevanceRank = -1;
        Boolean isDocId = true;

        ArrayList<Query> queries = new ArrayList<>();

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

    public static ArrayList<ArrayList<Query>> parseJsonFileConsonants(String path) throws IOException, ParseException {
        ArrayList<ArrayList<Query>> allConsonantQueries = new ArrayList<>();
        ArrayList<Query> consonantsList;

        JSONParser parser = new JSONParser();
        Object jsonData  = parser.parse(new FileReader(path));
        JSONArray jsonConsonants = (JSONArray) ((JSONObject) jsonData).get("Consonants");
        for(Object consonantObj : jsonConsonants) {
            JSONArray Words = (JSONArray) ((JSONObject) consonantObj).get("Words");
            for(Object word : Words) {
                Iterator<String> keys = ((JSONObject) word).keySet().iterator();
                consonantsList = new ArrayList<>();
                while(keys.hasNext()) {
                    String key = keys.next();

                    Query query = new Query();
                    query.setQuery(((JSONObject) word).get(key).toString().trim());
                    consonantsList.add(query);
                }
                allConsonantQueries.add(consonantsList);
            }
        }
        return allConsonantQueries;
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
/*        parseJsonStopwords("./src/main/resources/el-stopwords.json");
        for(Document Doc : parseJsonFileDocuments("./src/main/resources/dataset.json")) {
            System.out.println("{" + Doc.getID() + "," + Doc.getTitle() + "," + Doc.getBody() + "}");
        }
        for(Query q: parseJsonFileQueries("src/main/resources/dataset.json")) {
            System.out.println("{" + q.getID() + "," + q.getQuery() + "," + q.getHashMap()+ "}");
        }*/
        for(ArrayList<Query> listQ : parseJsonFileConsonants("./src/main/resources/consonants.json")) {
            System.out.print("{");
            for(Query q: listQ) {
                System.out.print(" "+q.getQuery());
            }
            System.out.println("}");
        }
    }
}
