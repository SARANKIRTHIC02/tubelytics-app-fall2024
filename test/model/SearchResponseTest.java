package model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SearchResponseTest {

    private SearchResponse searchResponse;
    private String title;
    private List<VideoSearchResult> results;


    @BeforeEach
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

    @Test
    public void testGetTitle() {
        assertEquals("Sample Title", searchResponse.getTitle(), "Title matches.");
    }

    @Test
    public void testGetResults() {
        List<VideoSearchResult> retrievedResults = searchResponse.getResults();

        assertNotNull(retrievedResults, "Result not null.");
        assertEquals(2, retrievedResults.size(), "Results list size should matches");

        assertEquals("Video1", retrievedResults.get(0).getTitle(), "the first video's title matches.");
        assertEquals("Video2", retrievedResults.get(1).getTitle(), "the second video's title matches.");
    }

    @Test
    public void testConstructorWithEmptyResults() {
        SearchResponse emptyResponse = new SearchResponse("Empty Title", new ArrayList<>());

        assertEquals("Empty Title", emptyResponse.getTitle(), "Title matches");
        assertTrue(emptyResponse.getResults().isEmpty(), "Empty Results.");
    }

    @Test
    public void testConstructorWithNullResults() {
        SearchResponse nullResultsResponse = new SearchResponse("Null Results Title", null);
        assertEquals("Null Results Title", nullResultsResponse.getTitle(), "Title matches.");
        assertNull(nullResultsResponse.getResults(), "Null Results.");
    }
}

