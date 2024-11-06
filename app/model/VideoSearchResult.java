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
    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String getChannelTitle() {
        return channelTitle;
    }

    public void setChannelTitle(String channelTitle) {
        this.channelTitle = channelTitle;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(String publishDate) {
        this.publishDate = publishDate;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public void setVideoUrl(String videoUrl){
        this.videoUrl=videoUrl;
    }

    public String getVideoUrl()
    {
        return videoUrl;
    }
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

