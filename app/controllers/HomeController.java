package controllers;

import model.*;
import play.mvc.*;

import java.util.Optional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The Homecontroller class handles various endpoints related to YouTube analytics, such as video
 * searches, tag and word statistics, and channel details.
 */
public class HomeController extends Controller {

    public SearchResponseList accumulatedResults=new SearchResponseList(new ArrayList<>(),UUID.randomUUID().toString());

    /**
     * Method that handles index.html page
     * @return renders index.html file
     */
    public Result index() {
        return ok(views.html.index.render());
    }

    /**
     * Handles the YouTube analytics page, showing video search results and word statistics based on a query.
     *
     * @param query an optional search query string.
     * @return renders ytlytics.html page.
     */
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

    /**
     * Handles the tag analytics page based on the given query, displaying relevant videos and word statistics.
     *
     * @param query the search query string
     * @return renders taglytics.html page.
     */
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

    /**
     * Retrieves and displays the profile details of a specific YouTube channel.
     *
     * @param channelId the unique ID of the YouTube channel.
     * @return renders channelprofile.html page.
     */
    public Result channelVideos(String channelId) {
        ChannelProfileResult channelProfileInfo = TubelyticService.fetchChannelDetails(channelId);

        if (channelProfileInfo == null) {
            return notFound("Channel not found with ID: " + channelId);
        }

        return ok(views.html.channelprofile.render(channelProfileInfo));
    }

//    /**
//     * Retrieves and displays the tags for a specific YouTube video.
//     *
//     * @param videoID the unique ID of the video
//     * @return a Result rendering the tags for the video, or a 404 error if the video is not found
//     */
//    public Result tags(String videoID){
//        ChannelProfileResult channelProfileInfo = TubelyticService.fetchChannelDetails(videoID);
//
//        if (channelProfileInfo == null) {
//            return notFound("Video not found with ID: " + videoID);
//        }
//        return ok(views.html.channelprofile.render(channelProfileInfo));
//    }

    /**
     * Displays word statistics based on a given search query.
     *
     * @param searchQuery the search query string
     * @return renders the wordStats.html page
     */
    public Result wordStats(String searchQuery) {
        List<VideoSearchResult> newResults = TubelyticService.fetchResults(searchQuery);
        Map<String, Long> wordsFiltered = TubelyticService.wordStatistics(newResults);
        return ok(views.html.wordStats.render(wordsFiltered));
    }

}