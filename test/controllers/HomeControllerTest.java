package controllers;

import model.ChannelProfileResult;
import model.TubelyticService;
import model.VideoSearchResult;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import play.Application;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Result;
import play.test.WithApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertTrue;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.GET;
import static play.test.Helpers.route;

public class HomeControllerTest extends WithApplication {



    @Test
    public void testIndex() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testYtlyticsWithQuery() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/ytlytics?query=sampleQuery");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testYtlyticsWithoutQuery() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/ytlytics");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testTaglyticsWithQuery() {
        Http.RequestBuilder request = new Http.RequestBuilder().method(GET).uri("/ytlytics/tags/sampleTag");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testWordStatsWithSearchQuery() {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/wordStatistics/sampleSearch");

        Result result = route(app, request);
        assertEquals(OK, result.status());
    }

    @Test
    public void testWordStatsWithoutSearchQuery() {
        HomeController controller = new HomeController();
        String emptySearchQuery = "";
        Result result = controller.wordStats(emptySearchQuery).toCompletableFuture().join();
        assertEquals(OK, result.status());
    }










}
