package model;

import org.junit.Test;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the VideoSearchResult class.
 * This class verifies the correctness of the VideoSearchResult model,
 * focusing on constructors and their parameter handling.
 *
 * @version 1.0
 * @author
 *   - Sushanth
 */
public class VideoSearchResultTest {

    /**
     * Tests the VideoSearchResult constructor with all parameters.
     * Verifies that all fields are initialized correctly and the video URL is constructed as expected.
     *
     * @author Sushanth
     */
    @Test
    public void testConstructorWithAllParameters() {
        // Arrange
        List<String> tags = Arrays.asList("tag1", "tag2");

        // Act
        VideoSearchResult result = new VideoSearchResult(
                "video123",
                "Sample Video",
                "This is a sample video description.",
                "http://example.com/thumbnail.jpg",
                "channel123",
                "Sample Channel",
                tags
        );

        // Assert
        assertEquals("video123", result.getVideoId());
        assertEquals("Sample Video", result.getTitle());
        assertEquals("This is a sample video description.", result.getDescription());
        assertEquals("http://example.com/thumbnail.jpg", result.getThumbnailUrl());
        assertEquals("channel123", result.getChannelId());
        assertEquals("Sample Channel", result.getChannelTitle());
        assertEquals(tags, result.getTags());
        assertEquals("https://www.youtube.com/watch?v=video123", result.getVideoUrl());
    }

    /**
     * Tests the VideoSearchResult constructor with partial parameters.
     * Verifies that the fields not included in the constructor remain null.
     *
     * @author Sushanth
     */
    @Test
    public void testConstructorWithPartialParameters() {
        // Act
        VideoSearchResult result = new VideoSearchResult(
                "video456",
                "Partial Video",
                "This is another video description."
        );

        // Assert
        assertEquals("video456", result.getVideoId());
        assertEquals("Partial Video", result.getTitle());
        assertEquals("This is another video description.", result.getDescription());
        assertNull(result.getThumbnailUrl());
        assertNull(result.getChannelId());
        assertNull(result.getChannelTitle());
        assertNull(result.getTags());
        assertNull(result.getVideoUrl());
    }
}
