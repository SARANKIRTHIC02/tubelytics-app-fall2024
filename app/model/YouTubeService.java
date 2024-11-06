package model;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

public class YouTubeService {

    private static final String APPLICATION_NAME = "tubelytics";
    private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();
    private static final String API_KEY;

    static{
        Config config = ConfigFactory.load();
        API_KEY = config.getString("youtube.api.key");
    }

    private final YouTube youtube;

    public YouTubeService() throws GeneralSecurityException, IOException {
        this.youtube = new YouTube.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JSON_FACTORY,
                null
        ).setApplicationName(APPLICATION_NAME).build();
    }
    public List<VideoSearchResult> searchVideosInfo(String query) throws IOException {
        YouTube.Search.List searchRequest = youtube.search().list("snippet");
        searchRequest.setQ(query);
        searchRequest.setType("video");
        searchRequest.setMaxResults(5L);
        searchRequest.setOrder("date");
        searchRequest.setKey(API_KEY);

        return getVideoSearchResults(searchRequest);
    }

    private List<VideoSearchResult> getVideoSearchResults(YouTube.Search.List searchRequest) throws IOException {
        SearchListResponse searchResponse = searchRequest.execute();

        List<String> videoIds = new ArrayList<>();
        List<VideoSearchResult> results = new ArrayList<>();

        for (SearchResult item : searchResponse.getItems()) {
            String videoId = item.getId().getVideoId();
            videoIds.add(videoId);
            results.add(new VideoSearchResult(
                    videoId,
                    item.getSnippet().getTitle(),
                    item.getSnippet().getDescription(),
                    item.getSnippet().getThumbnails().getDefault().getUrl(),
                    item.getSnippet().getChannelId(),
                    item.getSnippet().getChannelTitle(),
                    item.getSnippet().getPublishedAt().toString(),
                    null// TODO Placeholder for tags
            ));
        }

        if (videoIds.isEmpty()) {
            return new ArrayList<>();
        }
        return results;
    }

}

