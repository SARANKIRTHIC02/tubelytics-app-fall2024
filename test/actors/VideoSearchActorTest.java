package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
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

public class VideoSearchActorTest {

    static ActorSystem system;
    private TestKit testKit;
    private MockedStatic<TubelyticService> mockedStatic;

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
        mockedStatic = Mockito.mockStatic(TubelyticService.class);
    }

    @After
    public void teardown() {
        testKit = null;
        if (mockedStatic != null) {
            mockedStatic.close();
        }
    }

    @Test
    public void testValidQuery() {
        final String query = "test query";
        List<VideoSearchResult> mockResults = IntStream.range(0, 10)
                .mapToObj(i -> new VideoSearchResult("id" + i, "title" + i, "desc" + i, "thumb" + i, "channel" + i, "channelTitle" + i, List.of()))
                .collect(Collectors.toList());

        mockedStatic.when(() -> TubelyticService.fetchResults(query)).thenReturn(mockResults);

        final ActorRef videoSearchActor = system.actorOf(VideoSearchActor.props());

        videoSearchActor.tell(query, testKit.getRef());

        Map<String, Object> response = testKit.expectMsgClass(Duration.ofSeconds(5), Map.class);
        assertEquals(query, response.get("query"));

        List<VideoSearchResult> actualResults = (List<VideoSearchResult>) response.get("results");
        assertEquals(mockResults.size(), actualResults.size());
    }

    @Test
    public void testUnsupportedMessageType() {
        final ActorRef videoSearchActor = system.actorOf(VideoSearchActor.props());

        videoSearchActor.tell(123, testKit.getRef());

        akka.actor.Status.Failure response = testKit.expectMsgClass(Duration.ofSeconds(5), akka.actor.Status.Failure.class);
        assertTrue(response.cause() instanceof IllegalArgumentException);
        assertEquals("Unsupported message type", response.cause().getMessage());
    }
}