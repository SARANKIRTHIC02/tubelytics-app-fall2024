package model;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.*;

public class TubelyticService {
    public static  List<VideoSearchResult> fetchResults(String query) {
        List<VideoSearchResult> results= new ArrayList<>();

        if (!query.isEmpty()) {
            try {
                System.out.println("Line 49");
                YouTubeService youTubeService=new YouTubeService();
                results=youTubeService.searchVideosInfo(query);
            } catch (GeneralSecurityException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }


        } else {
            results=Collections.emptyList();
        }

        return results;
    }

}
