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
    @Test
    public void testFetchResultsWithQuery(){
        List<VideoSearchResult> mockResults = new ArrayList<>();
        mockResults.add(new VideoSearchResult("videoId", "title", "description", "thumbnailUrl", "channelId", "channelTitle", "publishedAt", null));
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


}
