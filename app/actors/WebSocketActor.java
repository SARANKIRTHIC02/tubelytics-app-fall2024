package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.stream.javadsl.Source;
import model.VideoSearchResult;
import model.TubelyticService;

import java.util.HashSet;
import java.util.Set;

public class WebSocketActor extends AbstractActor {

    private final ActorRef out; // Connection with the WebSocket client
    private String currentQuery = ""; // The current search query
    private final Set<String> seenResults = new HashSet<>(); // Used to filter duplicates

    public WebSocketActor(ActorRef out) {
        this.out = out;
    }

    public static Props props(ActorRef out) {
        return Props.create(WebSocketActor.class, out);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, this::handleSearchQuery)
                .build();
    }

    private void handleSearchQuery(String query) {
        currentQuery = query;
        seenResults.clear();

        // Fetch the initial batch of results and send them to the client
        TubelyticService.fetchResults(query).stream()
                .filter(result -> seenResults.add(result.getVideoId())) // Filter duplicates
                .forEach(result -> out.tell(result.toString(), self()));

        // Start streaming new results reactively
        Source<VideoSearchResult, ?> videoStream = TubelyticService.streamResults(query);
        videoStream.runForeach(result -> {
            if (seenResults.add(result.getVideoId())) {
                out.tell(result.toString(), self());
            }
        }, context().system());
    }

    @Override
    public void postStop() throws Exception {
        // Cleanup resources if necessary
        super.postStop();
    }
}

