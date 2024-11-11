package view;

import model.SearchResponse;
import model.VideoSearchResult;
import org.junit.Test;
import play.twirl.api.Content;
import play.test.WithApplication;

import java.util.*;

import static org.junit.Assert.assertTrue;
import static play.test.Helpers.contentAsString;

/**
 * Unit tests for the taglytics template, verifying the correct rendering of search results
 * along with associated word statistics. Tests both the scenarios where search results
 * are present and where no results are found.
 */
public class taglyticsTemplateTest extends WithApplication {

    /**
     * Tests the rendering of the taglytics template when there are video search results available.
     * Verifies that the search term, video details, and associated tags are correctly displayed.
     *
     */
    @Test
    public void testTaglyticsTemplateWithResults() {
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
        Map<String, Long> wordsFiltered = new HashMap<>();
        wordsFiltered.put("sample", 5L);
        wordsFiltered.put("term", 3L);

        Content html = views.html.taglytics.render(searchResponse, wordsFiltered);

        String renderedContent = contentAsString(html);
        assertTrue(renderedContent.contains("Search Results for term: Sample Search Term"));
        assertTrue(renderedContent.contains("Sample Video Title"));
        assertTrue(renderedContent.contains("Sample Channel Title"));
        assertTrue(renderedContent.contains("Sample Description"));
        assertTrue(renderedContent.contains("Tag1"));
        assertTrue(renderedContent.contains("Tag2"));
    }

    /**
     * @author saran
     * @author durai
     * Tests the rendering of the taglytics template when no video search results are found.
     * Verifies that the message indicating no results is correctly displayed.
     *
     */
    @Test
    public void testTaglyticsTemplateWithoutResults() {
        SearchResponse searchResponse = new SearchResponse("Sample Search Term", Collections.emptyList());
        Map<String, Long> wordsFiltered = new HashMap<>();

        Content html = views.html.taglytics.render(searchResponse, wordsFiltered);

        String renderedContent = contentAsString(html);
        assertTrue(renderedContent.contains("Search Results for term: Sample Search Term"));
        assertTrue(renderedContent.contains("No results found for the term."));
    }
}
