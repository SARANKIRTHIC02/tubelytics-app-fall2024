package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import model.TubelyticService;
import model.VideoSearchResult;

import java.util.List;
import java.util.stream.Collectors;

public class TagActor extends AbstractActor {
    public static Props props() {
        return Props.create(TagActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, tag -> {
                    System.out.println("TagActor received tag: " + tag);
                    try {
                        List<VideoSearchResult> tagResults = TubelyticService.fetchResults(tag);
                        // Limit the results to the top 10
                        List<VideoSearchResult> topResults = tagResults.stream()
                                .limit(10) // Take only the first 10 results
                                .collect(Collectors.toList());

                        getSender().tell(topResults, getSelf());
                    } catch (Exception e) {
                        System.err.println("Error fetching tag results: " + e.getMessage());
                        getSender().tell(new akka.actor.Status.Failure(e), getSelf());
                    }
                })
                .matchAny(message -> {
                    System.err.println("Unsupported message type in TagActor: " + message);
                    getSender().tell(new akka.actor.Status.Failure(new IllegalArgumentException("Unsupported message type")), getSelf());
                })
                .build();
    }
}