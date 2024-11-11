package model;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class ChannelProfileResultTest {

    /**
     * @author durai
     * @author sushanth
     * Tests that the channel description returned by {@link ChannelProfileResult#getChannelDescription()}
     * matches the expected description.
     */
    @Test
    public void testGetChannelDescription() {
        ChannelProfileResult result = new ChannelProfileResult(
                "channelId", "Test Channel", "Description", 100L, "http://thumbnail.url", "US", Collections.emptyList()
        );

        Assert.assertEquals("Description", result.getChannelDescription());

    }

    /**
     * @author durai
     * @author sushanth
     * Tests that the subscriber count returned by {@link ChannelProfileResult#getSubscriberCount()}
     * matches the expected subscriber count.
     */
    @Test
    public void testSubscriberCount() {
        ChannelProfileResult result = new ChannelProfileResult(
                "channelId", "Test Channel", "Description", 100L, "http://thumbnail.url", "US", Collections.emptyList()
        );

        Assert.assertEquals(Optional.of(100l), Optional.of(result.getSubscriberCount()));

    }

    /**
     * @author durai
     * @author saran
     * Tests that the thumbnail URL returned by {@link ChannelProfileResult#getChannelThumbnailUrl()}
     * matches the expected thumbnail URL.
     */
    @Test
    public void testChannelThumbnailUrl() {
        ChannelProfileResult result = new ChannelProfileResult(
                "channelId", "Test Channel", "Description", 100L, "http://thumbnail.url", "US", Collections.emptyList()
        );

        Assert.assertEquals("http://thumbnail.url", result.getChannelThumbnailUrl());

    }

    /**
     * @author durai
     * @author saran
     * Tests that the country code returned by {@link ChannelProfileResult#getCountry()}
     * matches the expected country code.
     */
    @Test
    public void testGetCountry() {
        ChannelProfileResult result = new ChannelProfileResult(
                "channelId", "Test Channel", "Description", 100L, "http://thumbnail.url", "US", Collections.emptyList()
        );

        Assert.assertEquals("US", result.getCountry());

    }

    /**
     * @author durai
     * Tests that the recent videos list returned by {@link ChannelProfileResult#getRecentVideos()}
     * matches the expected list of video results. Verifies the size and order of videos.
     */
    @Test
    public void testGetRecentVideos() {
        VideoSearchResult video1 = new VideoSearchResult("videoId1", "Title 1", "Description 1", "http://thumbnail1.url", "channelId1", "channelTitle1", null);
        VideoSearchResult video2 = new VideoSearchResult("videoId2", "Title 2", "Description 2", "http://thumbnail2.url", "channelId2", "channelTitle2", null);

        List<VideoSearchResult> videos = Arrays.asList(video1, video2);

        ChannelProfileResult profile = new ChannelProfileResult(
                "channelId123", "Test Channel", "Description", 100L, "http://thumbnail.url", "US", videos
        );

        List<VideoSearchResult> recentVideos = profile.getRecentVideos();
        Assert.assertNotNull(recentVideos);
        Assert.assertEquals(2, recentVideos.size());
        Assert.assertEquals("videoId1", recentVideos.get(0).getVideoId());
        Assert.assertEquals("videoId2", recentVideos.get(1).getVideoId());
    }
}