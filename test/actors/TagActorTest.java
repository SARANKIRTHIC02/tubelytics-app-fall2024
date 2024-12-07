package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit tests for the TagActor class.
 * This class verifies the functionality of the TagActor, focusing on its ability to handle unsupported messages.
 * @author sushanth
 */
public class TagActorTest {

    private static ActorSystem system;

    /**
     * Sets up the ActorSystem before all tests.
     * Initializes the test environment for the TagActor.
     */
    @BeforeClass
    public static void setup() {
        system = ActorSystem.create("TagActorTestSystem");
    }

    /**
     * Tears down the ActorSystem after all tests.
     * Releases resources used during the test cases.
     */
    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    /**
     * Tests handling of unsupported messages by the TagActor.
     * Verifies that the actor responds with a failure when receiving an unsupported message type.
     * @author sushanth
     */
    @Test
    public void testHandleUnsupportedMessage() {
        new TestKit(system) {{
            final ActorRef tagActor = system.actorOf(TagActor.props());
            tagActor.tell(12345, getRef()); // Send an unsupported message type
            expectMsgClass(akka.actor.Status.Failure.class); // Expect a failure response
        }};
    }
}
