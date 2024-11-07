package model;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.*;
import java.util.stream.Collectors;

public class TubelyticService {
    public static  List<VideoSearchResult> fetchResults(String query) {
        query= URLEncoder.encode(query, StandardCharsets.UTF_8);
        List<VideoSearchResult> results= new ArrayList<>();

        if (!query.isEmpty()) {
            try {
                System.out.println("Line 49");
                results= YouTubeService.searchVideosBasedOnQuery(query);
            } catch (IOException | InterruptedException e) {
                throw new RuntimeException(e);
            }
        } else {
            results=Collections.emptyList();
        }

        return results;
    }

    public static ChannelProfileResult fetchChannelDetails(String channelID){
        try {
            ChannelProfileResult channelProfile = YouTubeService.getChannelProfile(channelID);
            return channelProfile;
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static Map<String, Long> wordStatistics(List<VideoSearchResult> results) {
        List<String> allWords = results.stream()
                .map(VideoSearchResult::getDescription)
                .flatMap(description -> Arrays.stream(description.split("\\W+")))
                .map(String::toLowerCase)
                .filter(word -> !word.isEmpty())
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



