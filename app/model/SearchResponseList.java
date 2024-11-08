package model;
import java.util.*;

/**
 * The class represents a collection of objects associates with a unique session ID.
 * This class is used to store and retrieve search responses for a specific session.
 */
public class SearchResponseList {
    private List<SearchResponse> requestModels;
    private String sessionID;

    /**
     * Constructs a SearchResponseList with the specified list of search responses.
     * and a unique session ID.
     *
     * @param requestModels a list of SearchResponse objects representing individual search responses.
     * @param sessionID the unique identifier for the session associated with these search responses.
     */
    public SearchResponseList(List<SearchResponse> requestModels, String sessionID){

        this.requestModels=requestModels;
        this.sessionID=sessionID;
    }

    /**
     * Gets the list of search responses for the current session.
     *
     * @return a list of SearchResponse objects.
     */
    public List<SearchResponse> getRequestModels(){
        return requestModels;
    }

    /**
     * Gets the unique session ID associated with this collection of search responses.
     *
     * @return the session ID
     */
    public String getSessionID(){
        return sessionID;
    }
}
