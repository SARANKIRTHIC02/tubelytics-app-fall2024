package Util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


/**
 * The HttpUtils class provides utility methods to send HTTP requests and process responses.
 * It uses Java's HttpClient to make requests and Jackson's ObjectMapper to parse JSON responses.
 */
public class HttpUtils {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final HttpClient httpClient = HttpClient.newHttpClient();

    /**
     * Sends an HTTP GET request to the specified API URL and parses the JSON response.
     * @param apiUrl The URL of the API endpoint to send the request to.
     * @return A JsonNode representing the JSON response body.
     * @throws IOException If an I/O error occurs when sending or receiving the request.
     * @throws InterruptedException If the operation is interrupted.
     */
    public static JsonNode sendRequest(String apiUrl) throws IOException, InterruptedException {

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Accept", "application/json")
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return objectMapper.readTree(response.body());
    }
}
