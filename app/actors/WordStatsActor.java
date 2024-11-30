package actors;

import akka.actor.AbstractActor;
import akka.actor.Props;
import akka.actor.Status;
import model.TubelyticService;
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
        Map<String, Long> wordStats = TubelyticService.wordStatistics(videoResults);
        getSender().tell(wordStats, getSelf());
    }


}
