package Model;

public class Document {
    private int ID;
    private String Title;
    private String Body;

    public Document() {
        this.ID = -1;
        this.Title = "";
        this.Body = "";
    }

    public Document(int id, String title, String body) {
        this.ID = id;
        this.Title = title;
        this.Body = body;
    }

    public void setID(int id) {
        this.ID = id;
    }

    public int getID() {
        return this.ID;
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
