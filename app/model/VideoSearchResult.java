package model;
import java.util.List;

public class VideoSearchResult {
    private String videoId;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String channelId;
    private String channelTitle;
    private List<String> tags;
    private String videoUrl;

    /**
     * Constructor.
     *
     * @param videoId       The unique ID of the video.
     * @param title         The title of the video.
     * @param description   A brief description of the video.
     * @param thumbnailUrl  The URL of the video's thumbnail.
     * @param channelId     The unique ID of the channel that published the video.
     * @param channelTitle  The title of the channel that published the video.
     * @param tags          A list of tags associated with the video.
     */
    public VideoSearchResult(String videoId, String title, String description, String thumbnailUrl,
                             String channelId, String channelTitle, List<String> tags) {
        this.videoId = videoId;
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.channelId = channelId;
        this.channelTitle = channelTitle;
        this.tags = tags;
        this.videoUrl="https://www.youtube.com/watch?v="+this.videoId;
    }


    /**
     * Returns video ID.
     * @return video ID.
     */
    public String getVideoId() {
        return videoId;
    }


    /**
     * Returns the title of the video.
     * @return Title of the video.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Returns the description of the video.
     *
     * @return description of the video.
     */
    public String getDescription() {
        return description;
    }


    /**
     * Returns the url of the thumbnail.
     * @return url of the thumbnail.
     */
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }


    /**
     * Returns the channel ID.
     * @return channel ID.
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * Returns the channel title.
     * @return channel title.
     */
    public String getChannelTitle() {
        return channelTitle;
    }


    /**
     * Returns a list of tags in a video.
     * @return List of tags.
     */
    public List<String> getTags() {
        return tags;
    }

    /**
     * Sets the list of tags of a video.
     * @param tags list of tags.
     */
    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    /**
     * Returns the url link of the video.
     * @return url link of the video.
     */
    public String getVideoUrl()
    {
        return videoUrl;
    }
}

