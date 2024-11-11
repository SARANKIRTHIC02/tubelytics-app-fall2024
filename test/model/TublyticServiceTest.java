
package model;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class TublyticServiceTest {
    private static  MockedStatic<YouTubeService> youtubeServiceMock= Mockito.mockStatic(YouTubeService.class);
    private static  MockedStatic<VideoSearchResult> videoServiceMock= Mockito.mockStatic(VideoSearchResult.class);

    /**
     * @author durai
     * @author saran
     * Tests fetching video results based on a query string, using a mocked YouTubeService.
     * Verifies that the fetched results are not empty and that the video ID matches the expected value.
     */
    @Test
    public void testFetchResultsWithQuery(){
        List<VideoSearchResult> mockResults = new ArrayList<>();
        mockResults.add(new VideoSearchResult("videoId", "title", "description", "thumbnailUrl", "channelId", "channelTitle", null));
        youtubeServiceMock.when(() -> YouTubeService.searchVideosBasedOnQuery("Lion")).thenReturn(mockResults);
        List<VideoSearchResult> results=TubelyticService.fetchResults("Lion");
        Assert.assertTrue(results.size()>0);
        Assert.assertEquals("videoId",results.get(0).getVideoId());
    }

    /**
     * @author durai
     * @author sushanth
     * Tests fetching video results with an empty query string.
     */
    @Test
    public void testFetchResultsWithOutQuery(){
        youtubeServiceMock.when(() -> YouTubeService.searchVideosBasedOnQuery("test")).thenReturn(new ArrayList<VideoSearchResult>());
        List<VideoSearchResult> results=TubelyticService.fetchResults("");
        Assert.assertTrue(results.size()==0);
    }

    /**
     * @author durai
     * @author saran
     * Tests fetching channel details based on a channel ID, using a mocked YouTubeService.
     * Verifies that the channel ID and channel title match expected values.
     */
    @Test
    public void testFetchChannelDetailsWithChannelID(){
        String channelID="UCi7Zk9baY1tvdlgxIML8MXg";
        ChannelProfileResult mockChannelProfile=new ChannelProfileResult(channelID, "AVC News","",200000L,"","",Collections.emptyList());
        youtubeServiceMock.when(() -> YouTubeService.getChannelProfile(channelID)).thenReturn(mockChannelProfile);
        ChannelProfileResult channelProfileResult=TubelyticService.fetchChannelDetails(channelID);
        Assert.assertEquals("UCi7Zk9baY1tvdlgxIML8MXg",channelProfileResult.getChannelId());
        Assert.assertEquals("AVC News",channelProfileResult.getChannelTitle());
    }

    /**
     *
     * Tests fetching channel details with an empty channel ID
     */
    @Test
    public void testFetchChannelDetailsWithOutChannelID(){
        youtubeServiceMock.when(() -> YouTubeService.getChannelProfile("")).thenReturn(null);
        ChannelProfileResult profileResult=TubelyticService.fetchChannelDetails("");
        Assert.assertNull(profileResult);
    }

    /**
     * @author durai
     * @author saran
     * Tests that fetching video results throws a RuntimeException when an IOException occurs.
     */
   @Test
    public void testFetchResultsThrowsRuntimeExceptionOnIOException() {
        youtubeServiceMock.when(() -> YouTubeService.searchVideosBasedOnQuery("test"))
                .thenThrow(new IOException());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            TubelyticService.fetchResults("test");
        });
        Assert.assertTrue(exception.getCause() instanceof IOException);
    }

    /**
     * @author druai
     * @author sushanth
     * Tests that fetching video results throws a RuntimeException when an InterruptedException occurs.
     */
    @Test
    public void testFetchResultsThrowsRuntimeExceptionOnInterruptedException() {
        youtubeServiceMock.when(() -> YouTubeService.searchVideosBasedOnQuery("test"))
                .thenThrow(new InterruptedException());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            TubelyticService.fetchResults("test");
        });
        Assert.assertTrue(exception.getCause() instanceof InterruptedException);
    }

    /**
     * @author durai
     * @author saran
     * Tests that fetching channel details throws a RuntimeException when an IOException occurs.
     */
    @Test
    public void testFetchChannelDetailsThrowsRuntimeExceptionOnIOException() {
        youtubeServiceMock.when(() -> YouTubeService.getChannelProfile("channel123"))
                .thenThrow(new IOException());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            TubelyticService.fetchChannelDetails("channel123");
        });
        Assert.assertTrue(exception.getCause() instanceof IOException);
    }

    /**
     * @author durai
     * @author susanth
     * Tests that fetching channel details throws a RuntimeException when an InterruptedException occurs.
     */
    @Test
    public void testFetchChannelDetailsThrowsRuntimeExceptionOnInterruptedException() {
        youtubeServiceMock.when(() -> YouTubeService.getChannelProfile("channel123"))
                .thenThrow(new InterruptedException());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            TubelyticService.fetchChannelDetails("channel123");
        });
        Assert.assertTrue(exception.getCause() instanceof InterruptedException);
    }

    /**
     * @author saran
     * @author durai
     * Tests that the word frequency map is empty when given an empty list of video results.
     */
    @Test
    public void testEmptyResultsList() {
        List<VideoSearchResult> results = Collections.emptyList();
        Map<String, Long> wordFrequency = TubelyticService.wordStatistics(results);

        Assert.assertTrue("Expected empty word frequency map", wordFrequency.isEmpty());
    }

    /**
     * @author saran
     * @author durai
     * Tests word statistics generation with mixed-case words, verifying case insensitivity.
     * Expected result: words appear in lowercase with correct frequencies.
     */
    @Test
    public void testWordStatisticsWithWordsUpperAndLowerCase()
    {
        VideoSearchResult video1 = Mockito.mock(VideoSearchResult.class);
        VideoSearchResult video2 = Mockito.mock(VideoSearchResult.class);

        videoServiceMock.when(()-> video1.getDescription()).thenReturn("apple apple orange orange banana");
        videoServiceMock.when(()-> video2.getDescription()).thenReturn("apple Orange Cherry cherry");

        List<VideoSearchResult> results = Arrays.asList(video1, video2);
        Map<String, Long> wordFrequency = TubelyticService.wordStatistics(results);

        Map<String, Long> expected = new LinkedHashMap<>();
        expected.put("orange", 3L);
        expected.put("apple", 3L);
        expected.put("cherry", 2L);
        expected.put("banana", 1L);

        Assert.assertEquals(expected, wordFrequency);

    }

    /**
     * @author saran
     * @author durai
     * Tests word statistics generation with words, numbers, and special characters.
     * Verifies correct word frequencies, ignoring non-alphabetic symbols.
     */
    @Test
    public void testWithWordsNumbersAndSpecialCharecters()
    {
        VideoSearchResult video1 = Mockito.mock(VideoSearchResult.class);
        VideoSearchResult video2 = Mockito.mock(VideoSearchResult.class);

        videoServiceMock.when(()-> video1.getDescription()).thenReturn("Java programming is fun. 1 A I");
        videoServiceMock.when(()-> video2.getDescription()).thenReturn("Fun with programming! Java? 2023 special chars: @#$%^&*()");

        List<VideoSearchResult> results = Arrays.asList(video1, video2);
        Map<String, Long> testWordFrequency = TubelyticService.wordStatistics(results);
        Map<String, Long> expected = new LinkedHashMap<>();
        expected.put("java", 2L);
        expected.put("programming", 2L);
        expected.put("fun", 2L);
        expected.put("with", 1L);
        expected.put("is", 1L);
        expected.put("special", 1L);
        expected.put("chars", 1L);
        expected.put("2023", 1L);
        expected.put("a", 1L);
        expected.put("i", 1L);

        Assert.assertEquals(expected, testWordFrequency);
    }





}

