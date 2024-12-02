package actors;

import akka.actor.AbstractActor;
import akka.actor.ActorRef;
import akka.actor.Props;
import akka.stream.Materializer;
import akka.stream.OverflowStrategy;
import akka.stream.javadsl.Source;
import akka.stream.javadsl.Sink;
import akka.stream.javadsl.SourceQueueWithComplete;
import akka.stream.javadsl.Keep;
import model.TubelyticService;
import model.VideoSearchResult;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * VideoSearchActor is responsible for handling video search queries and streaming
 * the search results. It maintains a stream of results for each active search query.
 * Results are fetched periodically and streamed to the client.
 *
 * @author Durai
 * @author Saran
 */
public class VideoSearchActor extends AbstractActor {
    private final Map<String, SourceQueueWithComplete<List<VideoSearchResult>>> activeQueries = new HashMap<>();
    private final Materializer materializer;

    /**
     * Creates a new VideoSearchActor.
     *
     * @param materializer the materializer to be used for Akka streams
     * @return the {@code Props} for creating the actor
     * @author Durai
     * @author Saran
     */
    public static Props props(Materializer materializer) {
        return Props.create(VideoSearchActor.class, () -> new VideoSearchActor(materializer));
    }

    /**
     * Initializes the VideoSearchActor with the provided materializer.
     *
     * The materializer to be used for Akka streams
     * @author Durai
     * @author Saran
     */
    public VideoSearchActor(Materializer materializer) {
        this.materializer = materializer;
    }

    /**
     * Defines the behavior of the VideoSearchActor when it receives messages.
     * It starts a new stream for a search query if not already active and streams the results.
     *
     * @return the behavior of the actor
     * @author Durai
     * @author Saran
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(SearchQuery.class, query -> {
                    System.out.println("[VideoSearchActor] Received query: " + query.query);
                    if (!activeQueries.containsKey(query.query)) {
                        System.out.println("[VideoSearchActor] Starting new stream for query: " + query.query);
                        startStream(query.query, query.sender);
                    }
                })
                .matchAny(message -> {
                    System.err.println("[VideoSearchActor] Unsupported message type: " + message);
                    sender().tell(new akka.actor.Status.Failure(new IllegalArgumentException("Unsupported message type")), self());
                })
                .build();
    }

    /**
     * Starts a stream for a given search query. The stream will periodically fetch
     * results and send them to the client.
     *
     * @param query  the search query to stream results for
     * @param sender the actor to which the results will be sent
     * @author Durai
     * @author Saran
     */
    private void startStream(String query, ActorRef sender) {
        SourceQueueWithComplete<List<VideoSearchResult>> queue =
                Source.<List<VideoSearchResult>>queue(10, OverflowStrategy.backpressure())
                        .map(results -> {
                            System.out.println("[VideoSearchActor] Streaming results for query: " + query );
                            return results;
                        })
                        .toMat(Sink.foreach(results -> {
                            sender.tell(Map.of("query", query, "results", results), self());
                        }), Keep.left())
                        .run(materializer);

        activeQueries.put(query, queue);


        getContext().getSystem().scheduler().scheduleAtFixedRate(
                scala.concurrent.duration.Duration.Zero(),
                scala.concurrent.duration.Duration.create(40, "seconds"),
                () -> fetchResults(query, queue, sender),
                getContext().dispatcher()
        );
    }

    /**
     * Fetches results for a given search query and offers them to the stream.
     * Results are sent to the client through the code sender actor.
     *
     * @param query  the search query to fetch results for
     * @param queue  the queue used to stream results
     * @param sender the actor to which the results will be sent
     * @author Durai
     * @author Saran
     */
    private void fetchResults(String query, SourceQueueWithComplete<List<VideoSearchResult>> queue, ActorRef sender) {
        CompletableFuture.supplyAsync(() -> TubelyticService.fetchResults(query))
                .thenAccept(results -> {
                    List<VideoSearchResult> limitedResults = results.size() > 10 ? results.subList(0, 10) : results;
                    System.out.println("[VideoSearchActor] Fetched results for query: " + query);

                    // Offer results to the queue for streaming
                    queue.offer(limitedResults);

                    // Send results explicitly to the WebSocketActor
                    sender.tell(Map.of("query", query, "results", limitedResults), self());
                    System.out.println("[VideoSearchActor] Sent results to WebSocketActor for query: " + query);
                })
                .exceptionally(e -> {
                    System.err.println("[VideoSearchActor] Error fetching results for query: " + query + " - " + e.getMessage());
                    return null;
                });
    }

    /**
     * Cleans up any active streams and resources when the actor stops.
     *
     * @author Durai
     * @author Saran
     */
    @Override
    public void postStop() {
        activeQueries.values().forEach(SourceQueueWithComplete::complete);
        activeQueries.clear();
        System.out.println("[VideoSearchActor] Stopped and cleared active streams.");
    }


    /**
     * Represents a search query and the actor to which results should be sent.
     * @author Durai
     * @author Saran
     */
    public static class SearchQuery {
        public final String query;
        public final ActorRef sender;

        /**
         * Creates a new SearchQuery.
         *
         * @param query  the search query
         * @param sender the actor to send results to
         * @author Durai
         * @author Saran
         */
        public SearchQuery(String query, ActorRef sender) {
            this.query = query;
            this.sender = sender;
        }
    }
}
