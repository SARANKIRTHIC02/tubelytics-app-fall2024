package model;


import Util.HttpUtilsTest;
import actors.*;
import controllers.HomeControllerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
    YouTubeServiceTest.class,
        WordStatsActorTest.class,
        ChannelProfileResultTest.class,
        ChannelActorTest.class,
        TublyticServiceTest.class,
        VideoSearchActorTest.class,
        WebSocketActorTest.class,
        HomeControllerTest.class,
        VideoSearchResultTest.class,
        TagActorTest.class,
        SearchResponseTest.class


})
public class AllTestsSuite {
    // This class remains empty; it is used only as a holder for the above annotations
}
