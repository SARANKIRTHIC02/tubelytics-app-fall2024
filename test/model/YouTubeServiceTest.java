package model;

import Util.HttpUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;

public class YouTubeServiceTest {
    @After
    public void tearDown() {
        Mockito.framework().clearInlineMocks();
    }

    /**
     * Tests searchVideosBasedOnQuery with a valid API response.
     * Verifies that the result list contains one video with expected video ID and title.
     *
     * @throws IOException if there is an error reading JSON
     * @throws InterruptedException if the request is interrupted
     * @author durai
     * @author saran
     */
    @Test
    public void testSearchVideosBasedOnQueryWithValidResponse() throws IOException, InterruptedException {
        String mockApiResponse = "{ \"items\": [ " +
                "{ \"id\": { \"videoId\": \"testVideoID\" }, " +
                "\"snippet\": { \"title\": \"Test Title\", \"description\": \"Test Description\", " +
                "\"thumbnails\": { \"default\": { \"url\": \"testUrl\" } }, " +
                "\"channelId\": \"testChannelId\", \"channelTitle\": \"Test Channel\"} } ] }";

        ObjectMapper objectMapper=new ObjectMapper();
        JsonNode mockResponse = objectMapper.readTree(mockApiResponse);

        try (MockedStatic<HttpUtils> mockedHttpUtils = Mockito.mockStatic(HttpUtils.class)) {
            mockedHttpUtils.when(() -> HttpUtils.sendRequest(anyString())).thenReturn(mockResponse);

            List<VideoSearchResult> results = YouTubeService.searchVideosBasedOnQuery("test_query");
            assertNotNull(results);
            assertEquals(results.size(), 1);
            assertEquals("testVideoID", results.get(0).getVideoId());
            assertEquals("Test Title", results.get(0).getTitle());
        }
    }

    /**
     * Tests searchVideosBasedOnQuery with a null API response.
     *
     * @throws IOException if there is an error reading JSON
     * @throws InterruptedException if the request is interrupted
     * @author durai
     * @author saran
     */
    @Test
    public void testSearchVideosBasedOnQueryWithNullResponse() throws IOException, InterruptedException {
        try (MockedStatic<HttpUtils> mockedHttpUtils = Mockito.mockStatic(HttpUtils.class)) {
            mockedHttpUtils.when(() -> HttpUtils.sendRequest(anyString())).thenReturn(null);
            List<VideoSearchResult> results = YouTubeService.searchVideosBasedOnQuery("test query");
            assertNotNull(results);
            assertEquals(0, results.size());
        }
    }

    /**
     * Tests getChannelProfile with a valid API response.
     * Verifies that the profile contains the correct channel ID and a non-empty list of recent videos.
     *
     * @throws IOException if there is an error reading JSON
     * @throws InterruptedException if the request is interrupted
     * @author durai
     * @author sushanth
     */
    @Test
    public void testGetChannelProfileWithValidResponse() throws IOException, InterruptedException {
        String mockChannelApiResponse = "{ \"items\": [ " +
                "{ \"id\": \"testChannelId\", " +
                "\"snippet\": { \"title\": \"Test Channel Title\", \"description\": \"Test Channel Description\", \"thumbnails\": { \"default\": { \"url\": \"testThumbnailUrl\" } }, \"country\": \"US\" }," +
                " \"statistics\": { \"subscriberCount\": \"1000\" } } ] }";
        String mockVideoApiResponse = "{ \"items\": [ { \"id\": { \"videoId\": \"testVideoId\" }, \"snippet\": { \"title\": \"Test Video\", \"description\": \"Test Description\", \"thumbnails\": { \"default\": { \"url\": \"testVideoThumbnailUrl\" } }, \"channelId\": \"testChannelId\", \"channelTitle\": \"Test Channel Title\" } } ] }";

        ObjectMapper objectMapper=new ObjectMapper();
        JsonNode mockChannelResponse = objectMapper.readTree(mockChannelApiResponse);
        JsonNode mockVideoResponse = objectMapper.readTree(mockVideoApiResponse);

        try (MockedStatic<HttpUtils> mockedHttpUtils = Mockito.mockStatic(HttpUtils.class)) {
            mockedHttpUtils.when(() -> HttpUtils.sendRequest(anyString()))
                    .thenReturn(mockChannelResponse, mockVideoResponse);

            ChannelProfileResult profile = YouTubeService.getChannelProfile("testChannelId");

            assertNotNull(profile);
            assertEquals("testChannelId", profile.getChannelId());
            List<VideoSearchResult> recentVideos = profile.getRecentVideos();
            assertNotNull(recentVideos);
            assertEquals("testChannelId",recentVideos.get(0).getChannelId());
            assertEquals(1, recentVideos.size());

        }
    }


    /**
     * Tests getChannelProfile with an empty response for a non-existent channel.
     * Verifies that the returned profile is null.
     *
     * @throws IOException if there is an error reading JSON
     * @throws InterruptedException if the request is interrupted
     * @author durai
     * @author sushanth
     */
    @Test
    public void testGetChannelProfileWithNullResponse() throws IOException, InterruptedException {
        String mockEmptyItemsResponse = "{ \"items\": [] }";
        ObjectMapper objectMapper=new ObjectMapper();
        JsonNode mockResponse = objectMapper.readTree(mockEmptyItemsResponse);

        try (MockedStatic<HttpUtils> mockedHttpUtils = Mockito.mockStatic(HttpUtils.class)) {
            mockedHttpUtils.when(() -> HttpUtils.sendRequest(anyString())).thenReturn(mockResponse);
            ChannelProfileResult profile = YouTubeService.getChannelProfile("testChannelId");
            Assert.assertNull(profile);
        }
    }
}
