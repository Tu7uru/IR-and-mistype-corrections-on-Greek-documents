package service;

import Model.Document;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.util.ArrayList;
import java.util.List;

public class SolrParser {
    public static ArrayList<Document> parseSolrDocuments(SolrDocumentList docs) {
        ArrayList<Document> documents = new ArrayList<>();
        int ID;
        String Title;
        String Body;

        for(SolrDocument sDoc : docs) {
            Document newDoc = new Document();

            ID = Integer.parseInt(sDoc.get("ID").toString().substring(1,sDoc.get("ID").toString().length() - 1));
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
