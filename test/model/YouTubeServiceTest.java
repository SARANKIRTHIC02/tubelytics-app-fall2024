package model;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class YouTubeServiceTest {
    @Test
    public void testSearchVideosBasedOnQuery() throws IOException, InterruptedException {
        List<VideoSearchResult> results=YouTubeService.searchVideosBasedOnQuery("Lion");
        Assert.assertTrue(results.size()>0);
    }

    @Test
    public void testChannelProfileWithChannelID() throws IOException, InterruptedException {
        ChannelProfileResult result=YouTubeService.getChannelProfile("UCi7Zk9baY1tvdlgxIML8MXg");
        Assert.assertEquals("CTV News",result.getChannelTitle());
    }

    @Test
    public void testChannelProfileWithoutChannelID() throws IOException, InterruptedException {
        ChannelProfileResult result=YouTubeService.getChannelProfile("");
        Assert.assertNull(result);

    }
    //TODO write test case scenarios for exceptional cases
}
