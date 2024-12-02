package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.ChannelProfileResult;

import java.util.List;
import java.util.Map;
/**
 * WebSocketActor handles WebSocket messages, processes requests for video search, channel profiles,
 * word statistics, and tag-based results, and sends the responses back to the client in JSON format.
 *
 * @author Durai
 * @author Saran
 */
public class WebSocketActor extends AbstractActor {
    /** Session ID */
    private final String sessionId;

    /** ActorRef */
    private final ActorRef out;

    /** VideoSEarchActor */
    private final ActorRef videoSearchActor;

    /** ChannelActor*/
    private final ActorRef channelActor;

    /** WordStatsActor*/
    private final ActorRef wordStatsActor;

    /** TagActor */
    private final ActorRef tagActor;

    /**
     * Creates a new {@code WebSocketActor}.
     * @return the {@code Props} for creating the actor
     * @author Durai
     * @author Saran
     */
    public static Props props(String sessionId, ActorRef out, ActorRef videoSearchActor, ActorRef channelActor, ActorRef wordStatsActor, ActorRef tagActor) {
        return Props.create(WebSocketActor.class, () -> new WebSocketActor(sessionId, out, videoSearchActor, channelActor, wordStatsActor, tagActor));
    }

    public WebSocketActor(String sessionId, ActorRef out, ActorRef videoSearchActor, ActorRef channelActor, ActorRef wordStatsActor, ActorRef tagActor) {
        this.sessionId = sessionId;
        this.out = out;
        this.videoSearchActor = videoSearchActor;
        this.channelActor = channelActor;
        this.wordStatsActor = wordStatsActor;
        this.tagActor = tagActor;
    }

    /**
     * Defines how the actor handles incoming messages.
     *
     * @return the behavior of the actor
     * @author Durai
     * @author Saran
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, message -> {
                    if (message.startsWith("channel:")) {
                        String channelId = message.substring("channel:".length());
                        System.out.println("[WebSocketActor] Channel request for ID: " + channelId);
                        channelActor.tell(channelId, getSelf());
                    } else if (message.startsWith("wordStats:")) {
                        String searchQuery = message.substring("wordStats:".length());
                        System.out.println("[WebSocketActor] Word stats request for query: " + searchQuery);
                        wordStatsActor.tell(searchQuery, getSelf());
                    } else if (message.startsWith("tag:")) {
                        String tagQuery = message.substring("tag:".length());
                        System.out.println("[WebSocketActor] Tag request for query: " + tagQuery);
                        tagActor.tell(tagQuery, getSelf());
                    } else {
                        System.out.println("[WebSocketActor] Search query received: " + message);
                        videoSearchActor.tell(new VideoSearchActor.SearchQuery(message, getSelf()), getSelf());
                    }
                })

                // Handle video search results
                .match(Map.class, response -> {
                    System.out.println("[WebSocketActor] Video search results received: ");
                    sendToClient(response);
                })

                // Handle channel profile results
                .match(ChannelProfileResult.class, channelProfile -> {
                    System.out.println("[WebSocketActor] Channel profile received: " + channelProfile);
                    sendToClient(channelProfile);
                })

                // Handle word statistics results
                .match(Map.class, wordStats -> {
                    System.out.println("[WebSocketActor] Word statistics received: " + wordStats);
                    sendToClient(wordStats);
                })

                // Handle tag-based results
                .match(List.class, tagResults -> {
                    System.out.println("[WebSocketActor] Tag results received: " + tagResults);
                    sendToClient(tagResults);
                })

                // Handle unsupported messages
                .matchAny(message -> {
                    System.err.println("[WebSocketActor] Unsupported message received: " + message);
                    out.tell("Error: Unsupported message type", getSelf());
                })
                .build();
    }

    /**
     * Sends the response to the client as a JSON string.
     *
     * @param response the response to send
     * @author Durai
     * @author Saran
     */
    private void sendToClient(Object response) {
        String jsonResponse = null;
        try {
            jsonResponse = new ObjectMapper().writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        out.tell(jsonResponse, getSelf());
        System.out.println("[WebSocketActor] Sent to client: ");
    }

    /**
     * Logs when the WebSocket session starts.
     * @author Durai
     * @author Saran
     */
    @Override
    public void preStart() {
        System.out.println("[WebSocketActor] WebSocket session started for ID: " + sessionId);
    }
    /**
     * Logs when the WebSocket session stops.
     * @author Durai
     * @author Saran
     */
    @Override
    public void postStop() {
        System.out.println("[WebSocketActor] WebSocket session stopped for ID: " + sessionId);
    }
}
