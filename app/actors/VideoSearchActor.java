package actors;

import akka.actor.Props;
import akka.actor.AbstractActor;
import model.TubelyticService;
import model.VideoSearchResult;

import java.util.*;

public class VideoSearchActor extends AbstractActor {
    private final Map<String, List<VideoSearchResult>> userSearchHistory = new LinkedHashMap<>();

    public static Props props() {
        return Props.create(VideoSearchActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, query -> {
                    System.out.println("Processing query: " + query);

                    try {
                        // Fetch results for the query
                        List<VideoSearchResult> results = TubelyticService.fetchResults(query);

                        // Limit results to 10 videos
                        List<VideoSearchResult> limitedResults = results.size() > 10 ? results.subList(0, 10) : results;

                        // Update search history for the query
                        userSearchHistory.put(query, limitedResults);

                        // Prepare response to send back
                        Map<String, Object> response = new LinkedHashMap<>();
                        response.put("query", query);
                        response.put("results", limitedResults);
                        System.out.println("Line 37 inside videoActor: " + query);

                        // Send updated results to WebSocketActor
                        getSender().tell(response, getSelf());
                    } catch (Exception e) {
                        System.err.println("Error processing query: " + e.getMessage());
                        getSender().tell(new akka.actor.Status.Failure(e), getSelf());
                    }
                })
                .matchAny(message -> {
                    System.err.println("Unsupported message type: " + message);
                    getSender().tell(new akka.actor.Status.Failure(new IllegalArgumentException("Unsupported message type")), getSelf());
                })
                .build();
    }
}
