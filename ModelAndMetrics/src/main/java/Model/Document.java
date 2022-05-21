package Model;

public class Document {
    private int ID;
    private String objectID; // Algolia required field
    private String Title;
    private String Body;

    public Document() {
        this.ID = -1;
        this.objectID = "";
        this.Title = "";
        this.Body = "";
    }

    public Document(int id, String title, String body) {
        this.ID = id;
        this.objectID = Integer.toString(id);
        this.Title = title;
        this.Body = body;
    }

    public void setID(int id) {
        this.ID = id;
        this.objectID = Integer.toString(id);
    }

    public int getID() {
        return this.ID;
    }

    public String getObjectID() {
        return this.objectID;
    }

    public void setTitle(String title) {
        this.Title = title;
    }

    public String getTitle() {
        return this.Title;
    }

    public void setBody(String body) {
        this.Body = body;
    }

    public String getBody() {
        return this.Body;
    }
}
