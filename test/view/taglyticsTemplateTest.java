package view;

import model.SearchResponse;
import model.VideoSearchResult;
import org.junit.jupiter.api.Test;
import play.twirl.api.Content;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class taglyticsTemplateTest {
    @Test
    void testTaglyticsViewWithResults() {
        SearchResponse searchResponse = new SearchResponse("Test Search", List.of(
                new VideoSearchResult("video1", "Test Video 1", "This is a test video.", "http://example.com/thumb1.jpg",
                        "channel1", "Test Channel 1", List.of("Tag1", "Tag2")),
                new VideoSearchResult("video2", "Test Video 2", "Another test video.", "http://example.com/thumb2.jpg",
                        "channel2", "Test Channel 2", List.of("Tag3", "Tag4"))
        ));

        Map<String, Long> wordsFiltered = Map.of("Test", 2L);

        Content html = views.html.taglytics.render(searchResponse, wordsFiltered);

        assertTrue(html.body().contains("Search Results for term: Test Search"));
        assertTrue(html.body().contains("More Stats"));
        assertTrue(html.body().contains("Test Video 1"));
        assertTrue(html.body().contains("Test Channel 1"));
        assertTrue(html.body().contains("This is a test video."));
        assertTrue(html.body().contains("Tag1"));
        assertTrue(html.body().contains("Tag2"));
        assertTrue(html.body().contains("Test Video 2"));
        assertTrue(html.body().contains("Test Channel 2"));
        assertTrue(html.body().contains("Another test video."));
        assertTrue(html.body().contains("Tag3"));
        assertTrue(html.body().contains("Tag4"));
        assertTrue(html.body().contains("http://example.com/thumb1.jpg"));
        assertTrue(html.body().contains("http://example.com/thumb2.jpg"));
    }

    @Test
    void testTaglyticsViewNoResults() {
        SearchResponse searchResponse = new SearchResponse("Test Search", List.of());
        Map<String, Long> wordsFiltered = Map.of();

        Content html = views.html.taglytics.render(searchResponse, wordsFiltered);

        assertTrue(html.body().contains("No results found for the term."));
    }

    @Test
    void testTaglyticsViewEmptySearchTerm() {
        SearchResponse searchResponse = new SearchResponse("", List.of());
        Map<String, Long> wordsFiltered = Map.of();

        Content html = views.html.taglytics.render(searchResponse, wordsFiltered);

        assertTrue(html.body().contains("No results found for the term."));
        assertTrue(html.body().contains("More Stats"));
    }

    @Test
    void testTaglyticsViewSingleResult() {
        SearchResponse searchResponse = new SearchResponse("Single Search", List.of(
                new VideoSearchResult("video1", "Test Video 1", "Description", "http://example.com/thumb1.jpg",
                        "channel1", "Test Channel 1", List.of("Tag1"))
        ));

        Map<String, Long> wordsFiltered = Map.of("Test", 1L);

        Content html = views.html.taglytics.render(searchResponse, wordsFiltered);

        assertTrue(html.body().contains("Search Results for term: Single Search"));
        assertTrue(html.body().contains("Test Video 1"));
        assertTrue(html.body().contains("Test Channel 1"));
        assertTrue(html.body().contains("Description"));
        assertTrue(html.body().contains("Tag1"));
        assertTrue(html.body().contains("http://example.com/thumb1.jpg"));
    }
}