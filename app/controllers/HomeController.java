package controllers;

import actors.*;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.pattern.Patterns;
import akka.stream.Materializer;
import akka.util.Timeout;
import model.ChannelProfileResult;
import model.SearchResponseList;

import model.TubelyticService;
import model.VideoSearchResult;
import play.libs.streams.ActorFlow;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;
import scala.compat.java8.FutureConverters;

import javax.inject.Inject;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * The HomeController handles HTTP requests related to video search and analytics.
 * It manages WebSocket connections and provides endpoints for video search results, channel profiles,
 * word statistics, and tag-based analytics.
 *
 * @author Durai
 * @author Saran
 */
public class HomeController extends Controller {

    private final ActorSystem actorSystem;
    private final Materializer materializer;

    /**
     * Constructs a HomeController instance.
     *
     * @param actorSystem the ActorSystem for creating and managing actors
     * @param materializer the Materializer for managing Akka streams
     * @author Durai
     * @author Saran
     */
    @Inject
    public HomeController(ActorSystem actorSystem, Materializer materializer) {
        this.actorSystem = actorSystem;
        this.materializer = materializer;
    }

    /**
     * Establishes a WebSocket connection for the video search and analytics service.
     * It creates actors to handle video search, channel profiles, word statistics, and tag analytics.
     *
     * @return the WebSocket for the client-server communication
     * @author Durai
     * @author Saran
     */
    public WebSocket ytlyticsWebSocket() {
        return WebSocket.Text.accept(request -> {
            String sessionId = UUID.randomUUID().toString();
            System.out.println("New WebSocket session created: " + sessionId);

            ActorRef videoSearchActor = actorSystem.actorOf(VideoSearchActor.props(materializer), "videoSearchActor-" + sessionId);
            ActorRef channelActor = actorSystem.actorOf(ChannelActor.props(), "channelActor-" + sessionId);
            ActorRef wordStatsActor = actorSystem.actorOf(WordStatsActor.props(), "wordStatsActor-" + sessionId);
            ActorRef tagActor = actorSystem.actorOf(TagActor.props(), "tagActor-" + sessionId);



            return ActorFlow.actorRef(out -> WebSocketActor.props(sessionId, out, videoSearchActor, channelActor, wordStatsActor,tagActor), actorSystem, materializer);
        });
    }

    /**
     * Displays the initial page for the ytlytics video search and analytics service.
     *
     * @return the result to render the ytlytics page
     * @author Durai
     * @author Saran
     */
    public CompletionStage<Result> ytlytics() {
        System.out.println("ytlytics line 55");
        SearchResponseList accumulatedResults = new SearchResponseList(new ArrayList<>(), UUID.randomUUID().toString());
        Map<String, Long> wordsFiltered = new HashMap<>();
        String searchQuery = "";

        return CompletableFuture.completedStage(
                ok(views.html.ytlytics.render(accumulatedResults, wordsFiltered, searchQuery))
        );
    }

    /**
     * Displays the profile of a YouTube channel.
     *
     * @param id the ID of the channel to display
     * @return the result to render the channel profile page
     * @author Durai
     * @author Saran
     */
    public CompletionStage<Result> channelProfile(String id) {
        System.out.println("Channel Profile....");
        return CompletableFuture.completedStage(
                ok(views.html.channel.render(new ChannelProfileResult(), id))
        );
    }

    /**
     * Processes and displays word statistics for a given search query.
     *
     * @param query the search query to process for word statistics
     * @param sessionId the session ID for the WebSocket connection
     * @return the result to render the word statistics page
     * @throws IOException if an I/O error occurs while fetching results
     * @throws InterruptedException if the process is interrupted
     * @author Durai
     * @author Saran
     */
    public CompletionStage<Result> wordStats(String query, String sessionId) throws IOException, InterruptedException {
        ActorRef wordStatsActor = actorSystem.actorOf(WordStatsActor.props(), "wordStatsActor-" + sessionId);
        List<VideoSearchResult> videoResults = TubelyticService.fetchResults(query);

        CompletionStage<Object> wordStatsFuture = FutureConverters.toJava(
                Patterns.ask(
                        wordStatsActor,
                        new WordStatsActor.VideoSearchResultsMessage(videoResults),
                        Timeout.create(Duration.ofSeconds(5))
                )
        );

        return wordStatsFuture.thenApply(response -> {
            if (response instanceof Map<?, ?>) {
                @SuppressWarnings("unchecked")
                Map<String, Long> wordStats = (Map<String, Long>) response;

                actorSystem.stop(wordStatsActor);

                return ok(views.html.wordStats.render(wordStats));
            } else {
                return internalServerError("Unexpected response type: " + response.getClass());
            }
        }).exceptionally(ex -> {
            return internalServerError("Failed to process word statistics: " + ex.getMessage());
        });
    }

    /**
     * Displays the tag-based analytics page for a given query.
     *
     * @param query the search query to process for tag-based analytics
     * @return the result to render the taglytics page
     * @author Durai
     * @author Saran
     */
    public CompletionStage<Result> taglytics(String query) {
        return CompletableFuture.completedStage(
                ok(views.html.taglytics.render(new SearchResponseList(), query))
        );
    }
}