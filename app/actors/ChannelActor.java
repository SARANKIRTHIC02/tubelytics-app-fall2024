package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import model.ChannelProfileResult;
import model.TubelyticService;

/**
 * ChannelActor handles requests to fetch details for a YouTube channel.
 * using the TubelyticService.
 *
 * @author Durai
 * @author Saran
 */
public class ChannelActor extends AbstractActor {

    /**
     * Creates a new ChannelActor.
     *
     * @return the Props for creating the actor
     * @author Durai
     * @author Saran
     */
    public static Props props() {
        return Props.create(ChannelActor.class);
    }

    /**
     * Defines the behavior of the {@code ChannelActor} when it receives messages.
     *
     * @return the behavior of the actor
     * @author Durai
     * @author Saran
     */
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
