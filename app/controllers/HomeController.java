package controllers;

import com.google.inject.Inject;
import model.*;
import play.mvc.*;

import java.util.Optional;

import java.util.*;
import java.util.stream.Collectors;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class HomeController extends Controller {

    public SearchResponseList accumulatedResults=new SearchResponseList(new ArrayList<>(),UUID.randomUUID().toString());

    public CompletionStage<Result> index() {
        return CompletableFuture.supplyAsync(() -> ok(views.html.index.render()));
    }

    public CompletionStage<Result> ytlytics(Optional<String> query) {

        System.out.println("Received query: " + query.orElse("none"));
        String searchQuery = query.orElse("");

        return CompletableFuture.supplyAsync(() -> {
            List<VideoSearchResult> newResults = TubelyticService.fetchResults(searchQuery);
            Map<String, Long> wordsFiltered = TubelyticService.wordStatistics(newResults);
            System.out.println(wordsFiltered);
            List<VideoSearchResult> limitedResults = newResults.stream()
                    .limit(10)
                    .collect(Collectors.toList());

            SearchResponse model = new SearchResponse(searchQuery, limitedResults);
            accumulatedResults.getRequestModels().add(0, model);
            return ok(views.html.ytlytics.render(accumulatedResults, wordsFiltered, searchQuery));
        });
    }

    public CompletionStage<Result> taglytics(String query) {

        return CompletableFuture.supplyAsync(() -> {
            List<VideoSearchResult> newResults = TubelyticService.fetchResults(query);
            Map<String, Long> wordsFiltered = TubelyticService.wordStatistics(newResults);
            System.out.println(wordsFiltered);
            List<VideoSearchResult> limitedResults = newResults.stream()
                    .limit(10)
                    .collect(Collectors.toList());

            SearchResponse model = new SearchResponse(query, limitedResults);
            return ok(views.html.taglytics.render(model, wordsFiltered));
        });
    }

    public CompletionStage<Result> channelVideos(String channelId) {
        return CompletableFuture.supplyAsync(() -> {
        ChannelProfileResult channelProfileInfo = TubelyticService.fetchChannelDetails(channelId);

        if (channelProfileInfo == null) {
            return notFound("Channel not found with ID: " + channelId);
        }

        return ok(views.html.channelprofile.render(channelProfileInfo));
        });
    }

    public CompletionStage<Result> tags(String videoID){
        return CompletableFuture.supplyAsync(() -> {
        ChannelProfileResult channelProfileInfo = TubelyticService.fetchChannelDetails(videoID);

        if (channelProfileInfo == null) {
            return notFound("Video not found with ID: " + videoID);
        }
        return ok(views.html.channelprofile.render(channelProfileInfo));
        });
    }

    public CompletionStage<Result> wordStats(String searchQuery) {
        return CompletableFuture.supplyAsync(() -> {
        List<VideoSearchResult> newResults = TubelyticService.fetchResults(searchQuery);
        Map<String, Long> wordsFiltered = TubelyticService.wordStatistics(newResults);
        return ok(views.html.wordStats.render(wordsFiltered));});
    }

}