package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import model.TubelyticService;
import model.VideoSearchResult;

import java.util.List;

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
                        getSender().tell(tagResults, getSelf());
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