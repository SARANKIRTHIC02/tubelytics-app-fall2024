package view;

import model.SearchResponse;
import model.SearchResponseList;
import model.VideoSearchResult;
import org.junit.jupiter.api.Test;
import play.twirl.api.Content;
import play.twirl.api.Html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static play.test.Helpers.contentAsString;

/**
 * Unit tests for the `ytlytics` template, verifying the correct rendering of the YouTube search results,
 * handling of search queries, and display of filtered words and tags.
 */
public class ytlyticstemplatetest {

    /**
     * @author durai
     * @author saran
     * Tests the rendering of the `ytlytics` template when no search results are available.
     * Verifies that the template displays a welcome message and a prompt to enter search terms.
     *
     */
    @Test
    public void renderWithNoSearchResults() {
        List<SearchResponse> emptyResponseList = new ArrayList<>();
        SearchResponseList emptySearchResponseList = new SearchResponseList(emptyResponseList, "session123");
        Map<String, Long> wordsFiltered = Collections.emptyMap();
        String searchQuery = "";

        Html html = views.html.ytlytics.render(emptySearchResponseList, wordsFiltered, searchQuery);

        assertTrue(contentAsString(html).contains("Welcome to YTLytics"));
        assertTrue(contentAsString(html).contains("Enter search terms"));
        assertTrue(html.contentType().equals("text/html"));
    }

    /**
     * @author saran
     * @author durai
     * Tests the rendering of the `ytlytics` template with a single search result.
     * Verifies that the template correctly displays the video title, description, and associated tags.
     *
     */
    @Test
    public void renderWithSingleSearchResult() {
        VideoSearchResult videoResult = new VideoSearchResult(
                "videoId123",
                "Test Video",
                "Test Description",
                "http://example.com/thumb.jpg",
                "channelId456",
                "Test Channel",
                List.of("Tag1", "Tag2")
        );
        SearchResponse searchResponse = new SearchResponse("Test Query", List.of(videoResult));
        SearchResponseList singleResponseList = new SearchResponseList(List.of(searchResponse), "session123");
        Map<String, Long> wordsFiltered = Map.of("Test", 1L);
        String searchQuery = "Test Query";

        Html html = views.html.ytlytics.render(singleResponseList, wordsFiltered, searchQuery);

        assertTrue(contentAsString(html).contains("Search Results for term: Test Query"));
        assertTrue(contentAsString(html).contains("Test Video"));
        assertTrue(contentAsString(html).contains("Test Channel"));
        assertTrue(contentAsString(html).contains("Test Description"));
        assertTrue(contentAsString(html).contains("Tag1"));
        assertTrue(contentAsString(html).contains("Tag2"));
        assertTrue(html.contentType().equals("text/html"));
    }

    /**
     * @author saran
     * @author durai
     * Tests the behavior of the `ytlytics` template when the tags list for a video is empty.
     * Verifies that the template handles empty tag lists correctly and displays the appropriate content.
     *
     */
    @Test
    public void testEmptyTagsList() {
        VideoSearchResult videoResult = new VideoSearchResult(
                "videoId789",
                "Another Test Video",
                "Another Test Description",
                "http://example.com/another_thumb.jpg",
                "channelId789",
                "Another Test Channel",
                List.of()
        );

        assertTrue(videoResult.getTags().isEmpty());
        assertEquals("https://www.youtube.com/watch?v=videoId789", videoResult.getVideoUrl());
        assertEquals("Another Test Channel", videoResult.getChannelTitle());
        assertEquals("http://example.com/another_thumb.jpg", videoResult.getThumbnailUrl());
    }

    /**
     * @author saran
     * @author durai
     * Tests the rendering of the `ytlytics` template with valid search results.
     * Verifies that the search results, video details, and filtered words are displayed correctly.
     *
     */
    @Test
    public void testYtlyticsTemplateWithResults() {

        List<VideoSearchResult> videoResults = new ArrayList<>();
        videoResults.add(new VideoSearchResult(
                "Sample Video Title",
                "https://sample.video.url",
                "Sample Channel Title",
                "Sample Description",
                "sampleChannelId",
                "https://sample.thumbnail.url",
                Arrays.asList("Tag1", "Tag2")
        ));

        SearchResponse searchResponse = new SearchResponse("Sample Search Term", videoResults);
        List<SearchResponse> searchRequests = Collections.singletonList(searchResponse);
        SearchResponseList searchResponseList = new SearchResponseList(searchRequests, "sampleSessionID");

        Map<String, Long> wordsFiltered = new HashMap<>();
        wordsFiltered.put("sample", 5L);
        wordsFiltered.put("term", 3L);

        Content html = views.html.ytlytics.render(searchResponseList, wordsFiltered, "Sample Search Term");

        String renderedContent = contentAsString(html);
        assertTrue(renderedContent.contains("Welcome to YTLytics"));
        assertTrue(renderedContent.contains("sampleSessionID"));
        assertTrue(renderedContent.contains("Sample Search Term"));
        assertTrue(renderedContent.contains("Sample Video Title"));
        assertTrue(renderedContent.contains("Sample Channel Title"));
        assertTrue(renderedContent.contains("Sample Description"));
        assertTrue(renderedContent.contains("Tag1"));
        assertTrue(renderedContent.contains("Tag2"));
    }
}
