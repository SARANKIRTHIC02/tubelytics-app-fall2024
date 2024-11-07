package controllers;

import model.*;
import play.mvc.*;

import java.util.Optional;

import java.util.*;
import java.util.stream.Collectors;

public class HomeController extends Controller {

    public SearchResponseList accumulatedResults=new SearchResponseList(new ArrayList<>(),UUID.randomUUID().toString());

    public Result index() {
        return ok(views.html.index.render());
    }

    public Result ytlytics(Optional<String> query) {

        System.out.println("Received query: " + query.orElse("none"));
        String searchQuery = query.orElse("");

        List<VideoSearchResult> newResults = TubelyticService.fetchResults(searchQuery);
        Map<String, Long> wordsFiltered = TubelyticService.wordStatistics(newResults);
        System.out.println(wordsFiltered);
        List<VideoSearchResult> limitedResults = newResults.stream()
                .limit(10)
                .collect(Collectors.toList());

        SearchResponse model = new SearchResponse(searchQuery, limitedResults);
        accumulatedResults.getRequestModels().add(0,model);
        return ok(views.html.ytlytics.render(accumulatedResults, wordsFiltered, searchQuery));
    }

    public Result taglytics(String query) {

        List<VideoSearchResult> newResults = TubelyticService.fetchResults(query);
        Map<String, Long> wordsFiltered = TubelyticService.wordStatistics(newResults);
        System.out.println(wordsFiltered);
        List<VideoSearchResult> limitedResults = newResults.stream()
                .limit(10)
                .collect(Collectors.toList());

        SearchResponse model = new SearchResponse(query, limitedResults);
        return ok(views.html.taglytics.render(model,wordsFiltered));
    }

    public Result channelVideos(String channelId) {
        ChannelProfileResult channelProfileInfo = TubelyticService.fetchChannelDetails(channelId);

        if (channelProfileInfo == null) {
            return notFound("Channel not found with ID: " + channelId);
        }

        return ok(views.html.channelprofile.render(channelProfileInfo));
    }

    public Result tags(String videoID){
        ChannelProfileResult channelProfileInfo = TubelyticService.fetchChannelDetails(videoID);

        if (channelProfileInfo == null) {
            return notFound("Video not found with ID: " + videoID);
        }
        return ok(views.html.channelprofile.render(channelProfileInfo));
    }

    public Result wordStats(String searchQuery) {
        List<VideoSearchResult> newResults = TubelyticService.fetchResults(searchQuery);
        Map<String, Long> wordsFiltered = TubelyticService.wordStatistics(newResults);
        return ok(views.html.wordStats.render(wordsFiltered));
    }

}