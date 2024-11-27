package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.ChannelProfileResult;
import model.VideoSearchResult;
import java.util.List;
import java.util.Map;

public class WebSocketActor extends AbstractActor {
    private final String sessionId;
    private final ActorRef out;
    private final ActorRef videoSearchActor;
    private final ActorRef channelActor;
    private final ActorRef wordStatsActor;

    public static Props props(String sessionId, ActorRef out, ActorRef videoSearchActor, ActorRef channelActor, ActorRef wordStatsActor) {
        return Props.create(WebSocketActor.class, () -> new WebSocketActor(sessionId, out, videoSearchActor, channelActor, wordStatsActor));
    }

    public WebSocketActor(String sessionId, ActorRef out, ActorRef videoSearchActor, ActorRef channelActor, ActorRef wordStatsActor) {
        this.sessionId = sessionId;
        this.out = out;
        this.videoSearchActor = videoSearchActor;
        this.channelActor = channelActor;
        this.wordStatsActor = wordStatsActor;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, message -> {
                    if (message.startsWith("channel:")) {
                        String channelId = message.substring("channel:".length());
                        System.out.println("WebSocketActor received channel request for channel ID: " + channelId);

                        channelActor.tell(channelId, getSelf());
                    } else if (message.startsWith("wordStats:")) {
                        String searchQuery = message.substring("wordStats:".length());
                        System.out.println("WebSocketActor received word stats request for query: " + searchQuery);

                        videoSearchActor.tell(searchQuery, getSelf());
                    } else {
                        System.out.println("WebSocketActor received search query: " + message);
                        videoSearchActor.tell(message, getSelf());
                    }
                })
                .match(List.class, videoResults -> {
                    try {
                        System.out.println("WebSocketActor received video search results.");

                        wordStatsActor.tell(new WordStatsActor.VideoSearchResultsMessage(videoResults), getSelf());
                    } catch (Exception e) {
                        System.err.println("Error handling video search response: " + e.getMessage());
                        out.tell("Error: Failed to process video search response", getSelf());
                    }
                })
                .match(Map.class, wordStats -> {
                    try {
                        System.out.println("WebSocketActor received word statistics response.");
                        String jsonResponse = new ObjectMapper().writeValueAsString(wordStats);
                        out.tell(jsonResponse, getSelf());
                    } catch (Exception e) {
                        System.err.println("Error serializing word stats response: " + e.getMessage());
                        out.tell("Error: Failed to serialize word stats response", getSelf());
                    }
                })
                .match(ChannelProfileResult.class, channelProfile -> {
                    try {
                        System.out.println("WebSocketActor received channel profile response.");
                        String jsonResponse = new ObjectMapper().writeValueAsString(channelProfile);
                        out.tell(jsonResponse, getSelf()); // Send JSON response to WebSocket client
                    } catch (Exception e) {
                        System.err.println("Error serializing channel profile response: " + e.getMessage());
                        out.tell("Error: Failed to serialize channel profile response", getSelf());
                    }
                })
                .matchAny(message -> {
                    System.err.println("Unsupported message type in WebSocketActor: " + message);
                    out.tell("Error: Unsupported message type", getSelf());
                })
                .build();
    }
}
