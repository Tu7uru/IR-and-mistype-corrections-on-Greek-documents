package service;

import Model.Document;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

public class SolrParser {
    public static List<Document> parseSolrDocuments(SolrDocumentList docs) {
        List<Document> documents = new ArrayList<>();
        int ID;
        String Title;
        String Body;

        for(SolrDocument sDoc : docs) {
            Document newDoc = new Document();
            //ID = parseInt(sDoc.get("ID").toString());

            ID = Integer.parseInt(sDoc.get("ID").toString().replace("[","").replace("]",""));
            Title = sDoc.get("Title").toString().substring(1,sDoc.get("Title").toString().length() -1);
            Body = sDoc.get("Body").toString().substring(1,sDoc.get("Body").toString().length() -1);

            newDoc.setID(ID);
            newDoc.setTitle(Title);
            newDoc.setBody(Body);

            documents.add(newDoc);
        }
        return documents;
    }
}
