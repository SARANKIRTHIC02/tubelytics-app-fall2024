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

/**
 * Test suite for the Homecontroller class.
 * This class contains unit tests for the actions in the HomeController,
 * ensuring that the endpoints return the correct HTTP responses and content
 * based on different inputs and mocked services.
 * @author durai
 * @author saran
 * @author sushanth
 */
public class HomeControllerTest extends WithApplication {
    @InjectMocks
    private HomeController homeController;
    /**
     * Set up mock annotations before each test case.
     */
    @BeforeEach
    public void setup() {

        MockitoAnnotations.openMocks(this);
    }
    /**
     * Test the index route of the application ("/").
     * Verifies that the response returns HTTP status 200 (OK).
     * @author saran
     */
    @Test
    public void testIndex() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }
    /**
     * Test the "/ytlytics" route with a search query.
     * This test mocks the service calls for fetching search results and word statistics,
     * and verifies that the response contains the expected search results content.
     *
     * @throws IOException            If an I/O error occurs during the test.
     * @throws InterruptedException   If the test is interrupted.
     *  @author durai
     */
   @Test
    public void testYtlyticsWithQuery() throws IOException, InterruptedException {
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
    /**
     * Test the "/ytlytics/tags/{query}" route with a search query that includes tags.
     * This test mocks the service calls for fetching search results and word statistics,
     * and verifies that the response contains the expected search results content, including tags.
     *
     * @throws IOException            If an I/O error occurs during the test.
     * @throws InterruptedException   If the test is interrupted.
     * @author sushanth
     */
    @Test
    public void testTaglyticsWithQuery() throws IOException, InterruptedException {
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
    /**
     * Test the "/wordStatistics/{searchQuery}" route with a search query.
     * This test verifies that the word statistics are correctly calculated and displayed.
     *
     * @throws IOException            If an I/O error occurs during the test.
     * @throws InterruptedException   If the test is interrupted.
     *  @author saran
     */
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

    /**
     * Test the "/ytlytics/tags/{videoID}" route for video tags.
     * This test mocks the service call to fetch the tags of a video and verifies that the response is successful.
     *
     * @throws IOException            If an I/O error occurs during the test.
     * @throws InterruptedException   If the test is interrupted.
     * @author sushanth
     */
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
    /**
     * Test the "/ytlytics/channel/{channelID}" route when the channel details are found.
     * This test mocks the service call to fetch channel details and verifies that the response returns the correct channel information.
     * @author durai
     */
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
            assertTrue(responseContent.contains("<h1 id=\"channelName\">Channel Name: CTV News</h1>"));
        }
    }
    /**
     * Test the "/ytlytics/channel/{channelID}" route when no channel details are found.
     * This test mocks the service call to simulate a non-existent channel and verifies that the response returns a "Not Found" status.
     * @author durai
     */
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