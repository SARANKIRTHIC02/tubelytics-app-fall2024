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
            String sessionId = UUID.randomUUID().toString();
            System.out.println("New WebSocket session created: " + sessionId);

            ActorRef videoSearchActor = actorSystem.actorOf(VideoSearchActor.props(materializer), "videoSearchActor-" + sessionId);
            ActorRef channelActor = actorSystem.actorOf(ChannelActor.props(), "channelActor-" + sessionId);
            ActorRef wordStatsActor = actorSystem.actorOf(WordStatsActor.props(), "wordStatsActor-" + sessionId);
            ActorRef tagActor = actorSystem.actorOf(TagActor.props(), "tagActor-" + sessionId);



            return ActorFlow.actorRef(out -> WebSocketActor.props(sessionId, out, videoSearchActor, channelActor, wordStatsActor,tagActor), actorSystem, materializer);
        });
    }
    public CompletionStage<Result> ytlytics() {
        System.out.println("ytlytics line 55");
        SearchResponseList accumulatedResults = new SearchResponseList(new ArrayList<>(), UUID.randomUUID().toString());
        Map<String, Long> wordsFiltered = new HashMap<>();
        String searchQuery = "";

        return CompletableFuture.completedStage(
                ok(views.html.ytlytics.render(accumulatedResults, wordsFiltered, searchQuery))
        );
    }

    public CompletionStage<Result> channelProfile(String id) {
        System.out.println("Channel Profile....");
        return CompletableFuture.completedStage(
                ok(views.html.channel.render(new ChannelProfileResult(), id))
        );
    }

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

    public CompletionStage<Result> taglytics(String query) {
        return CompletableFuture.completedStage(
                ok(views.html.taglytics.render(new SearchResponseList(), query))
        );
    }
}