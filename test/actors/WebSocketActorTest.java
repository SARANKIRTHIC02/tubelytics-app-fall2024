package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.ChannelProfileResult;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Unit tests for the WebSocketActor class.
 * This class verifies the behavior of WebSocketActor in handling different types of requests,
 * including channel requests, word statistics, video search queries, and responses.
 *
 * @version 1.0
 * @author
 *   - Durai
 *   - Saran
 */
public class WebSocketActorTest {

    static ActorSystem system;

    /**
     * Sets up the ActorSystem before all tests.
     * Initializes the test environment for the actor system.
     * @author durai
     * @author saran
     */
    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    /**
     * Tears down the ActorSystem after all tests.
     * Frees up resources and shuts down the test environment.
     */
    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    /**
     * Tests handling of a channel request message.
     * Verifies that the channel ID is forwarded correctly to the ChannelActor.
     *  @author durai
     *   @author saran
     */
    @Test
    public void testChannelRequest() {
        new TestKit(system) {{
            TestKit out = new TestKit(system);
            TestKit channelActor = new TestKit(system);
            ActorRef webSocketActor = system.actorOf(WebSocketActor.props(
                    "testSession", out.getRef(), getRef(), channelActor.getRef(), getRef(), getRef()));

            webSocketActor.tell("channel:UC123456789", getRef());
            channelActor.expectMsg(Duration.ofSeconds(1), "UC123456789");
        }};
    }

    /**
     * Tests handling of a word statistics request message.
     * Verifies that the word stats query is forwarded to the WordStatsActor.
     *  @author durai
     *  @author saran
     */
    @Test
    public void testWordStatsRequest() {
        new TestKit(system) {{
            TestKit out = new TestKit(system);
            TestKit wordStatsActor = new TestKit(system);
            ActorRef webSocketActor = system.actorOf(WebSocketActor.props(
                    "testSession", out.getRef(), getRef(), getRef(), wordStatsActor.getRef(), getRef()));

            webSocketActor.tell("wordStats:test query", getRef());
            wordStatsActor.expectMsg(Duration.ofSeconds(1), "test query");
        }};
    }

    /**
     * Tests handling of a video search query message.
     * Verifies that the query is forwarded to the VideoSearchActor.
     * @author durai
     * @author saran
     */
    @Test
    public void testSearchQuery() {
        new TestKit(system) {{
            TestKit out = new TestKit(system);
            TestKit videoSearchActor = new TestKit(system);
            ActorRef webSocketActor = system.actorOf(WebSocketActor.props(
                    "testSession", out.getRef(), videoSearchActor.getRef(), getRef(), getRef(), getRef()));

            webSocketActor.tell("test query", getRef());
            videoSearchActor.expectMsgClass(Duration.ofSeconds(1), VideoSearchActor.SearchQuery.class);
        }};
    }

    /**
     * Tests handling of video search results.
     * Verifies that the search results are returned as a serialized JSON response.
     * @throws Exception if JSON processing fails
     * @author durai
     * @author saran
     */
    @Test
    public void testVideoSearchResults() throws Exception {
        new TestKit(system) {{
            TestKit out = new TestKit(system);
            ActorRef webSocketActor = system.actorOf(WebSocketActor.props(
                    "testSession", out.getRef(), getRef(), getRef(), getRef(), getRef()));

            Map<String, Object> videoSearchResult = Map.of("id", "12345", "title", "Test Video");
            webSocketActor.tell(videoSearchResult, getRef());

            String response = out.expectMsgClass(Duration.ofSeconds(1), String.class);
            ObjectMapper mapper = new ObjectMapper();
            Map<?, ?> parsedResponse = mapper.readValue(response, Map.class);

            assertEquals(videoSearchResult.get("id"), parsedResponse.get("id"));
            assertEquals(videoSearchResult.get("title"), parsedResponse.get("title"));
        }};
    }

    /**
     * Tests handling of a channel profile response.
     * Verifies that the channel profile result is returned as a serialized JSON response.
     *
     * @throws Exception if JSON processing fails
     *
     * @author durai
     * @author saran
     */
    @Test
    public void testChannelProfileResponse() throws Exception {
        new TestKit(system) {{
            TestKit out = new TestKit(system);
            ActorRef webSocketActor = system.actorOf(WebSocketActor.props(
                    "testSession", out.getRef(), getRef(), getRef(), getRef(), getRef()));

            ChannelProfileResult channelProfile = new ChannelProfileResult(
                    "UC123456789",
                    "Test Channel",
                    "Description",
                    1000L,
                    "thumbnail.jpg",
                    "US",
                    new ArrayList<>()
            );

            webSocketActor.tell(channelProfile, getRef());
            String response = out.expectMsgClass(Duration.ofSeconds(1), String.class);

            ObjectMapper mapper = new ObjectMapper();
            ChannelProfileResult responseProfile = mapper.readValue(response, ChannelProfileResult.class);

            assertEquals(channelProfile.getChannelId(), responseProfile.getChannelId());
            assertEquals(channelProfile.getChannelTitle(), responseProfile.getChannelTitle());
            assertEquals(channelProfile.getChannelDescription(), responseProfile.getChannelDescription());
        }};
    }

    /**
     * Tests handling of tag results.
     * Verifies that the tag results are returned as a serialized JSON response.
     *
     * @throws Exception if JSON processing fails
     * @author durai
     * @author saran
     */
    @Test
    public void testTagResults() throws Exception {
        new TestKit(system) {{
            TestKit out = new TestKit(system);
            ActorRef webSocketActor = system.actorOf(WebSocketActor.props(
                    "testSession", out.getRef(), getRef(), getRef(), getRef(), getRef()));

            List<String> tagResults = List.of("tag1", "tag2", "tag3");
            webSocketActor.tell(tagResults, getRef());

            String response = out.expectMsgClass(Duration.ofSeconds(1), String.class);

            ObjectMapper mapper = new ObjectMapper();
            List<String> parsedResponse = mapper.readValue(response, List.class);

            assertEquals(tagResults, parsedResponse);
        }};
    }

    /**
     * Tests handling of unsupported message types.
     * Verifies that the actor returns an error message for unsupported types.
     * @author durai
     * @author saran
     */
    @Test
    public void testUnsupportedMessageType() {
        new TestKit(system) {{
            TestKit out = new TestKit(system);
            ActorRef webSocketActor = system.actorOf(WebSocketActor.props(
                    "testSession", out.getRef(), getRef(), getRef(), getRef(), getRef()));

            webSocketActor.tell(12345, getRef());
            String response = out.expectMsgClass(Duration.ofSeconds(1), String.class);

            assertEquals("Error: Unsupported message type", response);
        }};
    }

}
