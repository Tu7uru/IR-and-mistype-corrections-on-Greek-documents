package Model;

import java.util.HashMap;

public class Query {
    int ID;
    private String Query;
    private HashMap<Integer,Integer> relevantDocID_relevancePosition;

    public void setID(int id) {
        this.ID = id;
    }

    public int getID() {
        return this.ID;
    }

    public void setQuery(String query) {
        this.Query = query;
    }

    public String getQuery() {
        return this.Query;
    }

    public void setMapOfRelevantDocs(HashMap<Integer,Integer> map) {
        this.relevantDocID_relevancePosition = map;
    }
    public HashMap<Integer,Integer> getHashMap() {
        return this.relevantDocID_relevancePosition;
    }

    public void addMapKeyValue(Integer key,Integer value) {
        this.relevantDocID_relevancePosition.put(key,value);
    }

    public Integer getMapValue(Integer key) {
        return this.relevantDocID_relevancePosition.get(key);
    }
}
