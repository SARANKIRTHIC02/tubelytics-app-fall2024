package controllers;

import model.SearchResponse;
import model.SearchResponseList;
import model.TubelyticService;
import model.VideoSearchResult;
import play.mvc.*;

import java.util.Optional;

import java.util.*;


public class HomeController extends Controller {

    public SearchResponseList accumulatedResults=new SearchResponseList(new ArrayList<>(),UUID.randomUUID().toString());

    public Result index() {
        return ok(views.html.index.render());
    }

    public Result ytlytics(Optional<String> query) {

        System.out.println("Received query: " + query.orElse("none"));
        String searchQuery = query.orElse("");

        List<VideoSearchResult> newResults = TubelyticService.fetchResults(searchQuery);
        SearchResponse model = new SearchResponse(searchQuery, newResults);
        accumulatedResults.getRequestModels().add(0,model);
        return ok(views.html.ytlytics.render(accumulatedResults));
    }


}