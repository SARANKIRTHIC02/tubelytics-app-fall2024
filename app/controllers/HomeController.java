package controllers;

import actors.ChannelActor;
import actors.VideoSearchActor;
import actors.WebSocketActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.stream.Materializer;
import model.ChannelProfileResult;
import model.SearchResponseList;

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
    public WebSocket ytlyticsWebSocket() {
        return WebSocket.Text.accept(request -> {
            String sessionId = UUID.randomUUID().toString();
            System.out.println("New WebSocket session created: " + sessionId);
            ActorRef videoSearchActor = actorSystem.actorOf(VideoSearchActor.props(), "videoSearchActor-" + sessionId);
            ActorRef channelActor = actorSystem.actorOf(ChannelActor.props(), "channelActor-" + sessionId);
            return ActorFlow.actorRef(out -> WebSocketActor.props(sessionId, out, videoSearchActor, channelActor), actorSystem, materializer);
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
}
