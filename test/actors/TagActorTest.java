/*
package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import model.TubelyticService;
import model.VideoSearchResult;
import org.junit.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class TagActorTest {

    public static ActorSystem system;

    @BeforeClass
    public static void setupClass() {
        system = ActorSystem.create("TestSystem");
    }

    @AfterClass
    public static void teardownClass() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testValidTagQuery() {
        new TestKit(system) {{
            TubelyticService mockService = mock(TubelyticService.class);
            String testTag = "technology";
            List<VideoSearchResult> mockResults = IntStream.range(0, 5)
                    .mapToObj(i -> new VideoSearchResult(
                            "video" + i, "title" + i, "desc" + i,
                            "thumb" + i, "channel" + i, "channelTitle" + i, List.of()))
                    .collect(Collectors.toList());
            when(TubelyticService.fetchResults(testTag)).thenReturn(mockResults);
            final ActorRef tagActor = system.actorOf(TagActor.props());
            tagActor.tell(testTag, getRef());
            List<VideoSearchResult> receivedResults = expectMsgClass(Duration.ofSeconds(5), List.class);
            assertEquals(5, receivedResults.size());
            assertEquals(mockResults, receivedResults);
            verify(mockService, times(1)).fetchResults(testTag);
        }};
    }

    @Test
    public void testEmptyTagQuery() {
        new TestKit(system) {
            {
                TubelyticService mockService = mock(TubelyticService.class);
                String emptyTag = "";
                List<VideoSearchResult> mockResults = new ArrayList<>();
                when(TubelyticService.fetchResults(emptyTag)).thenReturn(mockResults);
                final ActorRef tagActor = system.actorOf(TagActor.props());
                tagActor.tell(emptyTag, getRef());
                List<VideoSearchResult> receivedResults = expectMsgClass(Duration.ofSeconds(5), List.class);
                assertTrue(receivedResults.isEmpty());
                verify(mockService, times(1)).fetchResults(emptyTag);
            }};
    }

    @Test
    public void testInvalidTagQuery() {
        new TestKit(system) {{
            TubelyticService mockService = mock(TubelyticService.class);
            String invalidTag = "!@#$%^&*";
            when(TubelyticService.fetchResults(invalidTag)).thenThrow(new RuntimeException("Invalid tag format"));
            final ActorRef tagActor = system.actorOf(TagActor.props());
            tagActor.tell(invalidTag, getRef());
            expectMsgClass(Duration.ofSeconds(5), akka.actor.Status.Failure.class);
            verify(mockService, times(1)).fetchResults(invalidTag);
        }};
    }

    @Test
    public void testConcurrencyWithMultipleTags() {
        new TestKit(system) {{
            TubelyticService mockService = mock(TubelyticService.class);
            String tag1 = "tag1";
            String tag2 = "tag2";
            List<VideoSearchResult> results1 = List.of(new VideoSearchResult("vid1", "Title1", "Desc1", "Thumb1", "Ch1", "ChTitle1", List.of()));
            List<VideoSearchResult> results2 = List.of(new VideoSearchResult("vid2", "Title2", "Desc2", "Thumb2", "Ch2", "ChTitle2", List.of()));
            when(TubelyticService.fetchResults(tag1)).thenReturn(results1);
            when(TubelyticService.fetchResults(tag2)).thenReturn(results2);
            final ActorRef tagActor = system.actorOf(TagActor.props());
            tagActor.tell(tag1, getRef());
            tagActor.tell(tag2, getRef());
            List<VideoSearchResult> receivedResults1 = expectMsgClass(Duration.ofSeconds(5), List.class);
            assertEquals(results1, receivedResults1);

            List<VideoSearchResult> receivedResults2 = expectMsgClass(Duration.ofSeconds(5), List.class);
            assertEquals(results2, receivedResults2);

            // Verify the mocked service was called
            verify(mockService, times(1)).fetchResults(tag1);
            verify(mockService, times(1)).fetchResults(tag2);
        }};
    }
}*/
