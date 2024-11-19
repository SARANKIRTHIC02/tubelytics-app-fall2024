package controllers;

import model.*;
import play.mvc.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * The HomeController class handles various endpoints related to YouTube analytics, such as video
 * searches, tag and word statistics, and channel details, with caching to maintain user history.
 * @author durai
 * @author saran
 * @author sushanth
 */
public class HomeController extends Controller {

    private static final Map<String, SearchResponseList> userHistoryCache = new ConcurrentHashMap<>();

    /**
     * Renders the index page.
     *
     * @param request The HTTP request.
     * @return Rendered index page.
     */
    public CompletionStage<Result> index(Http.Request request) {
        return CompletableFuture.supplyAsync(() -> ok(views.html.index.render()));
    }

    /**
     * Handles YouTube video search based on a query, accumulates the results,
     * and returns the analysis of word statistics and search results.
     *
     * @param request The HTTP request.
     * @param query   The search query as an optional string.
     * @return Renders the YouTube analytics page with search results and word statistics.
     * @author durai
     * @author saran
     */
    public CompletionStage<Result> ytlytics(Http.Request request, Optional<String> query) {
        String searchQuery = query.orElse("");
        String userToken = getUserToken(request);

        return CompletableFuture.supplyAsync(() -> {
            List<VideoSearchResult> newResults = TubelyticService.fetchResults(searchQuery);
            Map<String, Long> wordsFiltered = TubelyticService.wordStatistics(newResults);

            List<VideoSearchResult> limitedResults = newResults.stream()
                    .limit(10)
                    .collect(Collectors.toList());

            SearchResponse model = new SearchResponse(searchQuery, limitedResults);
            SearchResponseList userHistory = userHistoryCache.get(userToken);
            userHistory.getRequestModels().add(0, model);

            return ok(views.html.ytlytics.render(userHistory, wordsFiltered, searchQuery))
                    .addingToSession(request, "userToken", userToken);
        });
    }

    /**
     * Handles YouTube video search based on a query and returns tag statistics for the search results.
     *
     * @param query   The search query.
     * @return Renders the tag analytics page with tag statistics and search results.
     * @author sushanth
     */
    public CompletionStage<Result> taglytics(String query) {
        //String userToken = getUserToken(request);

        return CompletableFuture.supplyAsync(() -> {
            List<VideoSearchResult> newResults = TubelyticService.fetchResults(query);
            Map<String, Long> wordsFiltered = TubelyticService.wordStatistics(newResults);

            List<VideoSearchResult> limitedResults = newResults.stream()
                    .limit(10)
                    .collect(Collectors.toList());

            SearchResponse model = new SearchResponse(query, limitedResults);
            //userHistoryCache.get(userToken).getRequestModels().add(0, model);

            return ok(views.html.taglytics.render(model, wordsFiltered));
        });
    }

    /**
     * Retrieves videos from a specific YouTube channel based on the channel ID and renders the channel profile page.
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
     */
    public CompletionStage<Result> tags(String videoID) {
        return CompletableFuture.supplyAsync(() -> {
            ChannelProfileResult channelProfileInfo = TubelyticService.fetchChannelDetails(videoID);

            if (channelProfileInfo == null) {
                return notFound("Video not found with ID: " + videoID);
            }

            return ok(views.html.channelprofile.render(channelProfileInfo));
        });
    }

    /**
     * Analyzes word statistics based on a search query and renders the word statistics page.
     * @param searchQuery The search query for which word statistics will be generated.
     * @return Renders the word statistics page with analyzed word data.
     * @author saran
     */
    public CompletionStage<Result> wordStats(String searchQuery) {
        return CompletableFuture.supplyAsync(() -> {
            List<VideoSearchResult> newResults = TubelyticService.fetchResults(searchQuery);
            Map<String, Long> wordsFiltered = TubelyticService.wordStatistics(newResults);

            return ok(views.html.wordStats.render(wordsFiltered));
        });
    }

    /**
     * Helper method to get or initialize the user token from the session.
     *
     * @param request The HTTP request.
     * @return The user token.
     * @author durai
     */
    private String getUserToken(Http.Request request) {
        String userToken = request.session().getOptional("userToken").orElse(null);
        if (userToken == null) {
            userToken = UUID.randomUUID().toString();
            userHistoryCache.put(userToken, new SearchResponseList(new ArrayList<>(), UUID.randomUUID().toString()));
        }
        return userToken;
    }
}
