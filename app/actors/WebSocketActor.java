package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.ObjectMapper;
import model.ChannelProfileResult;

import java.util.Map;

public class WebSocketActor extends AbstractActor {
    private final String sessionId;
    private final ActorRef out;
    private final ActorRef videoSearchActor;
    private final ActorRef channelActor;

    public static Props props(String sessionId, ActorRef out, ActorRef videoSearchActor, ActorRef channelActor) {
        return Props.create(WebSocketActor.class, () -> new WebSocketActor(sessionId, out, videoSearchActor, channelActor));
    }

    public WebSocketActor(String sessionId, ActorRef out, ActorRef videoSearchActor, ActorRef channelActor) {
        this.sessionId = sessionId;
        this.out = out;
        this.videoSearchActor = videoSearchActor;
        this.channelActor = channelActor;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, message -> {
                    if (message.startsWith("channel:")) {
                        String channelId = message.substring("channel:".length());
                        System.out.println("WebSocketActor received channel request: " + channelId);

                        // Forward channel request to ChannelActor
                        channelActor.tell(channelId, getSelf());
                    } else {
                        System.out.println("WebSocketActor received search query: " + message);

                        // Forward search query to VideoSearchActor
                        videoSearchActor.tell(message, getSelf());
                    }
                })
                // Handle video search response
                .match(Map.class, response -> {
                    System.out.println("WebSocketActor received video search response.");
                    String jsonResponse = new ObjectMapper().writeValueAsString(response);
                    out.tell(jsonResponse, getSelf());
                })
                // Handle channel profile response
                .match(ChannelProfileResult.class, channelProfile -> {
                    System.out.println("WebSocketActor received channel profile response.");
                    String jsonResponse = new ObjectMapper().writeValueAsString(channelProfile);
                    out.tell(jsonResponse, getSelf());
                })
                // Handle unsupported messages
                .matchAny(message -> {
                    System.err.println("Unsupported message type in WebSocketActor: " + message);
                    out.tell("Error: Unsupported message type", getSelf());
                })
                .build();
    }
}
