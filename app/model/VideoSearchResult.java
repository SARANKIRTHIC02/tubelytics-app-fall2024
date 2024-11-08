package model;
import java.util.List;

public class VideoSearchResult {
    private String videoId;
    private String title;
    private String description;
    private String thumbnailUrl;
    private String channelId;
    private String channelTitle;
    private String publishDate;
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
     * @param publishDate   The publication date of the video.
     * @param tags          A list of tags associated with the video.
     */
    public VideoSearchResult(String videoId, String title, String description, String thumbnailUrl,
                             String channelId, String channelTitle, String publishDate, List<String> tags) {
        this.videoId = videoId;
        this.title = title;
        this.description = description;
        this.thumbnailUrl = thumbnailUrl;
        this.channelId = channelId;
        this.channelTitle = channelTitle;
        this.publishDate = publishDate;
        this.tags = tags;
        this.videoUrl="https://www.youtube.com/watch?v="+this.videoId;
    }

    // Getters and Setters

    /**
     * Returns video ID.
     * @return video ID.
     */
    public String getVideoId() {
        return videoId;
    }


    /**
     * Sets video ID.
     * @param videoId video ID.
     */
    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    /**
     * Returns the title of the video.
     * @return Title of the video.
     */
    public String getTitle() {
        return title;
    }

    /**
     * Sets the title of the video.
     * @param title title of the video.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Returns the description of the video.
     * @return description of the video.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description of the video to the variable.
     * @param description sets the description of the video.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the url of the thumbnail.
     * @return url of the thumbnail.
     */
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    /**
     * Sets the url of the thumbnail.
     * @param thumbnailUrl sets the url of the thumbnail.
     */
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    /**
     * Returns the channel ID.
     * @return channel ID.
     */
    public String getChannelId() {
        return channelId;
    }

    /**
     * Sets the channel ID.
     * @param channelId Channel ID.
     */
    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    /**
     * Returns the channel title.
     * @return channel title.
     */
    public String getChannelTitle() {
        return channelTitle;
    }

    /**
     * Sets the channel title.
     * @param channelTitle sets the channel title.
     */
    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    /**
     * Returns the published date of the video.
     * @return published date of the video.
     */
    public String getPublishDate() {
        return publishDate;
    }

    /**
     * Sets the published date of the video.
     * @param publishDate Sets the published date of the video.
     */
    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
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
     * Sets the url link of the video.
     * @param videoUrl sets the url link of the video.
     */
    public void setVideoUrl(String videoUrl){
        this.videoUrl=videoUrl;
    }

    /**
     * Returns the url link of the video.
     * @return url link of the video.
     */
    public String getVideoUrl()
    {
        return videoUrl;
    }

    /**
     * Returns a string representation of the VideoSearchResult.
     * @return A string with video details, Including ID, Title, Description, Thumbnail Url,
     * channel ID, Channel title, Published date and tags.
     */
    @Override
    public String toString() {
        return "VideoSearchResult{" +
                "videoId='" + videoId + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", thumbnailUrl='" + thumbnailUrl + '\'' +
                ", channelId='" + channelId + '\'' +
                ", channelTitle='" + channelTitle + '\'' +
                ", publishDate='" + publishDate + '\'' +
                ", tags=" + tags +
                '}';
    }
}

