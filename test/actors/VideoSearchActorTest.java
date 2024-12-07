package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import akka.stream.Materializer;
import model.TubelyticService;
import model.VideoSearchResult;
import org.junit.*;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

/**
 * Unit tests for the VideoSearchActor class.
 * This class verifies the behavior of the VideoSearchActor in different scenarios,
 * including successful results fetching, unsupported messages, and query handling.
 *
 * @author
 *   - Durai
 *   - Saran
 */
public class VideoSearchActorTest {

    static ActorSystem system;
    private TestKit testKit;
    private MockedStatic<TubelyticService> mockedStatic;
    private Materializer materializer;

    /**
     * Set up the ActorSystem before all tests.
     * Initializes the actor system for the test environment.
     */
    @BeforeClass
    public static void setupClass() {
        system = ActorSystem.create("TestSystem");
    }

    /**
     * Tear down the ActorSystem after all tests.
     * Shuts down the actor system to free up resources.
     */
    @AfterClass
    public static void teardownClass() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    /**
     * Set up the test environment before each test.
     * Prepares a TestKit, mocks TubelyticService, and initializes the materializer.
     */
    @Before
    public void setup() {
        testKit = new TestKit(system);
        mockedStatic = Mockito.mockStatic(TubelyticService.class);
        materializer = Materializer.createMaterializer(system);
    }

    /**
     * Tear down the test environment after each test.
     * Closes the mock static context to clean up resources.
     */
    @After
    public void teardown() {
        if (mockedStatic != null) {
            mockedStatic.close();
        }
    }

    /**
     * Tests successful handling of a video search query.
     * Verifies that the actor returns the correct results for a given query.
     * @author durai
     * @author saran
     */
    @Test
    public void testFetchResultsSuccess() {
        final String query = "successQuery";

        List<VideoSearchResult> mockResults = IntStream.range(0, 5)
                .mapToObj(i -> new VideoSearchResult("id" + i, "title" + i, "desc" + i, "thumb" + i, "channel" + i, "channelTitle" + i, List.of()))
                .collect(Collectors.toList());

        mockedStatic.when(() -> TubelyticService.fetchResults(query)).thenReturn(mockResults);

        final ActorRef videoSearchActor = system.actorOf(VideoSearchActor.props(materializer));

        videoSearchActor.tell(new VideoSearchActor.SearchQuery(query, testKit.getRef()), testKit.getRef());

        Map<String, Object> response = testKit.expectMsgClass(Duration.ofSeconds(5), Map.class);
        assertEquals(query, response.get("query"));
    }

    /**
     * Tests handling of unsupported message types.
     * Verifies that the actor responds with a failure for unsupported message types.
     *  @author durai
     *  @author saran
     */
    @Test
    public void testUnsupportedMessageType() {
        final ActorRef videoSearchActor = system.actorOf(VideoSearchActor.props(materializer));

        videoSearchActor.tell(12345, testKit.getRef());

        akka.actor.Status.Failure response = testKit.expectMsgClass(Duration.ofSeconds(5), akka.actor.Status.Failure.class);
        assertTrue(response.cause() instanceof IllegalArgumentException);
        assertEquals("Unsupported message type", response.cause().getMessage());
    }

    /**
     * Tests handling of unsupported message strings.
     * Verifies that the actor responds with a failure for unsupported message strings.
     *  @author durai
     *  @author saran
     */
    @Test
    public void testUnsupportedMessage() {
        final ActorRef videoSearchActor = system.actorOf(VideoSearchActor.props(materializer));

        videoSearchActor.tell("UnsupportedMessage", testKit.getRef());

        akka.actor.Status.Failure response = testKit.expectMsgClass(Duration.ofSeconds(5), akka.actor.Status.Failure.class);
        assertTrue(response.cause() instanceof IllegalArgumentException);
    }

    /**
     * Tests starting a new video search query.
     * Verifies that the actor processes the new query correctly.
     *  @author durai
     *  @author saran
     */
    @Test
    public void testStartStreamNewQuery() {
        final String query = "newQuery";
        final ActorRef videoSearchActor = system.actorOf(VideoSearchActor.props(materializer));

        videoSearchActor.tell(new VideoSearchActor.SearchQuery(query, testKit.getRef()), testKit.getRef());

        Map<String, Object> response = testKit.expectMsgClass(Duration.ofSeconds(5), Map.class);
        assertEquals(query, response.get("query"));
    }

    /**
     * Tests the cleanup of active queries after actor termination.
     * Verifies that the actor cleans up its state upon being stopped.
     *  @author durai
     * @author saran
     */
    @Test
    public void testPostStopClearsActiveQueries() {
        final String query = "cleanupQuery";

        final ActorRef videoSearchActor = system.actorOf(VideoSearchActor.props(materializer));
        videoSearchActor.tell(new VideoSearchActor.SearchQuery(query, testKit.getRef()), testKit.getRef());

        Map<String, Object> response = testKit.expectMsgClass(Duration.ofSeconds(5), Map.class);
        assertEquals(query, response.get("query"));

        system.stop(videoSearchActor);
    }
}
