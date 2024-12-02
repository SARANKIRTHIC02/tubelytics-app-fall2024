package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import model.TubelyticService;
import model.VideoSearchResult;

import java.util.List;
import java.util.stream.Collectors;
/**
 * TagActor handles requests to fetch video search results based on tags.
 * It listens for a tag and retrieves the corresponding video search results from
 * the TubelyticService. The results are limited to the top 10.
 *
 * @author Durai
 * @author Saran
 */
public class TagActor extends AbstractActor {


    /**
     * Creates a new TagActor.
     *
     * @return the Props for creating the actor
     * @author Durai
     * @author Saran
     */
    public static Props props() {
        return Props.create(TagActor.class);
    }

    /**
     * Defines the behavior of the {@code TagActor} when it receives messages.
     * It fetches video search results for a given tag and returns the top 10 results.
     *
     * @return the behavior of the actor
     * @author Durai
     * @author Saran
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, tag -> {
                    System.out.println("TagActor received tag: " + tag);
                    try {
                        List<VideoSearchResult> tagResults = TubelyticService.fetchResults(tag);
                        List<VideoSearchResult> topResults = tagResults.stream()
                                .limit(10)
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