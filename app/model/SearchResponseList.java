package model;
import java.util.*;

public class SearchResponseList {
    private List<SearchResponse> requestModels;
    private String sessionID;

    private static String counter;

    public SearchResponseList(List<SearchResponse> requestModels, String sessionID){

        this.requestModels=requestModels;
        this.sessionID=sessionID;
    }

    public List<SearchResponse> getRequestModels(){
        return requestModels;
    }

    public String getSessionID(){
        return sessionID;
    }
}
