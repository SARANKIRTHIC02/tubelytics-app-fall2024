package view;

import model.ChannelProfileResult;
import model.VideoSearchResult;
import org.junit.jupiter.api.Test;
import play.twirl.api.Content;
import views.html.channelprofile;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit tests for the channel profile view template, verifying content rendering for channels
 * with and without recent videos.
 */
public class channelProfileTemplateTest {


    /**
     * @author saran
     * @author durai
     * Tests the rendering of the channel profile view when recent videos are available.
     * Verifies that the HTML content includes channel information, video titles, descriptions, tags,
     * and other relevant details.
     */
    @Test
    public void testChannelProfileViewWithVideos() {
        List<VideoSearchResult> recentVideos = List.of(
                new VideoSearchResult(
                        "videoId1", "Video Title 1", "Video Description 1",
                        "http://example.com/video1-thumb.jpg", "channelId1",
                        "Test Channel", List.of("Tag1", "Tag2")
                ),
                new VideoSearchResult(
                        "videoId2", "Video Title 2", "Video Description 2",
                        "http://example.com/video2-thumb.jpg", "channelId2",
                        "Test Channel", List.of("Tag3", "Tag4")
                )
        );

        ChannelProfileResult channel = new ChannelProfileResult(
                "channelId123", "Test Channel", "This is a test channel description.",
                123456L, "http://example.com/channel-thumbnail.jpg",
                "US", recentVideos
        );

        Content html = channelprofile.render(channel);

        assertTrue(html.body().contains("Channel Name: Test Channel"));
        assertTrue(html.body().contains("This is a test channel description."));
        assertTrue(html.body().contains("Video Title 1"));
        assertTrue(html.body().contains("Video Title 2"));
        assertTrue(html.body().contains("Tag1"));
        assertTrue(html.body().contains("Tag4"));
        assertTrue(html.body().contains("<p><b>Country: </b>US </p>"));
        assertTrue(html.body().contains("<p><b>Subscribers:</b> 123456</p>"));
    }

    /**
     * @author sushanth
     * @author saran
     * Tests the rendering of the channel profile view when no recent videos are available.
     * Verifies that the HTML content includes the channel information but displays a message
     * indicating no recent videos are found.
     */
    @Test
    public void testChannelProfileViewNoVideos() {
        ChannelProfileResult channel = new ChannelProfileResult(
                "channelId123", "Test Channel", "This is a test channel description.",
                123456L, "http://example.com/channel-thumbnail.jpg",
                "US", List.of()
        );

        Content html = channelprofile.render(channel);

        assertTrue(html.body().contains("Channel Name: Test Channel"));
        assertTrue(html.body().contains("This is a test channel description."));
        assertTrue(html.body().contains("No recent videos found."));
    }


}