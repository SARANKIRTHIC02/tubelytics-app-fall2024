package Util;
import com.fasterxml.jackson.databind.JsonNode;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.junit.jupiter.api.Test;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;;

public class HttpUtilsTest {
    private static final Config config = ConfigFactory.load();
    private static final String BASE_URL = config.getString("youtube.base-url");
    private static final String API_KEY = config.getString("youtube.api-key");
    private static final String SEARCH_ENDPOINT=config.getString("youtube.search-endpoint");

    @Test
    void testSendRequestWithPublicApi() throws IOException, InterruptedException {
        String apiUrl = BASE_URL+SEARCH_ENDPOINT+"&q=youtube&key="+API_KEY;
        JsonNode response = HttpUtils.sendRequest(apiUrl);
        assertNotNull(response);
        assertEquals("youtube#searchListResponse",response.get("kind").asText());
    }

    @Test
    void testSendRequestWithInvalidUrl() {

        String nonExistentUrl = BASE_URL+"inavlidurl";

        assertThrows(IOException.class, () -> {
            HttpUtils.sendRequest(nonExistentUrl);
        });
    }

    @Test
    void testSendRequestInterrupted() {
        String apiUrl = BASE_URL+SEARCH_ENDPOINT+"&q=youtube&key="+API_KEY;
        Thread.currentThread().interrupt();

        try {
            assertThrows(InterruptedException.class, () -> {
                HttpUtils.sendRequest(apiUrl);
            });
        } finally {
            Thread.interrupted();
        }
    }
}
