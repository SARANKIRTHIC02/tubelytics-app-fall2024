package model;

import Util.HttpUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class YouTubeServiceTest {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void testSearchVideosBasedOnQueryWithValidResponse() throws IOException, InterruptedException {
        String mockApiResponse = "{ \"items\": [ " +
                "{ \"id\": { \"videoId\": \"testVideoID\" }, " +
                "\"snippet\": { \"title\": \"Test Title\", \"description\": \"Test Description\", \"thumbnails\": { \"default\": { \"url\": \"testUrl\" } }, \"channelId\": \"testChannelId\", \"channelTitle\": \"Test Channel\"} } ] }";
        JsonNode mockResponse = objectMapper.readTree(mockApiResponse);
        try (MockedStatic<HttpUtils> mockedHttpUtils = Mockito.mockStatic(HttpUtils.class)) {
            mockedHttpUtils.when(() -> HttpUtils.sendRequest(anyString())).thenReturn(mockResponse);

            List<VideoSearchResult> results = YouTubeService.searchVideosBasedOnQuery("test_query");

            Assert.assertNotNull(results);
            Assert.assertEquals(results.size(), 1);
            Assert.assertEquals("testVideoID", results.get(0).getVideoId());
            Assert.assertEquals("Test Title", results.get(0).getTitle());
        }
    }

    @Test
    public void testSearchVideosBasedOnQueryWithNullResponse() throws IOException, InterruptedException {
        try (MockedStatic<HttpUtils> mockedHttpUtils = Mockito.mockStatic(HttpUtils.class)) {
            mockedHttpUtils.when(() -> HttpUtils.sendRequest(anyString())).thenReturn(null);
            List<VideoSearchResult> results = YouTubeService.searchVideosBasedOnQuery("test query");
            Assert.assertNotNull(results);
            Assert.assertEquals(0, results.size());
        }
    }

    @Test
    public void testGetChannelProfile_withValidResponse() throws IOException, InterruptedException {
        String mockChannelApiResponse = "{ \"items\": [ { \"id\": \"testChannelId\", \"snippet\": { \"title\": \"Test Channel Title\", \"description\": \"Test Channel Description\", \"thumbnails\": { \"default\": { \"url\": \"testThumbnailUrl\" } }, \"country\": \"US\" }, \"statistics\": { \"subscriberCount\": \"1000\" } } ] }";
        String mockVideoApiResponse = "{ \"items\": [ { \"id\": { \"videoId\": \"testVideoId\" }, \"snippet\": { \"title\": \"Test Video\", \"description\": \"Test Description\", \"thumbnails\": { \"default\": { \"url\": \"testVideoThumbnailUrl\" } }, \"channelId\": \"testChannelId\", \"channelTitle\": \"Test Channel\", \"publishedAt\": \"2023-01-01T00:00:00Z\" } } ] }";

        JsonNode mockChannelResponse = objectMapper.readTree(mockChannelApiResponse);
        JsonNode mockVideoResponse = objectMapper.readTree(mockVideoApiResponse);

        try (MockedStatic<HttpUtils> mockedHttpUtils = Mockito.mockStatic(HttpUtils.class)) {
            mockedHttpUtils.when(() -> HttpUtils.sendRequest(anyString()))
                    .thenReturn(mockChannelResponse, mockVideoResponse);

            ChannelProfileResult profile = YouTubeService.getChannelProfile("testChannelId");

            Assert.assertNotNull(profile);
            Assert.assertEquals("testChannelId", profile.getChannelId());
            List<VideoSearchResult> recentVideos = profile.getRecentVideos();
            Assert.assertNotNull(recentVideos);
            Assert.assertEquals(1, recentVideos.size());

        }
    }


}
