package service;

import Model.Document;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;


import java.nio.charset.StandardCharsets;

import static java.lang.Boolean.TRUE;
import static service.SolrParser.parseSolrDocuments;

public class SolrQueryBuilder {

    public String formatSolrQuery(String WebQuery,Boolean AllQueryTermsMustExist) {
        if(AllQueryTermsMustExist) {
            return "Title:" + WebQuery + " Body:" + WebQuery;
        }
        else {
            String query = "";
            // split on special characters and create query on each token
            String[] webQueryTokenized = WebQuery.split("[\\s@&.?$+-]+");
            for(String queryToken : webQueryTokenized) {
                query += " Title:" + queryToken + " Body:" + queryToken;
            }
            return query;
        }
    }

    public SolrDocumentList solrQuery(String WebQuery,Boolean RemoveStopwords,Boolean ApplyStemmer,Boolean AllQueryTermsMustExist) {
        SolrDocumentList list = new SolrDocumentList();
        String solrHost;
        /* different datasets, with different indexing policy */
        if(RemoveStopwords && ApplyStemmer) {
            solrHost = "http://localhost:8983/solr/dataset_stopNstem";
        }
        else if(RemoveStopwords) {
            solrHost = "http://localhost:8983/solr/dataset_stop";
        }
        else if(ApplyStemmer) {
            solrHost = "http://localhost:8983/solr/dataset_stem";
        }
        else {
            solrHost = "http://localhost:8983/solr/dataset";
        }
        String query;
        try {

            SolrClient solrClient = new HttpSolrClient.Builder(solrHost).build();

            //query = "Title:" + webQuery + " Body:" + webQuery;
            query = formatSolrQuery(WebQuery,AllQueryTermsMustExist);
            System.out.println(query);
            SolrQuery solrQ = new SolrQuery();

            solrQ.setQuery(query);

            String finalQuery = solrQ.toString();

            QueryResponse response = solrClient.query(solrQ);

            list = response.getResults();
            //System.out.println(list.get(0).getFieldValue("Body"));
            //System.out.println(list.get(0).get("ID").toString().replace("]",""));
            //System.out.println("HWG: " + (list.get(0).get("Title").toString()));
            //list.stream().forEach(System.out::println);
            //System.out.print(utf8EncodedString);
        }catch ( Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static void main(String[] args) {
        SolrQueryBuilder builder = new SolrQueryBuilder();
        SolrDocumentList results = builder.solrQuery("αφρική",TRUE, TRUE,Boolean.FALSE);
        for(Document d : parseSolrDocuments(results)) {
            System.out.println(d.getID() + ", " + d.getTitle() + ", " + d.getBody());
        }
    }
}
