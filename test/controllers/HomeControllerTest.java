package controllers;

import model.TubelyticService;
import model.VideoSearchResult;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.*;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;
import static play.test.Helpers.contentAsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HomeControllerTest extends WithApplication {
    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    public void setup(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testIndex() {
        System.out.println("HomeController 1");
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testYtlyticsWithQuery() throws IOException, InterruptedException {
        System.out.println("HomeController 2");
        String query = "sampleQuery";
        List<VideoSearchResult> mockResults = new ArrayList<>();
        mockResults.add(new VideoSearchResult("video123", "Sample Video", "Sample description", "thumbnail", "channel123", "Sample Channel", null));
        Map<String, Long> mockWordStats = Map.of("sample", 2L, "description", 1L);

        try (MockedStatic<TubelyticService> mockedService = mockStatic(TubelyticService.class)) {
            mockedService.when(() -> TubelyticService.fetchResults(query)).thenReturn(mockResults);
            mockedService.when(() -> TubelyticService.wordStatistics(mockResults)).thenReturn(mockWordStats);

            Http.RequestBuilder request = new Http.RequestBuilder()
                    .method(GET)
                    .uri("/ytlytics?query=" + query);

            Result result = route(app, request);

            // Assert: Check status and response content
            assertEquals(OK, result.status());
            String responseContent = contentAsString(result);
            assertTrue(responseContent.contains("<h2>Search Results for term: sampleQuery</h2>")); // Adjust based on actual response format
        }
    }

    @Test
    public void testTaglyticsWithQuery_MockedResponse() throws IOException, InterruptedException {
        System.out.println("HomeController 3");
        String query = "SampleVideo";
        List<VideoSearchResult> mockResults = new ArrayList<>();
        mockResults.add(new VideoSearchResult("video123", "SampleVideo", "Sample description", "thumbnail", "channel123", "Sample Channel", List.of("tag1", "tag2")));
        Map<String, Long> mockWordStats = Map.of("sample", 2L, "description", 1L);

        try (MockedStatic<TubelyticService> mockedService = mockStatic(TubelyticService.class)) {
            mockedService.when(() -> TubelyticService.fetchResults(query)).thenReturn(mockResults);
            mockedService.when(() -> TubelyticService.wordStatistics(mockResults)).thenReturn(mockWordStats);

            Http.RequestBuilder request = new Http.RequestBuilder()
                    .method(GET)
                    .uri("/ytlytics/tags/" + query);

            Result result = route(app, request);
            assertEquals(OK, result.status());
            String responseContent = contentAsString(result);
            assertTrue(responseContent.contains("<h2>Search Results for term: SampleVideo</h2>")); // Adjust based on actual response format

        }
    }

   /* @Test
    public void testWordStatsWithSearchQuery() {
        String searchQuery = "sampleSearch";
        String mockWordStatsData = "Mocked word stats data";

        try (MockedStatic<YouTubeService> mockedService = mockStatic(YouTubeService.class)) {
            mockedService.when(() -> YouTubeService.fetchWordStats(searchQuery)).thenReturn(mockWordStatsData);

            Http.RequestBuilder request = new Http.RequestBuilder()
                    .method(GET)
                    .uri("/wordStatistics/" + searchQuery);

            Result result = route(app, request);
            assertEquals(OK, result.status());
            assertEquals("Mocked word stats data", contentAsString(result));
        }
    }

    @Test
    public void testWordStatsWithoutSearchQuery() {
        String mockWordStatsData = "Mocked empty word stats data";

        try (MockedStatic<YouTubeService> mockedService = mockStatic(YouTubeService.class)) {
            mockedService.when(() -> YouTubeService.fetchWordStats("")).thenReturn(mockWordStatsData);

            Http.RequestBuilder request = new Http.RequestBuilder()
                    .method(GET)
                    .uri("/wordStatistics");

            Result result = route(app, request);
            assertEquals(OK, result.status());
            assertEquals("Mocked empty word stats data", contentAsString(result));
        }
    }*/
}





