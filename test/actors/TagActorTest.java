package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import model.TubelyticService;
import model.VideoSearchResult;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

public class TagActorTest {
    private static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("TagActorTestSystem");
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testHandleValidTagWithoutAPI() {
        String testTag = "technology";
        List<VideoSearchResult> mockResults = new ArrayList<>();
        mockResults.add(new VideoSearchResult("video1", "Video Title 1", "Description 1", "thumbnail1", "channel1", "Channel 1", null));
        mockResults.add(new VideoSearchResult("video2", "Video Title 2", "Description 2", "thumbnail2", "channel2", "Channel 2", null));
        TubelyticService tubelyticServiceMock = mock(TubelyticService.class);
        when(TubelyticService.fetchResults(testTag)).thenReturn(mockResults);
        new TestKit(system) {{
            final ActorRef tagActor = system.actorOf(TagActor.props());
            tagActor.tell(testTag, getRef());
            expectMsg(mockResults);
            verify(tubelyticServiceMock, times(1)).fetchResults(testTag);
        }};
    }

    @Test
    public void testHandleEmptyTagWithoutAPI() {
        String testTag = "";
        List<VideoSearchResult> mockResults = new ArrayList<>(); // Empty list for empty tag
        TubelyticService tubelyticServiceMock = mock(TubelyticService.class);
        when(TubelyticService.fetchResults(testTag)).thenReturn(mockResults);
        new TestKit(system) {{
            final ActorRef tagActor = system.actorOf(TagActor.props());
            tagActor.tell(testTag, getRef());
            expectMsg(mockResults);
            verify(tubelyticServiceMock, times(1)).fetchResults(testTag);
        }};
    }

    @Test
    public void testHandleInvalidCharactersWithoutAPI() {
        String invalidTag = "!@#$%^&*";
        TubelyticService tubelyticServiceMock = mock(TubelyticService.class);
        when(TubelyticService.fetchResults(invalidTag)).thenThrow(new RuntimeException("Invalid input"));
        new TestKit(system) {{
            final ActorRef tagActor = system.actorOf(TagActor.props());
            tagActor.tell(invalidTag, getRef());
            expectMsgClass(akka.actor.Status.Failure.class);
            verify(tubelyticServiceMock, times(1)).fetchResults(invalidTag);
        }};
    }

    @Test
    public void testHandleConcurrencyWithoutAPI() {
        // Prepare mock data
        String tag1 = "tag1";
        String tag2 = "tag2";
        List<VideoSearchResult> results1 = new ArrayList<>();
        results1.add(new VideoSearchResult("video1", "Title1", "Desc1", "thumb1", "channel1", "Channel1", null));
        List<VideoSearchResult> results2 = new ArrayList<>();
        results2.add(new VideoSearchResult("video2", "Title2", "Desc2", "thumb2", "channel2", "Channel2", null));
        TubelyticService tubelyticServiceMock = mock(TubelyticService.class);
        when(TubelyticService.fetchResults(tag1)).thenReturn(results1);
        when(TubelyticService.fetchResults(tag2)).thenReturn(results2);
        new TestKit(system) {{
            final ActorRef tagActor = system.actorOf(TagActor.props());
            tagActor.tell(tag1, getRef());
            tagActor.tell(tag2, getRef());
            expectMsg(results1);
            expectMsg(results2);
            verify(tubelyticServiceMock, times(1)).fetchResults(tag1);
            verify(tubelyticServiceMock, times(1)).fetchResults(tag2);
        }};
    }
}
