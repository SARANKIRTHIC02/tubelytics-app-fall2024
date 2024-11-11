
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


    @Test
    public void testFetchResultsWithQuery(){
        List<VideoSearchResult> mockResults = new ArrayList<>();
        mockResults.add(new VideoSearchResult("videoId", "title", "description", "thumbnailUrl", "channelId", "channelTitle", null));
        youtubeServiceMock.when(() -> YouTubeService.searchVideosBasedOnQuery("Lion")).thenReturn(mockResults);
        List<VideoSearchResult> results=TubelyticService.fetchResults("Lion");
        System.out.println(results);
        Assert.assertTrue(results.size()>0);
        Assert.assertEquals("videoId",results.get(0).getVideoId());
    }
    @Test
    public void testFetchResultsWithOutQuery(){
        youtubeServiceMock.when(() -> YouTubeService.searchVideosBasedOnQuery("test")).thenReturn(new ArrayList<VideoSearchResult>());
        List<VideoSearchResult> results=TubelyticService.fetchResults("");
        Assert.assertTrue(results.size()==0);
    }
    @Test
    public void testFetchChannelDetailsWithChannelID(){
        String channelID="UCi7Zk9baY1tvdlgxIML8MXg";
        ChannelProfileResult mockChannelProfile=new ChannelProfileResult(channelID, "AVC News","",200000L,"","",Collections.emptyList());
        youtubeServiceMock.when(() -> YouTubeService.getChannelProfile(channelID)).thenReturn(mockChannelProfile);
        ChannelProfileResult channelProfileResult=TubelyticService.fetchChannelDetails(channelID);
        Assert.assertEquals("UCi7Zk9baY1tvdlgxIML8MXg",channelProfileResult.getChannelId());
        Assert.assertEquals("AVC News",channelProfileResult.getChannelTitle());
    }
    @Test
    public void testFetchChannelDetailsWithOutChannelID(){
        youtubeServiceMock.when(() -> YouTubeService.getChannelProfile("")).thenReturn(null);
        ChannelProfileResult profileResult=TubelyticService.fetchChannelDetails("");
        Assert.assertNull(profileResult);
    }
   @Test
    public void testFetchResultsThrowsRuntimeExceptionOnIOException() {
        youtubeServiceMock.when(() -> YouTubeService.searchVideosBasedOnQuery("test"))
                .thenThrow(new IOException());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            TubelyticService.fetchResults("test");
        });
        Assert.assertTrue(exception.getCause() instanceof IOException);
    }

    @Test
    public void testFetchResultsThrowsRuntimeExceptionOnInterruptedException() {
        youtubeServiceMock.when(() -> YouTubeService.searchVideosBasedOnQuery("test"))
                .thenThrow(new InterruptedException());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            TubelyticService.fetchResults("test");
        });
        Assert.assertTrue(exception.getCause() instanceof InterruptedException);
    }

    @Test
    public void testFetchChannelDetailsThrowsRuntimeExceptionOnIOException() {
        youtubeServiceMock.when(() -> YouTubeService.getChannelProfile("channel123"))
                .thenThrow(new IOException());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            TubelyticService.fetchChannelDetails("channel123");
        });
        Assert.assertTrue(exception.getCause() instanceof IOException);
    }

    @Test
    public void testFetchChannelDetailsThrowsRuntimeExceptionOnInterruptedException() {
        youtubeServiceMock.when(() -> YouTubeService.getChannelProfile("channel123"))
                .thenThrow(new InterruptedException());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            TubelyticService.fetchChannelDetails("channel123");
        });
        Assert.assertTrue(exception.getCause() instanceof InterruptedException);
    }

    @Test
    public void testEmptyResultsList() {
        List<VideoSearchResult> results = Collections.emptyList();
        Map<String, Long> wordFrequency = TubelyticService.wordStatistics(results);

        Assert.assertTrue("Expected empty word frequency map", wordFrequency.isEmpty());
    }

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

