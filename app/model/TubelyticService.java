package model;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.*;
import java.util.stream.Collectors;

import java.time.Duration;
import akka.stream.javadsl.Source;
import model.VideoSearchResult;

/**
 *
 * The TubelyticService class provides methods to fetch YouTube video results and channel details,
 * as well as generate word frequency statistics from video descriptions.
 * @author durai
 */
public class TubelyticService {
    private TubelyticService(){}
    /**
     *
     * Fetches a list of video search results for a given query.
     * Encodes the query using UTF-8 encoding and uses the YouTubeService to retrieve results.
     * If the query is empty, an empty list is returned.
     *
     * @param query The search term to look up videos.
     * @return A list of VideoSearchResult objects matching the query.
     * @author durai
     */

    public static Source<VideoSearchResult, ?> streamResults(String query) {
        return Source.tick(Duration.ZERO, Duration.ofSeconds(2), query) // Tick every 2 seconds
                .mapConcat(TubelyticService::fetchResults); // Fetch new results
    }


    public static  List<VideoSearchResult> fetchResults(String query) {
        query= URLEncoder.encode(query, StandardCharsets.UTF_8);
        List<VideoSearchResult> results= new ArrayList<>();

        if (!query.isEmpty()) {
            try {
                results= YouTubeService.searchVideosBasedOnQuery(query);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            results=Collections.emptyList();
        }

        return results;
    }

    /**
     *
     * Fetches the profile details of a YouTube channel by its ID.
     * Uses the YouTubeService to retrieve channel information.
     *
     * @param channelID The unique ID of the YouTube channel.
     * @return A ChannelProfileResult object containing channel details, or null if the channel is not found.
     * @author durai
     */

    public static ChannelProfileResult fetchChannelDetails(String channelID){
        try {
            ChannelProfileResult channelProfile = YouTubeService.getChannelProfile(channelID);
            return channelProfile;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Generates a word frequency map based on the descriptions of a list of video search results.
     * Words are split by non-word characters, converted to lowercase, and counted.
     * The resulting map is sorted in descending order of frequency.
     *
     * @param results A list of VideoSearchResult objects from which to generate word statistics.
     * @return A map where keys are words and values are their frequencies, sorted by frequency in descending order.
     * @author saran
     *
     */
    public static Map<String, Long> wordStatistics(List<VideoSearchResult> results) {
        List<String> allWords = results.stream()
                .map(VideoSearchResult::getDescription)
                .flatMap(description -> Arrays.stream(description.split("\\W+")))
                .map(String::toLowerCase)
                .filter(word -> !word.isEmpty() && word.matches(".*[a-zA-Z0-9].*") &&
                        (word.length() > 1 || word.equals("i") || word.equals("a")))
                .collect(Collectors.toList());

        Map<String, Long> wordFrequency = allWords.stream()
                .collect(Collectors.groupingBy(word -> word, Collectors.counting()));

        return wordFrequency.entrySet()
                .stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));
    }
}



