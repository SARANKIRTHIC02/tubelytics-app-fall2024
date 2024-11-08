package model;
import java.util.List;

/**
 * The class represents a response containing a title and a list of video search results.
 * This class is used to encapsulate the search results from a specific query or topic.
 */
public class SearchResponse {
    private String title;
    private List<VideoSearchResult> results;

    /**
     * Constructor.
     *
     * @param title the title or query associated with the search results.
     * @param results a list of VideoSearchResult representing the video results for the search.
     */
    public SearchResponse(String title, List<VideoSearchResult> results) {
        this.title = title;
        this.results = results;
    }

    /**
     * Gets the title or query associated with the search results.
     *
     * @return the title of the search response.
     */
    public String getTitle() {
        return title;
    }


    /**
     * Gets the list of video search results.
     *
     * @return a list of VideoSearchResult representing the video search results.
     */
    public List<VideoSearchResult> getResults() {
        return results;
    }


}
