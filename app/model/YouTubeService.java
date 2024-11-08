package model;

import Util.HttpUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.ConfigException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class YouTubeService {
    private static final String apiKey;
    private static final String baseUrl;
    private static final String searchEndpoint;
    private static final String channelEndpoint;
    private static final String videosEndpoint;

    static {
        try {
            apiKey = "AIzaSyCL43QCR0kOW8iDEgmvgwybGfcaJCgKH10";
            baseUrl = "https://www.googleapis.com/youtube/v3";
            searchEndpoint = "/search?part=snippet";
            channelEndpoint = "/channels?part=snippet,statistics";
            videosEndpoint = "/videos?part=snippet,statistics";
        } catch (ConfigException.Missing e) {
            throw new RuntimeException("YouTube API configuration issuef", e);
        }
    }

    public static String getApiKey() {
        return apiKey;
    }

    public static String getApiBaseUrl() {
        return baseUrl;
    }

    public static String getSearchEndpoint() {
        return searchEndpoint;
    }

    public static String getChannelEndpoint() {
        return channelEndpoint;
    }

    public static String getVideosEndpoint() {
        return videosEndpoint;
    }

    public static List<VideoSearchResult> searchVideosBasedOnQuery(String query) throws IOException, InterruptedException {
        String apiUrl = String.format("%s%s&q=%s&type=video&maxResults=%d&order=date&key=%s",
                getApiBaseUrl(), getSearchEndpoint(), query, 50, getApiKey());
        JsonNode response = HttpUtils.sendRequest(apiUrl);

        List<VideoSearchResult> results = parseVideoResults(response);

        List<String> videoIds = getVideoIds(results);
        fetchVideoTags(videoIds, results);

        System.out.println("Line 33:   "+results);

        return results;
    }

    public static ChannelProfileResult getChannelProfile(String channelId) throws IOException, InterruptedException {
        String apiUrl = String.format("%s%s&id=%s&key=%s", getApiBaseUrl(), getChannelEndpoint(), channelId, getApiKey());
        JsonNode response = HttpUtils.sendRequest(apiUrl);

        if (!response.has("items") || response.get("items").isEmpty()) {
            System.out.println("No channel found with ID: " + channelId);
            return null;
        }

        JsonNode channel = response.get("items").get(0);
        JsonNode snippet = channel.get("snippet");
        JsonNode statistics = channel.get("statistics");

        List<VideoSearchResult> recentVideos = getChannelRecentVideos(channelId);

        return new ChannelProfileResult(
                channelId,
                snippet.get("title").asText(),
                snippet.get("description").asText(),
                statistics.get("subscriberCount").asLong(),
                snippet.get("thumbnails").get("default").get("url").asText(),
                snippet.get("country") == null ? "-" : snippet.get("country").asText(),
                recentVideos
        );
    }

    private static List<VideoSearchResult> getChannelRecentVideos(String channelId) throws IOException, InterruptedException {
        String apiUrl = String.format("%s%s&channelId=%s&type=video&order=date&maxResults=%d&key=%s",
                getApiBaseUrl(), getSearchEndpoint(), channelId, 10, getApiKey());
        JsonNode response = HttpUtils.sendRequest(apiUrl);

        List<VideoSearchResult> results = parseVideoResults(response);
        List<String> videoIds = getVideoIds(results);
        fetchVideoTags(videoIds, results);

        return results;
    }

    private static List<VideoSearchResult> parseVideoResults(JsonNode response) {
        List<VideoSearchResult> results = new ArrayList<>();
        for (JsonNode item : response.get("items")) {
            JsonNode snippet = item.get("snippet");
            String videoId = item.get("id").get("videoId").asText();
            results.add(new VideoSearchResult(
                    videoId,
                    snippet.get("title").asText(),
                    snippet.get("description").asText(),
                    snippet.get("thumbnails").get("default").get("url").asText(),
                    snippet.get("channelId").asText(),
                    snippet.get("channelTitle").asText(),
                    snippet.get("publishedAt").asText(),
                    null
            ));
        }
        return results;
    }

    private static List<String> getVideoIds(List<VideoSearchResult> results) {
        return results.stream().map(VideoSearchResult::getVideoId).collect(Collectors.toList());
    }

    private static void fetchVideoTags(List<String> videoIds, List<VideoSearchResult> results) throws IOException, InterruptedException {
        String apiUrl = String.format("%s%s&id=%s&key=%s",
                getApiBaseUrl(), getVideosEndpoint(), String.join(",", videoIds), getApiKey());

        JsonNode videoDetailsResponse = HttpUtils.sendRequest(apiUrl);
        System.out.println(videoDetailsResponse);

        for (JsonNode video : videoDetailsResponse.get("items")) {
            String videoId = video.get("id").asText();
            JsonNode tagsNode = video.get("snippet").get("tags");
            List<String> tags = new ArrayList<>();
            if (tagsNode != null) {
                for (JsonNode tag : tagsNode) {
                    tags.add(tag.asText());
                }
            }
            for (VideoSearchResult result : results) {
                if (result.getVideoId().equals(videoId)) {
                    result.setTags(tags);
                    break;
                }
            }
        }
    }
}
