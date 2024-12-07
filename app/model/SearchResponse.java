package model;
import java.util.List;

/**
 *
 * The class represents a response containing a title and a list of video search results.
 * This class is used to encapsulate the search results from a specific query or topic.
 * @author durai
 */
public class SearchResponse {
    private String title;
    private List<VideoSearchResult> results;

    /**
     *
     * Constructor.
     *
     * @param title the title or query associated with the search results.
     * @param results a list of VideoSearchResult representing the video results for the search.
     *                @author durai
     */
    public SearchResponse(String title, List<VideoSearchResult> results) {
        this.title = title;
        this.results = results;
    }

    /**
     *
     * Gets the title or query associated with the search results.
     *
     * @return the title of the search response.
     * @author durai
     */
    public String getTitle() {
        return title;
    }


    /**
     *
     * Gets the list of video search results.
     *
     * @return a list of VideoSearchResult representing the video search results.
     * @author durai
     */
    public List<VideoSearchResult> getResults() {
        return results;
    }
}
