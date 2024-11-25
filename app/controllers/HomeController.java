package controllers;

import actors.ChannelActor;
import actors.VideoSearchActor;
import actors.WebSocketActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.stream.Materializer;
import model.ChannelProfileResult;
import model.SearchResponseList;

import model.TubelyticService;
import play.libs.streams.ActorFlow;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.WebSocket;

import javax.inject.Inject;
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

    // WebSocket endpoint with session management
    public WebSocket ytlyticsWebSocket() {
        return WebSocket.Text.accept(request -> {
            // Generate a unique session ID for each connection
            String sessionId = UUID.randomUUID().toString();
            System.out.println("New WebSocket session created: " + sessionId);

            // Create actor instances for the session
            ActorRef videoSearchActor = actorSystem.actorOf(VideoSearchActor.props(), "videoSearchActor-" + sessionId);


            // Create the WebSocketActor with the session ID
            return ActorFlow.actorRef(out -> WebSocketActor.props(sessionId, out, videoSearchActor), actorSystem, materializer);
        });
    }

    // Render the ytlytics page
    public CompletionStage<Result> ytlytics() {
        System.out.println("ytlytics line 55");
        SearchResponseList accumulatedResults = new SearchResponseList(new ArrayList<>(), UUID.randomUUID().toString());
        Map<String, Long> wordsFiltered = new HashMap<>();
        String searchQuery = "";

        return CompletableFuture.completedStage(
                ok(views.html.ytlytics.render(accumulatedResults, wordsFiltered, searchQuery))
        );
    }

    // Render the channel profile page
    public CompletionStage<Result> channelProfile(String id) {
        // Initialize a placeholder or empty ChannelProfileResult
        ChannelProfileResult placeholderProfile = new ChannelProfileResult();
        // Return a completed future to render the view
        return CompletableFuture.completedStage(
                ok(views.html.channel.render(placeholderProfile, id))
        );
    }
}
