package view;

import model.SearchResponse;
import model.SearchResponseList;
import model.VideoSearchResult;
import model.YouTubeService;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import play.test.WithApplication;
import play.twirl.api.Content;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import static play.test.Helpers.contentAsString;

import java.util.*;

public class ytlyticsTemplateTest extends WithApplication {
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

