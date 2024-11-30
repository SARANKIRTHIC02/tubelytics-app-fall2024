package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import model.VideoSearchResult;
import org.junit.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class WordStatsActorTest {

    static ActorSystem system;
    private TestKit testKit;

    @BeforeClass
    public static void setupClass() {
        system = ActorSystem.create("TestSystem");
    }

    @AfterClass
    public static void teardownClass() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Before
    public void setup() {
        testKit = new TestKit(system);
    }

    @After
    public void teardown() {
        testKit = null;
    }

    @Test
    public void testProcessSingleVideoSearchResult() {
        final ActorRef wordStatsActor = system.actorOf(WordStatsActor.props());
        List<VideoSearchResult> results = Collections.singletonList(
                new VideoSearchResult("videoId1", "Test Video", "This is a test video description",
                        "thumbnail1.jpg", "channel1", "Channel 1", Collections.emptyList())
        );
        wordStatsActor.tell(new WordStatsActor.VideoSearchResultsMessage(results), testKit.getRef());

        Map<String, Long> response = testKit.expectMsgClass(Duration.ofSeconds(5), Map.class);
        assertEquals(6, response.size());
        assertEquals(1L, (long) response.get("test"));
        assertEquals(1L, (long) response.get("video"));
        assertEquals(1L, (long) response.get("description"));
    }

    @Test
    public void testProcessMultipleVideoSearchResults() {
        final ActorRef wordStatsActor = system.actorOf(WordStatsActor.props());
        List<VideoSearchResult> results = Arrays.asList(
                new VideoSearchResult("videoId1", "Video 1", "This is the first video",
                        "thumbnail1.jpg", "channel1", "Channel 1", Collections.emptyList()),
                new VideoSearchResult("videoId2", "Video 2", "This is the second video",
                        "thumbnail2.jpg", "channel2", "Channel 2", Collections.emptyList())
        );
        wordStatsActor.tell(new WordStatsActor.VideoSearchResultsMessage(results), testKit.getRef());
        Map<String, Long> response = testKit.expectMsgClass(Duration.ofSeconds(5), Map.class);
        assertEquals(6, response.size());
        assertEquals(2L, (long) response.get("video"));
        assertEquals(2L, (long) response.get("is"));
        assertEquals(2L, (long) response.get("the"));
        assertEquals(1L, (long) response.get("first"));
        assertEquals(1L, (long) response.get("second"));
    }

    @Test
    public void testCaseInsensitivity() {
        final ActorRef wordStatsActor = system.actorOf(WordStatsActor.props());
        List<VideoSearchResult> results = Collections.singletonList(
                new VideoSearchResult("videoId1", "Test Video", "The THE tHe ThE",
                        "thumbnail1.jpg", "channel1", "Channel 1", Collections.emptyList())
        );
        wordStatsActor.tell(new WordStatsActor.VideoSearchResultsMessage(results), testKit.getRef());
        Map<String, Long> response = testKit.expectMsgClass(Duration.ofSeconds(5), Map.class);
        assertEquals(1, response.size());
        assertEquals(4L, (long) response.get("the"));
    }

    @Test
    public void testPunctuation() {
        final ActorRef wordStatsActor = system.actorOf(WordStatsActor.props());
        List<VideoSearchResult> results = Collections.singletonList(
                new VideoSearchResult("videoId1", "Test Video", "Hello, world! How are you?",
                        "thumbnail1.jpg", "channel1", "Channel 1", Collections.emptyList())
        );
        wordStatsActor.tell(new WordStatsActor.VideoSearchResultsMessage(results), testKit.getRef());
        Map<String, Long> response = testKit.expectMsgClass(Duration.ofSeconds(5), Map.class);
        assertEquals(5, response.size());
        assertEquals(1L, (long) response.get("hello"));
        assertEquals(1L, (long) response.get("world"));
        assertEquals(1L, (long) response.get("how"));
        assertEquals(1L, (long) response.get("are"));
        assertEquals(1L, (long) response.get("you"));
    }

    @Test
    public void testLongDescription() {
        final ActorRef wordStatsActor = system.actorOf(WordStatsActor.props());
        String longDescription = "This is a very long description. " +
                "It contains many words. Some words are repeated. " +
                "Words like 'is' and 'a' appear multiple times. " +
                "The purpose is to test the actor with a longer input.";
        List<VideoSearchResult> results = Collections.singletonList(
                new VideoSearchResult("videoId1", "Long Video", longDescription,
                        "thumbnail1.jpg", "channel1", "Channel 1", Collections.emptyList())
        );
        wordStatsActor.tell(new WordStatsActor.VideoSearchResultsMessage(results), testKit.getRef());
        Map<String, Long> response = testKit.expectMsgClass(Duration.ofSeconds(5), Map.class);
        assertTrue(response.size() > 10);
        assertEquals(3L, (long) response.get("is"));
        assertEquals(3L, (long) response.get("a"));
        assertEquals(3L, (long) response.get("words"));
        assertEquals(2L, (long) response.get("the"));
        assertEquals(1L, (long) response.get("to"));
    }
}