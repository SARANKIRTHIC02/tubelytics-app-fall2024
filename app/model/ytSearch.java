package model;
import java.util.List;

public class ytSearch {
    private String title;
    private List<String> results;

    public String getSessionId() {
        return sessionId;
    }

    private String sessionId;


    public ytSearch(String title, List<String> results, String sessionId) {
        this.title = title;
        this.results = results;
        this.sessionId = sessionId;
    }

    public String getTitle() {
        return title;
    }

    public List<String> getResults() {
        return results;
    }
}
