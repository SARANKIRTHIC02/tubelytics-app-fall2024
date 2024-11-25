package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import model.ChannelProfileResult;
import model.TubelyticService;

public class ChannelActor extends AbstractActor {

    public static Props props() {
        return Props.create(ChannelActor.class);
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(String.class, channelId -> {
                    System.out.println("ChannelActor received channelId: " + channelId);
                    try {
                        ChannelProfileResult channelProfile = TubelyticService.fetchChannelDetails(channelId);
                        getSender().tell(channelProfile, getSelf());
                    } catch (Exception e) {
                        System.err.println("Error fetching channel details: " + e.getMessage());
                        getSender().tell(new akka.actor.Status.Failure(e), getSelf());
                    }
                })
                .matchAny(message -> {
                    System.err.println("Unsupported message type in ChannelActor: " + message);
                    getSender().tell(new akka.actor.Status.Failure(
                            new IllegalArgumentException("Unsupported message type")), getSelf());
                })
                .build();
    }
}
