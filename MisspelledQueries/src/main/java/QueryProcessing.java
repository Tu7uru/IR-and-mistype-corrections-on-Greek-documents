import LexicalAnalysis.JsonReader;
import Model.Query;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.StringTokenizer;

import static LexicalAnalysis.JsonReader.parseJsonFileQueries;

public class QueryProcessing {

    public static String RemovePunctuations;

    public static ArrayList<String> TokenizeQueries(ArrayList<Query> queries){
        //ArrayList<ArrayList<String>> tokenizedQueries = new ArrayList<>();
        ArrayList<String> tokenizedQueries = new ArrayList<>();
        int queryIndex = 0;
        int queryTokenIndex = 0;

        for(Query query : queries) {
            //ArrayList<String> queryTokens = new ArrayList<>();
            StringTokenizer queryTokenized = new StringTokenizer(query.getQuery());
            while (queryTokenized.hasMoreTokens()) {
                String token = new String(queryTokenized.nextToken());
                System.out.println(token);
                //queryTokens.add(token);
                tokenizedQueries.add(token);
            }
            //tokenizedQueries.add(queryTokens);
        }

        return tokenizedQueries;
    }

    public static void main(String[] args) throws IOException, ParseException {
        ArrayList<Query> queries = JsonReader.parseJsonFileQueries("C:\\Users\\Haralampos  Varsamis\\uoc\\ptyxiaki\\ir-from-greek-documents\\dataset\\dataset.json");
        System.out.println(TokenizeQueries(queries));
    }
}
