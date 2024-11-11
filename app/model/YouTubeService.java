package model;

import Util.HttpUtils;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author saran
 * @author sushanth
 * @author durai
 * The YouTubeService class provides methods to interact with the YouTube Data API.
 * It allows for searching videos based on a query, fetching channel profile details, and
 * retrieving word statistics from video descriptions.
 */
public class YouTubeService {

    private YouTubeService(){}
    private static final String apiKey;
    private static final String baseUrl;
    private static final String searchEndpoint;
    private static final String channelEndpoint;
    private static final String videosEndpoint;

    static {
            apiKey = "AIzaSyCGi_3tP5IQdpXajRIe7ycgbkaLLwYWWYs";
            baseUrl = "https://www.googleapis.com/youtube/v3";
            searchEndpoint = "/search?part=snippet";
            channelEndpoint = "/channels?part=snippet,statistics";
            videosEndpoint = "/videos?part=snippet,statistics";
    }

    /**
     * @author durai
     *  @author saran
     *  @author sushanth
     * Method return API key.
     * @return API key.
     */
    public static String getApiKey() {
        return apiKey;
    }

    /**
     * @author durai
     * Method to return the base URL for the YouTube Data API.
     * @return Base URL for the API.
     */
    public static String getApiBaseUrl() {
        return baseUrl;
    }

    /**
     * @author durai
     * Method to return the endpoint for the search API.
     * @return Endpoint for search API.
     */
    public static String getSearchEndpoint() {
        return searchEndpoint;
    }

    /**
     * @author durai
     * Method to return the endpoint for channel details API.
     * @return Endpoint for Channel details API.
     */
    public static String getChannelEndpoint() {
        return channelEndpoint;
    }

    /**
     * @author durai
     * Method to return the endpoint for video details API.
     * @return Endpoint for video details API.
     */
    public static String getVideosEndpoint() {
        return videosEndpoint;
    }

    /**
     * @author durai
     * Searches for videos based on a provided query.
     * Fetches the initial results and then retrieves additional details such as tags for each video.
     * @param query The search term for the video query.
     * @return A list of VideoSearchResult objects containing the search results.
     * @throws IOException If an I/O error occurs during the API request.
     * @throws InterruptedException If the API request is interrupted.
     */
    public static List<VideoSearchResult> searchVideosBasedOnQuery(String query) throws IOException, InterruptedException {
        String apiUrl = String.format("%s%s&q=%s&type=video&maxResults=%d&order=date&key=%s",
                getApiBaseUrl(), getSearchEndpoint(), query, 50, getApiKey());
        JsonNode response = HttpUtils.sendRequest(apiUrl);
        List<VideoSearchResult> results =new ArrayList<>();
        if(response!=null) {
            results = parseVideoResults(response);

            List<String> videoIds = getVideoIds(results);
            fetchVideoTags(videoIds, results);

        }

        return results;
    }

    /**
     * @author durai
     * @author sushanth
     * Retrieves the profile details of a YouTube channel by its ID.
     * @param channelId The unique ID of the YouTube channel.
     * @return A ChannelProfileResult object containing channel details, or null if the channel is not found.
     * @throws IOException If an I/O error occurs during the API request.
     * @throws InterruptedException  If the API request is interrupted.
     */
    public static ChannelProfileResult getChannelProfile(String channelId) throws IOException, InterruptedException {
        String apiUrl = String.format("%s%s&id=%s&key=%s", getApiBaseUrl(), getChannelEndpoint(), channelId, getApiKey());
        JsonNode response = HttpUtils.sendRequest(apiUrl);
        if (!response.has("items") || response.get("items").isEmpty()) {
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

    /**
     * @author durai
     * @author saran
     * Retrieves recent videos from a specified YouTube channel.
     * @param channelId The unique ID of the YouTube channel.
     * @return A list of VideoSearchResult objects containing the recent videos from the channel.
     * @throws IOException If an I/O error occurs during the API request.
     * @throws InterruptedException  If the API request is interrupted.
     */
    private static List<VideoSearchResult> getChannelRecentVideos(String channelId) throws IOException, InterruptedException {
        String apiUrl = String.format("%s%s&channelId=%s&type=video&order=date&maxResults=%d&key=%s",
                getApiBaseUrl(), getSearchEndpoint(), channelId, 10, getApiKey());
        JsonNode response = HttpUtils.sendRequest(apiUrl);

        List<VideoSearchResult> results = parseVideoResults(response);
        List<String> videoIds = getVideoIds(results);
        fetchVideoTags(videoIds, results);

        return results;
    }

    /**
     * @author durai
     * Parses video results from a JSON response and converts them into VideoSearchResult objects.
     *
     * @param response The JSON response from the YouTube Data API.
     * @return A list of VideoSearchResult objects parsed from the JSON response.
     */
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
                    null
            ));
        }
        return results;
    }

    /**
     * @author sushanth
     * Extracts video IDs from a list of VideoSearchResult objects.
     * @param results A list of VideoSearchResult objects.
     * @return A list of video IDs.
     */
    private static List<String> getVideoIds(List<VideoSearchResult> results) {
        return results.stream().map(VideoSearchResult::getVideoId).collect(Collectors.toList());
    }

    /**
     * @author sushanth
     * Fetches tags for a list of videos based on their IDs.
     * Updates each VideoSearchResult with its corresponding tags.
     * @param videoIds A list of video IDs to fetch tags for.
     * @param results  A list of VideoSearchResult objects to update with tags.
     * @throws IOException If an I/O error occurs during the API request.
     * @throws InterruptedException  If the API request is interrupted.
     */
    private static void fetchVideoTags(List<String> videoIds, List<VideoSearchResult> results) throws IOException, InterruptedException {
        String apiUrl = String.format("%s%s&id=%s&key=%s",
                getApiBaseUrl(), getVideosEndpoint(), String.join(",", videoIds), getApiKey());

        JsonNode videoDetailsResponse = HttpUtils.sendRequest(apiUrl);

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
