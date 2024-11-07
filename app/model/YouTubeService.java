package model;

import Util.HttpUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class YouTubeService {

    private static final Config config = ConfigFactory.load();
    private static final String BASE_URL = config.getString("youtube.base-url");
    private static final String API_KEY = config.getString("youtube.api-key");
    private static final String SEARCH_ENDPOINT = config.getString("youtube.search-endpoint");
    private static final String VIDEOS_ENDPOINT = config.getString("youtube.videos-endpoint");
    private static final String CHANNEL_ENDPOINT = config.getString("youtube.channel-endpoint");

    public static List<VideoSearchResult> searchVideosBasedOnQuery(String query) throws IOException, InterruptedException {
        String apiUrl = String.format("%s%s&q=%s&type=video&maxResults=%d&order=date&key=%s",
                BASE_URL, SEARCH_ENDPOINT, query, 50, API_KEY);
        JsonNode response = HttpUtils.sendRequest(apiUrl);

        List<VideoSearchResult> results = parseVideoResults(response);

        // Fetch tags for the videos
        List<String> videoIds = getVideoIds(results);
        fetchVideoTags(videoIds, results);

        System.out.println("Line 33:   "+results);

        return results;
    }

    public static ChannelProfileResult getChannelProfile(String channelId) throws IOException, InterruptedException {
        String apiUrl = String.format("%s%s&id=%s&key=%s", BASE_URL, CHANNEL_ENDPOINT, channelId, API_KEY);
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
                BASE_URL, SEARCH_ENDPOINT, channelId, 10, API_KEY);
        JsonNode response = HttpUtils.sendRequest(apiUrl);

        List<VideoSearchResult> results = parseVideoResults(response);

        // Fetch tags for the recent videos
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
                    null  // Tags will be added later
            ));
        }
        return results;
    }

    private static List<String> getVideoIds(List<VideoSearchResult> results) {
        return results.stream().map(VideoSearchResult::getVideoId).collect(Collectors.toList());
    }

    private static void fetchVideoTags(List<String> videoIds, List<VideoSearchResult> results) throws IOException, InterruptedException {
        // Construct the API URL to fetch video details including tags
        String apiUrl = String.format("%s%s&id=%s&key=%s",
                BASE_URL, VIDEOS_ENDPOINT, String.join(",", videoIds), API_KEY);

        JsonNode videoDetailsResponse = HttpUtils.sendRequest(apiUrl);
        System.out.println(videoDetailsResponse);

        // Map tags to the corresponding VideoSearchResult object
        for (JsonNode video : videoDetailsResponse.get("items")) {
            String videoId = video.get("id").asText();
            JsonNode tagsNode = video.get("snippet").get("tags");

            // Convert JSON array of tags to a list
            List<String> tags = new ArrayList<>();
            if (tagsNode != null) {
                for (JsonNode tag : tagsNode) {
                    tags.add(tag.asText());
                }
            }

            // Add tags to the appropriate VideoSearchResult
            for (VideoSearchResult result : results) {
                if (result.getVideoId().equals(videoId)) {
                    result.setTags(tags);
                    break;
                }
            }
        }
    }
}
