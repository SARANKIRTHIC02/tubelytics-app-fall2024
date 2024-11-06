package model;
import Util.HttpUtils;
import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class YouTubeService {
    private static final Config config = ConfigFactory.load();
    private static final String BASE_URL = config.getString("youtube.base-url");
    private static final String API_KEY = config.getString("youtube.api-key");
    private static final String SEARCH_ENDPOINT=config.getString("youtube.search-endpoint");
    private static final String CHANNEL_ENDPOINT=config.getString("youtube.channel-endpoint");

    public static List<VideoSearchResult> searchVideosBasedOnQuery(String query) throws IOException, InterruptedException {
        String apiUrl = String.format("%s%s&q=%s&type=video&maxResults=%d&order=date&key=%s",
                BASE_URL,SEARCH_ENDPOINT, query, 50, API_KEY);
        JsonNode response = HttpUtils.sendRequest(apiUrl);

        return parseVideoResults(response);
    }

    public static ChannelProfileResult getChannelProfile(String channelId) throws IOException, InterruptedException {
        String apiUrl = String.format("%s%s&id=%s&key=%s", BASE_URL,CHANNEL_ENDPOINT, channelId, API_KEY);
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
                snippet.get("country")==null?"-":snippet.get("country").asText(),
                recentVideos
        );
    }

    private static List<VideoSearchResult> getChannelRecentVideos(String channelId) throws IOException, InterruptedException {
        String apiUrl = String.format("%s%s&channelId=%s&type=video&order=date&maxResults=%d&key=%s",
                BASE_URL, SEARCH_ENDPOINT, channelId, 10, API_KEY);
        JsonNode response = HttpUtils.sendRequest(apiUrl);

        return parseVideoResults(response);
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
                    null  // TODO tags
            ));
        }
        return results;
    }
}
