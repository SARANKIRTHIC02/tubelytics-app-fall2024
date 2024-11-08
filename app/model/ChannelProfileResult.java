package model;

import java.util.List;

/**
 * The class represents a YouTube channel profile, including details like channel ID, title, description, subscriber
 * count, thumbnail URL, country, and a list of recent videos.
 */
public class ChannelProfileResult {
    private String channelId;
    private String channelTitle;
    private String channelDescription;
    private Long subscriberCount;
    private String channelThumbnailUrl;
    private String country;
    private List<VideoSearchResult> recentVideos;

    /**
     * Constructor.
     *
     * @param channelId the unique identifier of the YouTube channel.
     * @param channelTitle the title or name of the channel.
     * @param channelDescription a description of the channel content.
     * @param subscriberCount the number of subscribers to the channel.
     * @param channelThumbnailUrl the URL of the channel's thumbnail image.
     * @param country the country where the channel is based, if available.
     * @param recentVideos a list of recent videos published by the channel.
     */
    public ChannelProfileResult(String channelId, String channelTitle, String channelDescription, Long subscriberCount,
                                String channelThumbnailUrl, String country, List<VideoSearchResult> recentVideos) {
        this.channelId = channelId;
        this.channelTitle = channelTitle;
        this.channelDescription = channelDescription;
        this.subscriberCount = subscriberCount;
        this.channelThumbnailUrl = channelThumbnailUrl;
        this.country = country;
        this.recentVideos = recentVideos;
    }

    // Getters and Setters
    /**
     * Gets the unique identifier of the YouTube channel.
     *
     * @return the channel ID.
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * Gets the title or name of the YouTube channel.
     *
     * @return the channel title.
     */
    public String getChannelTitle() {
        return channelTitle;
    }

    /**
     * Gets the description of the YouTube channel.
     *
     * @return the channel description.
     */
    public String getChannelDescription() {
        return channelDescription;
    }

    /**
     * Gets the subscriber count of the YouTube channel.
     *
     * @return the subscriber count.
     */
    public Long getSubscriberCount() {
        return subscriberCount;
    }

    /**
     * Gets the URL of the YouTube channel's thumbnail image.
     *
     * @return the channel thumbnail URL.
     */
    public String getChannelThumbnailUrl() {
        return channelThumbnailUrl;
    }

    /**
     * Gets the list of recent videos published by the channel.
     *
     * @return a list of {@code VideoSearchResult} representing recent videos.
     */
    public List<VideoSearchResult> getRecentVideos() {
        return recentVideos;
    }

    /**
     * Gets the country where the YouTube channel is based, if available.
     *
     * @return the country of the channel, or "-" if not specified.
     */
    public String getCountry(){
        return country;
    }

    /**
     * Returns a string representation of the Channel Profile object,
     * containing the channel details.
     *
     * @return a string representation of the channel profile.
     */
    @Override
    public String toString() {
        return "ChannelProfileResult{" +
                "channelId='" + channelId + '\'' +
                ", channelTitle='" + channelTitle + '\'' +
                ", channelDescription='" + channelDescription + '\'' +
                ", subscriberCount=" + subscriberCount +
                ", channelThumbnailUrl='" + channelThumbnailUrl + '\'' +
                ", channelThumbnailUrl='" + channelThumbnailUrl + '\''+
                ", recentVideos=" + recentVideos +
                '}';
    }
}
