package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.Status;
import model.VideoSearchResult;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class WordStatsActor extends AbstractActor {

    public static Props props() {
        return Props.create(WordStatsActor.class);
    }

    public static class VideoSearchResultsMessage {
        private final List<VideoSearchResult> videoResults;

        public VideoSearchResultsMessage(List<VideoSearchResult> videoResults) {
            this.videoResults = videoResults;
        }

        public List<VideoSearchResult> getVideoResults() {
            return videoResults;
        }
    }

    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(VideoSearchResultsMessage.class, msg -> processVideoResults(msg.getVideoResults()))
                .matchAny(o -> getSender().tell(new Status.Failure(
                        new IllegalArgumentException("Unsupported message type: " + o.getClass())), getSelf()))
                .build();
    }

    private void processVideoResults(List<VideoSearchResult> videoResults) {
        Map<String, Long> wordStats = calculateWordStats(videoResults);
        getSender().tell(wordStats, getSelf());
    }

    private Map<String, Long> calculateWordStats(List<VideoSearchResult> videoResults) {
        System.out.println("Started word statistics calculation...");
        long startTime = System.currentTimeMillis();

        Map<String, Long> wordStats = videoResults.stream()
                .flatMap(result -> Arrays.stream(result.getDescription().split("\\W+")))
                .map(String::toLowerCase)
                .filter(word -> !word.isEmpty() && word.matches(".*[a-zA-Z0-9].*")
                        && (word.length() > 1 || word.equals("i") || word.equals("a")))
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        Map<String, Long> sortedWordStats = wordStats.entrySet().stream()
                .sorted((entry1, entry2) -> entry2.getValue().compareTo(entry1.getValue()))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        System.out.println("Finished in " + (System.currentTimeMillis() - startTime) + "ms");
        return sortedWordStats;
    }
}
