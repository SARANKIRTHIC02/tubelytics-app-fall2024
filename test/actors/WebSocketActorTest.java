package actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.testkit.javadsl.TestKit;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.ChannelProfileResult;
import model.VideoSearchResult;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class WebSocketActorTest {

    static ActorSystem system;

    @BeforeClass
    public static void setup() {
        system = ActorSystem.create();
    }

    @AfterClass
    public static void teardown() {
        TestKit.shutdownActorSystem(system);
        system = null;
    }

    @Test
    public void testChannelRequest() {
        new TestKit(system) {{
            TestKit out = new TestKit(system);
            TestKit channelActor = new TestKit(system);
            ActorRef webSocketActor = system.actorOf(WebSocketActor.props(
                    "testSession", out.getRef(), getRef(), channelActor.getRef(), getRef()));

            webSocketActor.tell("channel:UC123456789", getRef());
            channelActor.expectMsg(Duration.ofSeconds(1), "UC123456789");
        }};
    }

    @Test
    public void testWordStatsRequest() {
        new TestKit(system) {{
            TestKit out = new TestKit(system);
            TestKit videoSearchActor = new TestKit(system);
            ActorRef webSocketActor = system.actorOf(WebSocketActor.props(
                    "testSession", out.getRef(), videoSearchActor.getRef(), getRef(), getRef()));

            webSocketActor.tell("wordStats:test query", getRef());
            videoSearchActor.expectMsg(Duration.ofSeconds(1), "test query");
        }};
    }

    @Test
    public void testSearchQuery() {
        new TestKit(system) {{
            TestKit out = new TestKit(system);
            TestKit videoSearchActor = new TestKit(system);
            ActorRef webSocketActor = system.actorOf(WebSocketActor.props(
                    "testSession", out.getRef(), videoSearchActor.getRef(), getRef(), getRef()));

            webSocketActor.tell("test query", getRef());
            videoSearchActor.expectMsg(Duration.ofSeconds(1), "test query");
        }};
    }

    @Test
    public void testVideoSearchResults() {
        new TestKit(system) {{
            TestKit out = new TestKit(system);
            TestKit wordStatsActor = new TestKit(system);
            ActorRef webSocketActor = system.actorOf(WebSocketActor.props(
                    "testSession", out.getRef(), getRef(), getRef(), wordStatsActor.getRef()));

            List<VideoSearchResult> results = IntStream.range(0, 2)
                    .mapToObj(i -> new VideoSearchResult("id" + i, "title" + i, "desc" + i, "thumb" + i, "channel" + i, "channelTitle" + i, List.of()))
                    .collect(Collectors.toList());

            webSocketActor.tell(results, getRef());
            WordStatsActor.VideoSearchResultsMessage msg = wordStatsActor.expectMsgClass(Duration.ofSeconds(1), WordStatsActor.VideoSearchResultsMessage.class);
            assertEquals(results, msg.getVideoResults());
        }};
    }


    @Test
    public void testChannelProfileResponse() throws Exception {
        new TestKit(system) {{
            TestKit out = new TestKit(system);
            ActorRef webSocketActor = system.actorOf(WebSocketActor.props(
                    "testSession", out.getRef(), getRef(), getRef(), getRef()));

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

    @Test
    public void testUnsupportedMessageType() {
        new TestKit(system) {{
            TestKit out = new TestKit(system);
            ActorRef webSocketActor = system.actorOf(WebSocketActor.props(
                    "testSession", out.getRef(), getRef(), getRef(), getRef()));

            webSocketActor.tell(123, getRef());
            out.expectMsg(Duration.ofSeconds(1), "Error: Unsupported message type");
        }};
    }
}