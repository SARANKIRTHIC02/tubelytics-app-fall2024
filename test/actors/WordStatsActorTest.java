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

/**
 * Unit tests for the WordStatsActor class.
 * This class validates the actor's functionality for processing video search results
 * and generating word statistics.
 *
 * @version 1.0
 * @author
 *   - Saran
 */
public class WordStatsActorTest {

    static ActorSystem system;
    private TestKit testKit;

    /**
     * Sets up the ActorSystem before all tests.
     * Initializes the test environment for the actor system.
     *
     * @author Saran
     */
    @BeforeClass
    public static void setupClass() {
        system = ActorSystem.create("TestSystem");
    }

    /**
     * Tears down the ActorSystem after all tests.
     * Frees up resources and shuts down the test environment.
     *
     * @author Saran
     */
    @AfterClass
    public static void teardownClass() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    /**
     * Sets up the TestKit before each test.
     * Prepares the TestKit for communication with actors.
     *
     * @author Saran
     */
    @Before
    public void setup() {
        testKit = new TestKit(system);
    }

    /**
     * Cleans up the TestKit after each test.
     * Resets the TestKit to release resources.
     *
     * @author Saran
     */
    @After
    public void teardown() {
        testKit = null;
    }

    /**
     * Tests processing of a single video search result.
     * Verifies that word counts are generated correctly for one video's description.
     *
     * @author Saran
     */
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

    /**
     * Tests processing of multiple video search results.
     * Verifies that word counts are aggregated correctly across multiple videos' descriptions.
     *
     * @author Saran
     */
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

    /**
     * Tests case insensitivity in word statistics.
     * Verifies that word counts are case-insensitive and aggregated correctly.
     *
     * @author Saran
     */
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

    /**
     * Tests handling of punctuation in video descriptions.
     * Verifies that punctuation marks are removed before calculating word statistics.
     *
     * @author Saran
     */
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

    /**
     * Tests processing of a long video description.
     * Verifies that the actor handles long descriptions and calculates word statistics correctly.
     *
     * @author Saran
     */
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
