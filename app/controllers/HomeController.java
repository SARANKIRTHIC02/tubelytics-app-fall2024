package controllers;

import play.mvc.*;

import java.util.*;

import model.ytSearch;


/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HomeController extends Controller {

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok(views.html.index.render());
    }

    public Result ytlytics(Optional<String> query) {
        String sessionId = UUID.randomUUID().toString();
        String searchQuery = query.orElse("");
        List<String> results = fetchResults(searchQuery);
        ytSearch model = new ytSearch("YT Lytics", results, sessionId);
        return ok(views.html.ytlytics.render(model));
    }


    private List<String> fetchResults(String query) {
        List<String> results = new ArrayList<>();

        if (!query.isEmpty()) {
                results.add("Dummy Result " + 1 + " for query: " + query);

        } else {
            results.add("No results found.");
        }

        return results;
    }


}
