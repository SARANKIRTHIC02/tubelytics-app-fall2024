package controllers;

import model.ChannelProfileResult;
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
import static play.mvc.Http.Status.NOT_FOUND;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;
import static play.test.Helpers.contentAsString;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class HomeControllerTest extends WithApplication {
    @InjectMocks
    private HomeController homeController;

    @BeforeEach
    public void setup() {
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

            assertEquals(OK, result.status());
            String responseContent = contentAsString(result);
            assertTrue(responseContent.contains("<h2>Search Results for term: sampleQuery</h2>"));
        }
    }

    @Test
    public void testTaglyticsWithQuery() throws IOException, InterruptedException {
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
            assertTrue(responseContent.contains("<h2>Search Results for term: SampleVideo</h2>"));

        }
    }

    @Test
    public void testWordStatsWithSearchQuery() throws IOException, InterruptedException {
        String searchQuery = "sampleSearch";
        List<VideoSearchResult> mockResults = new ArrayList<>();
        mockResults.add(new VideoSearchResult("video123", "Sample Video", "Sample description", "thumbnail", "channel123", "Sample Channel", null));
        Map<String, Long> mockWordStats = Map.of("sample", 2L, "description", 1L);

        try (MockedStatic<TubelyticService> mockedService = mockStatic(TubelyticService.class)) {
            mockedService.when(() -> TubelyticService.fetchResults(searchQuery)).thenReturn(mockResults);
            mockedService.when(() -> TubelyticService.wordStatistics(mockResults)).thenReturn(mockWordStats);

            Http.RequestBuilder request = new Http.RequestBuilder()
                    .method(GET)
                    .uri("/wordStatistics/" + searchQuery);

            Result result = route(app, request);
            assertEquals(OK, result.status());
            String responseContent = contentAsString(result);
            assertTrue(responseContent.contains("<thead>\n" +
                    "            <tr>\n" +
                    "                <th>Word</th>\n" +
                    "                <th>Frequency</th>\n" +
                    "            </tr>\n" +
                    "            </thead>"));
        }
    }
    @Test
    public void testTags() throws IOException, InterruptedException {
        String videoID = "video123";
        List<VideoSearchResult> mockResults = new ArrayList<>();
        mockResults.add(new VideoSearchResult("video123", "Sample Video", "Sample description", "thumbnail", "channel123", "Sample Channel", List.of("tag1", "tag2", "tag3")));

        try (MockedStatic<TubelyticService> mockedService = mockStatic(TubelyticService.class)) {
            mockedService.when(() -> TubelyticService.fetchResults(videoID)).thenReturn(mockResults);

            Http.RequestBuilder request = new Http.RequestBuilder()
                    .method(GET)
                    .uri("/ytlytics/tags/" + videoID);

            Result result = route(app, request);
            assertEquals(OK, result.status());
        }
    }

    @Test
    public void testChannelVideosFound() {
        String channelID="UCi7Zk9baY1tvdlgxIML8MXg";
        ChannelProfileResult mockChannelProfile=new ChannelProfileResult(channelID, "AVC News","",200000L,"","",Collections.emptyList());


        try (MockedStatic<TubelyticService> mockedService = mockStatic(TubelyticService.class)) {
            mockedService.when(() -> TubelyticService.fetchChannelDetails(channelID)).thenReturn(mockChannelProfile);

            Http.RequestBuilder request = new Http.RequestBuilder()
                    .method(GET)
                    .uri("/ytlytics/channel/" + channelID);

            Result result = route(app, request);
            assertEquals(OK, result.status());
            String responseContent = contentAsString(result);
            System.out.println(responseContent);
            assertTrue(responseContent.contains("<h1 id=\"channelName\">Channel Name: CTV News</h1>"));
        }
    }

    @Test
    public void testChannelVideosNotFound() {
        String channelId = "nonExistentChannel";

        try (MockedStatic<TubelyticService> mockedService = mockStatic(TubelyticService.class)) {
            mockedService.when(() -> TubelyticService.fetchChannelDetails(channelId)).thenReturn(null);

            Http.RequestBuilder request = new Http.RequestBuilder()
                    .method(GET)
                    .uri("/ytlytics/channel/" + channelId);

            Result result = route(app, request);
            assertEquals(NOT_FOUND, result.status());
            String responseContent = contentAsString(result);
            assertEquals("Channel not found with ID: " + channelId, responseContent);
        }
    }
}





