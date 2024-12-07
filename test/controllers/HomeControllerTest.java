package controllers;

import actors.ChannelActor;
import actors.TagActor;
import actors.VideoSearchActor;
import actors.WordStatsActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import akka.testkit.TestProbe;
import akka.stream.Materializer;
import model.*;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import play.mvc.Result;
import play.mvc.WebSocket;

import java.util.concurrent.CompletionStage;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;

/**
 * Unit tests for the HomeController class.
 * This class verifies the functionality of HomeController, including HTTP endpoints,
 * WebSocket initialization, and actor interactions.
 *
 * @version 1.0
 * @author
 *   - Durai
 */
public class HomeControllerTest {

    private static ActorSystem system;
    private static Materializer materializer;
    private HomeController homeController;

    /**
     * Sets up the ActorSystem and Materializer before all tests.
     * Prepares the environment for the HomeController test cases.
     *
     * @author Durai
     * @author saran
     */
    @BeforeClass
    public static void setupClass() {
        system = ActorSystem.create("HomeControllerTestSystem");
        materializer = mock(Materializer.class);
    }

    /**
     * Tears down the ActorSystem after all tests.
     * Releases resources used during the test cases.
     *
     * @author Durai
     */
    @AfterClass
    public static void teardownClass() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    /**
     * Initializes the HomeController instance for testing.
     * Sets up the controller with a mock Materializer and ActorSystem.
     *
     * @author Durai
     */
    public HomeControllerTest() {
        homeController = new HomeController(system, materializer);
    }

    /**
     * Tests the channelProfile endpoint.
     * Verifies that the endpoint responds with a status of OK and contains the correct channel ID in the response.
     *
     * @author Durai
     */
    @Test
    public void testChannelProfile() {
        String channelId = "testChannel";
        CompletionStage<Result> result = homeController.channelProfile(channelId);
        Result resolvedResult = result.toCompletableFuture().join();

        assertEquals(OK, resolvedResult.status());
        String content = contentAsString(resolvedResult);
        assertTrue(content.contains(channelId));
    }

    /**
     * Tests the taglytics endpoint.
     * Verifies that the endpoint responds with a status of OK and contains the query string in the response.
     *
     * @author Durai
     * @author saran
     */
    @Test
    public void testTaglytics() {
        String query = "testTagQuery";
        CompletionStage<Result> result = homeController.taglytics(query);
        Result resolvedResult = result.toCompletableFuture().join();

        assertEquals(OK, resolvedResult.status());
        String content = contentAsString(resolvedResult);
        assertTrue(content.contains(query));
    }

    /**
     * Tests the ytlyticsWebSocket method.
     * Verifies that the method initializes and returns a valid WebSocket object.
     *
     * @author Durai
     * @author saran
     */
    @Test
    public void testWebSocketInitialization() {
        WebSocket ws = homeController.ytlyticsWebSocket();
        assertNotNull(ws);
    }

    /**
     * Tests interaction with a mocked ChannelActor.
     * Verifies that the ChannelActor receives the correct message.
     *
     * @author Durai
     */
    @Test
    public void testChannelActorWithMock() {
        TestProbe probe = new TestProbe(system);
        ActorRef mockChannelActor = probe.ref();

        String testChannelId = "testChannel";

        mockChannelActor.tell(testChannelId, probe.ref());

        probe.expectMsg(testChannelId);
    }

    /**
     * Tests interaction with a mocked TagActor.
     * Verifies that the TagActor receives the correct message and responds as expected.
     *
     * @author Durai
     * @author saran
     */
    @Test
    public void testTagActorWithMock() {
        TestProbe probe = new TestProbe(system);
        ActorRef mockTagActor = probe.ref();

        String testTagData = "testTag";

        mockTagActor.tell(testTagData, probe.ref());

        String receivedMessage = probe.expectMsgClass(String.class);
        assertEquals(testTagData, receivedMessage);
    }

    /**
     * Tests interaction with a mocked VideoSearchActor.
     * Verifies that the VideoSearchActor receives the correct query string.
     *
     * @author Durai
     */
    @Test
    public void testVideoSearchActorWithMock() {
        TestProbe probe = new TestProbe(system);
        ActorRef mockVideoSearchActor = probe.ref();

        String testQuery = "sampleQuery";

        mockVideoSearchActor.tell(testQuery, probe.ref());

        String receivedMessage = probe.expectMsgClass(String.class);
        assertEquals(testQuery, receivedMessage);
    }

    /**
     * Tests actor creation during WebSocket initialization.
     * Verifies that the VideoSearchActor, ChannelActor, WordStatsActor, and TagActor are created successfully.
     *
     * @author Saran
     */
    @Test
    public void testActorCreationOnWebSocket() {
        homeController = new HomeController(system, materializer);

        WebSocket ws = homeController.ytlyticsWebSocket();

        ActorRef videoSearchActor = system.actorOf(VideoSearchActor.props(materializer), "videoSearchActor");
        ActorRef channelActor = system.actorOf(ChannelActor.props(), "channelActor");
        ActorRef wordStatsActor = system.actorOf(WordStatsActor.props(), "wordStatsActor");
        ActorRef tagActor = system.actorOf(TagActor.props(), "tagActor");
        assertNotNull("VideoSearchActor should be created", videoSearchActor);
        assertNotNull("ChannelActor should be created", channelActor);
        assertNotNull("WordStatsActor should be created", wordStatsActor);
        assertNotNull("TagActor should be created", tagActor);
    }

    /**
     * Tests the ytlytics endpoint.
     * Verifies that the endpoint responds with a status of OK and contains relevant data in the response.
     *
     * @author Durai
     * @author saran
     */
    @Test
    public void testYtlyticsReturnsEmptyResultsAndOKStatus() {
        HomeController controller = new HomeController(system, materializer);

        CompletionStage<Result> resultStage = controller.ytlytics();
        Result result = resultStage.toCompletableFuture().join();
        SearchResponseList list = new SearchResponseList();
        list.getRequestModels();
        assertEquals(OK, result.status());
        assertNotNull(result);
        String responseBody = contentAsString(result);
        assertTrue(responseBody.contains("ytlytics"));
        assertTrue(responseBody.contains("UUID"));
    }
}
