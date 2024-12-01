package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.javadsl.TestKit;
import model.TubelyticService;
import model.VideoSearchResult;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

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
    public void testHandleValidTag() {
        // Prepare mock data
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
    public void testHandleExceptionDuringFetch() {
        String testTag = "invalid";

        TubelyticService tubelyticServiceMock = mock(TubelyticService.class);
        when(TubelyticService.fetchResults(testTag)).thenThrow(new RuntimeException("Mocked exception"));

        new TestKit(system) {{
            final ActorRef tagActor = system.actorOf(TagActor.props());
            tagActor.tell(testTag, getRef());
            expectMsgClass(akka.actor.Status.Failure.class);
            verify(tubelyticServiceMock, times(1)).fetchResults(testTag);
        }};
    }

    @Test
    public void testHandleUnsupportedMessage() {
        new TestKit(system) {{
            final ActorRef tagActor = system.actorOf(TagActor.props());
            tagActor.tell(12345, getRef());
            expectMsgClass(akka.actor.Status.Failure.class);
        }};
    }
}
