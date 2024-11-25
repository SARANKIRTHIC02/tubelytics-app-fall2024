package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Map;

public class WebSocketActor extends AbstractActor {
    private final String sessionId;
    private final ActorRef out;
    private final ActorRef videoSearchActor;

    public static Props props(String sessionId, ActorRef out, ActorRef videoSearchActor) {
        return Props.create(WebSocketActor.class, () -> new WebSocketActor(sessionId, out, videoSearchActor));
    }

    public WebSocketActor(String sessionId, ActorRef out, ActorRef videoSearchActor) {
        this.sessionId = sessionId;
        this.out = out;
        this.videoSearchActor = videoSearchActor;
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, query -> {
                    System.out.println("WebSocketActor received query: " + query);

                    // Forward query to VideoSearchActor
                    videoSearchActor.tell(query, getSelf());
                })
                .match(Map.class, response -> {
                    // Convert response to JSON and send to the client
                    String jsonResponse = new ObjectMapper().writeValueAsString(response);
                    out.tell(jsonResponse, getSelf());
                })
                .matchAny(message -> {
                    System.err.println("Unsupported message type in session " + sessionId + ": " + message);
                    out.tell("Error: Unsupported message type", getSelf());
                })
                .build();
    }
}
