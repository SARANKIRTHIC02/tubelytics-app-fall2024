package controllers;

import model.*;
import play.mvc.*;

import java.util.Optional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.actor.ActorRef;
import akka.stream.Materializer;
import play.mvc.WebSocket;

import javax.inject.Inject;
import play.libs.streams.ActorFlow;



import actors.WebSocketActor;

/**
 *
 * The Homecontroller class handles various endpoints related to YouTube analytics, such as video
 * searches, tag and word statistics, and channel details.
 *
 * @author saran
 * @author durai
 * @author sushanth
 */
public class HomeController extends Controller {
    private final ActorSystem actorSystem;
    private final Materializer materializer;

    @Inject
    public HomeController(ActorSystem actorSystem, Materializer materializer) {
        this.actorSystem = actorSystem;
        this.materializer = materializer;
    }

    public WebSocket ytlyticsWebSocket() {
        return WebSocket.Text.accept(request -> {
            // Create an Akka actor fActoror each WebSocket connection
            ActorRef webSocketActor = actorSystem.actorOf(Props.create(WebSocketActor.class));
            return WebSocket.ActorFlow.actorRef(out -> Props.create(WebSocketActor.class, out));
        });
    }


    public SearchResponseList accumulatedResults=new SearchResponseList(new ArrayList<>(),UUID.randomUUID().toString());

    /**
     * Renders the index page.
     *
     * @return Rendered index page.
     */
    public CompletionStage<Result> index() {
        return CompletableFuture.supplyAsync(() -> ok(views.html.index.render()));
    }

    /**
     *
     * Handles YouTube video search based on a query, accumulates the results,
     * and returns the analysis of word statistics and search results.
     * @param query The search query as an optional string.
     * @return Renders the YouTube analytics page with search results and word statistics.
     *  @author saran
     *  @author durai
     */

    public CompletionStage<Result> ytlytics(Optional<String> query) {
        System.out.println("Received query: " + query.orElse("none"));
        String searchQuery = query.orElse("");

        return CompletableFuture.supplyAsync(() -> {
            List<VideoSearchResult> newResults = TubelyticService.fetchResults(searchQuery);
            Map<String, Long> wordsFiltered = TubelyticService.wordStatistics(newResults);
            //System.out.println(wordsFiltered);
            List<VideoSearchResult> limitedResults = newResults.stream()
                    .limit(10)
                    .collect(Collectors.toList());

            SearchResponse model = new SearchResponse(searchQuery, limitedResults);
            accumulatedResults.getRequestModels().add(0, model);
            return ok(views.html.ytlytics.render(accumulatedResults, wordsFiltered, searchQuery));
        });
    }

    /**
     *
     * Handles YouTube video search based on a query and returns tag statistics for the search results.
     * @param query The search query.
     * @return Renders the tag analytics page with tag statistics and search results.
     * @author durai
     * @author Saran
     * @author sushanth
     */
    public CompletionStage<Result> taglytics(String query) {

        return CompletableFuture.supplyAsync(() -> {
            List<VideoSearchResult> newResults = TubelyticService.fetchResults(query);
            Map<String, Long> wordsFiltered = TubelyticService.wordStatistics(newResults);
            //System.out.println(wordsFiltered);
            List<VideoSearchResult> limitedResults = newResults.stream()
                    .limit(10)
                    .collect(Collectors.toList());

            SearchResponse model = new SearchResponse(query, limitedResults);
            return ok(views.html.taglytics.render(model, wordsFiltered));
        });
    }

    /**
     * Retrieves videos from a specific YouTube channel based on the channel ID and renders the channel profile page.
     *
     * @param channelId The unique identifier for the YouTube channel.
     * @return A completion stage that renders the channel profile page with channel details.
     * @author durai
     */
    public CompletionStage<Result> channelVideos(String channelId) {
        return CompletableFuture.supplyAsync(() -> {
            ChannelProfileResult channelProfileInfo = TubelyticService.fetchChannelDetails(channelId);

            if (channelProfileInfo == null) {
                return notFound("Channel not found with ID: " + channelId);
            }

            return ok(views.html.channelprofile.render(channelProfileInfo));
        });
    }
    /**
     * Fetches tags for a specific video based on the video ID and renders the channel profile page.
     *
     * @param videoID The unique identifier for the YouTube video.
     * @return Renders the channel profile page.
     * @author sushanth
     * @author durai
     */
    public CompletionStage<Result> tags(String videoID){
        return CompletableFuture.supplyAsync(() -> {
            ChannelProfileResult channelProfileInfo = TubelyticService.fetchChannelDetails(videoID);

            if (channelProfileInfo == null) {
                return notFound("Video not found with ID: " + videoID);
            }
            return ok(views.html.channelprofile.render(channelProfileInfo));
        });
    }
    /**
     *
     * Analyzes word statistics based on a search query and renders the word statistics page.
     * @param searchQuery The search query for which word statistics will be generated.
     * @return Renders the word statistics page with analyzed word data.
     * @author durai
     * @author saran
     */
    public CompletionStage<Result> wordStats(String searchQuery) {
        return CompletableFuture.supplyAsync(() -> {
            List<VideoSearchResult> newResults = TubelyticService.fetchResults(searchQuery);
            Map<String, Long> wordsFiltered = TubelyticService.wordStatistics(newResults);
            return ok(views.html.wordStats.render(wordsFiltered));});
    }
}