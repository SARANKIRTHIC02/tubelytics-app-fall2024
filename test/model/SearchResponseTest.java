package model;

import org.junit.Before;
import org.junit.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Unit tests for the SearchResponse class.
 * This class ensures the correctness of the SearchResponse model,
 * focusing on getter methods and constructors under different conditions.
 *
 * @version 1.0
 * @author
 *   - Sushanth
 */
public class SearchResponseTest {

    private SearchResponse searchResponse;
    private String title;
    private List<VideoSearchResult> results;

    /**
     * Sets up the test environment for SearchResponse.
     * Initializes a SearchResponse instance with a sample title and a list of mock VideoSearchResult objects.
     * @author Sushanth
     */
    @Before
    public void setUp() {
        title = "Sample Title";
        results = new ArrayList<>();

        // Creating mock VideoSearchResult objects
        results.add(new VideoSearchResult(
                "videoId1", "Video1", "Description1", "ThumbnailURL1",
                "channelId1", "ChannelTitle1", Arrays.asList("tag1", "tag2")
        ));
        results.add(new VideoSearchResult(
                "videoId2", "Video2", "Description2", "ThumbnailURL2",
                "channelId2", "ChannelTitle2", Arrays.asList("tag3", "tag4")
        ));

        searchResponse = new SearchResponse(title, results);
    }

    /**
     * Tests the getTitle method of the SearchResponse class.
     * Verifies that the title is returned correctly.
     *
     * @author Sushanth
     */
    @Test
    public void testGetTitle() {
        assertEquals("Sample Title", searchResponse.getTitle(), "Title matches.");
    }

    /**
     * Tests the getResults method of the SearchResponse class.
     * Verifies that the results list is returned correctly and matches the expected size and content.
     *
     * @author Sushanth
     */
    @Test
    public void testGetResults() {
        List<VideoSearchResult> retrievedResults = searchResponse.getResults();

        assertNotNull(retrievedResults, "Result not null.");
        assertEquals(2, retrievedResults.size(), "Results list size should match.");

        assertEquals("Video1", retrievedResults.get(0).getTitle(), "The first video's title matches.");
        assertEquals("Video2", retrievedResults.get(1).getTitle(), "The second video's title matches.");
    }

    /**
     * Tests the SearchResponse constructor with an empty results list.
     * Verifies that the title is set correctly and the results list is empty.
     *
     * @author Sushanth
     */
    @Test
    public void testConstructorWithEmptyResults() {
        SearchResponse emptyResponse = new SearchResponse("Empty Title", new ArrayList<>());

        assertEquals("Empty Title", emptyResponse.getTitle(), "Title matches.");
        assertTrue(emptyResponse.getResults().isEmpty(), "Results list is empty.");
    }

    /**
     * Tests the SearchResponse constructor with null results.
     * Verifies that the title is set correctly and the results list is null.
     *
     * @author Sushanth
     */
    @Test
    public void testConstructorWithNullResults() {
        SearchResponse nullResultsResponse = new SearchResponse("Null Results Title", null);
        assertEquals("Null Results Title", nullResultsResponse.getTitle(), "Title matches.");
        assertNull(nullResultsResponse.getResults(), "Results list is null.");
    }
}
