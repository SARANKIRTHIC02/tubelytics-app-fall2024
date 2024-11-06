package model;

import java.util.List;

public class ChannelProfileResult {
    private String channelId;
    private String channelTitle;
    private String channelDescription;
    private Long subscriberCount;
    private String channelThumbnailUrl;
    private List<VideoSearchResult> recentVideos;

    public ChannelProfileResult(String channelId, String channelTitle, String channelDescription, Long subscriberCount,
                                String channelThumbnailUrl, List<VideoSearchResult> recentVideos) {
        this.channelId = channelId;
        this.channelTitle = channelTitle;
        this.channelDescription = channelDescription;
        this.subscriberCount = subscriberCount;
        this.channelThumbnailUrl = channelThumbnailUrl;
        this.recentVideos = recentVideos;
    }

    // Getters and Setters
    public String getChannelId() {
        return channelId;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public String getChannelDescription() {
        return channelDescription;
    }

    public Long getSubscriberCount() {
        return subscriberCount;
    }

    public String getChannelThumbnailUrl() {
        return channelThumbnailUrl;
    }

    public List<VideoSearchResult> getRecentVideos() {
        return recentVideos;
    }

    @Override
    public String toString() {
        return "ChannelProfileResult{" +
                "channelId='" + channelId + '\'' +
                ", channelTitle='" + channelTitle + '\'' +
                ", channelDescription='" + channelDescription + '\'' +
                ", subscriberCount=" + subscriberCount +
                ", channelThumbnailUrl='" + channelThumbnailUrl + '\'' +
                ", recentVideos=" + recentVideos +
                '}';
    }
}
