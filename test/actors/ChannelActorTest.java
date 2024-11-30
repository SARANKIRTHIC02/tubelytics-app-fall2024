package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import model.ChannelProfileResult;
import model.TubelyticService;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.time.Duration;

import static org.junit.Assert.*;

public class ChannelActorTest {

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
    public void testInvalidChannelId() {
        final String invalidChannelId = "invalid_id";
        try (MockedStatic<TubelyticService> mockedStatic = Mockito.mockStatic(TubelyticService.class)) {
            mockedStatic.when(() -> TubelyticService.fetchChannelDetails(invalidChannelId))
                    .thenThrow(new RuntimeException("Invalid channel ID"));

            final ActorRef channelActor = system.actorOf(ChannelActor.props());

            channelActor.tell(invalidChannelId, testKit.getRef());

            testKit.expectMsgClass(Duration.ofSeconds(5), akka.actor.Status.Failure.class);
        }
    }

    @Test
    public void testUnsupportedMessageType() {
        final ActorRef channelActor = system.actorOf(ChannelActor.props());

        channelActor.tell(123, testKit.getRef());

        akka.actor.Status.Failure response = testKit.expectMsgClass(Duration.ofSeconds(5), akka.actor.Status.Failure.class);
        assertTrue(response.cause() instanceof IllegalArgumentException);
        assertEquals("Unsupported message type", response.cause().getMessage());
    }
}