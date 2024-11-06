package model;
import java.util.List;

public class SearchResponse {
    private String title;
    private List<VideoSearchResult> results;



    public SearchResponse(String title, List<VideoSearchResult> results) {
        this.title = title;
        this.results = results;
    }

    public String getTitle() {
        return title;
    }

    public List<VideoSearchResult> getResults() {
        return results;
    }


}
