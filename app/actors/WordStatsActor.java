package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.Status;
import model.VideoSearchResult;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;


/**
 * WordStatsActor is responsible for processing a list of video search results
 * and calculating the word statistics for the descriptions of those videos. It generates
 * a frequency count of words and sorts them in descending order based on the frequency.
 *
 * @author Durai
 * @author Saran
 */
public class WordStatsActor extends AbstractActor {


    /**
     * Creates a new instance of the {@code WordStatsActor}.
     *
     * @return the Props for creating the actor
     * @author Durai
     * @author Saran
     */
    public static Props props() {
        return Props.create(WordStatsActor.class);
    }

    /**
     * Represents a message containing a list of video search results.
     * @author Durai
     * @author Saran
     */
    public static class VideoSearchResultsMessage {
        private final List<VideoSearchResult> videoResults;

        /**
         * Constructs a VideoSearchResultsMessage.
         *
         * @param videoResults the list of video search results
         * @author Durai
         * @author Saran
         */
        public VideoSearchResultsMessage(List<VideoSearchResult> videoResults) {
            this.videoResults = videoResults;
        }

        /**
         * Gets the video search results contained in the message.
         *
         * @return the list of video search results
         * @author Durai
         * @author Saran
         */
        public List<VideoSearchResult> getVideoResults() {
            return videoResults;
        }
    }

    /**
     * Defines the behavior of the WordStatsActor when it receives messages.
     * If the message contains video search results, it processes those results
     * to calculate word statistics.
     *
     * @return the behavior of the actor
     * @author Durai
     * @author Saran
     */
    @Override
    public Receive createReceive() {
        return receiveBuilder()
                .match(VideoSearchResultsMessage.class, msg -> processVideoResults(msg.getVideoResults()))
                .matchAny(o -> getSender().tell(new Status.Failure(
                        new IllegalArgumentException("Unsupported message type: " + o.getClass())), getSelf()))
                .build();
    }

    /**
     * Processes the video results to calculate word statistics.
     *
     * @param videoResults the list of video search results to process
     * @author Durai
     * @author Saran
     */
    private void processVideoResults(List<VideoSearchResult> videoResults) {
        Map<String, Long> wordStats = calculateWordStats(videoResults);
        getSender().tell(wordStats, getSelf());
    }

    /**
     * Calculates word statistics by counting the frequency of words in the video descriptions.
     * The words are filtered to exclude short words (except for "i" and "a") and non-alphanumeric characters.
     * The word counts are then sorted in descending order.
     *
     * @param videoResults the list of video search results to process
     * @return a map of word frequencies, sorted by frequency in descending order
     * @author Durai
     * @author Saran
     */
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
