package Util;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HttpUtilsTest {

    @Mock
    private HttpClient mockHttpClient;

    @Mock
    private HttpResponse<String> mockResponse;

    private ObjectMapper objectMapper;
    private MockedStatic<HttpClient> mockedHttpClientStatic;

    /**
     * Sets up the ObjectMapper and mocks HttpClient as a static instance before each test.
     * @author saran
     */
    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        mockedHttpClientStatic = Mockito.mockStatic(HttpClient.class);
        mockedHttpClientStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
    }

    /**
     * Closes the mocked HttpClient static instance after each test.
     * @author saran
     */
    @AfterEach
    public void tearDown() {
        mockedHttpClientStatic.close();
    }

    /**
     *
     * Tests sendRequest with a valid JSON response.
     * Verifies that the response is correctly parsed to a JsonNode with expected structure and content.
     *
     * @throws Exception if there is an error in sending the request or parsing JSON
     * @author durai
     */
    @Test
    public void testSendRequestValidJsonResponse() throws Exception {
        String apiUrl = "http://example.com/api";
        String validJson = "{\"key\":\"value\"}";
        JsonNode expectedNode = objectMapper.readTree(validJson);
        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn(validJson);

        JsonNode actualNode = HttpUtils.sendRequest(apiUrl);
        assertNotNull(actualNode);
        assertEquals(expectedNode, actualNode);
    }

    /**
     *
     * Tests sendRequest with a non-JSON response.
     * Verifies that a JsonParseException is thrown when response content is not JSON.
     *
     * @throws Exception if there is an error in sending the request
     * @author durai
     */
    @Test
    public void testSendRequestNonJsonResponse() throws Exception {
        String apiUrl = "http://example.com/api";
        String nonJsonResponse = "<html><body>Not JSON</body></html>";


        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn(nonJsonResponse);

        assertThrows(JsonParseException.class, () -> HttpUtils.sendRequest(apiUrl));
    }

    /**
     * Tests sendRequest when an IOException occurs.
     * Verifies that the IOException is thrown by sendRequest in case of a network error.
     *
     * @throws Exception if there is an error in sending the request
     * @author sushanth
     */
    @Test
    public void testSendRequestIOException() throws Exception {
        String apiUrl = "http://example.com/api";

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Network error"));

        assertThrows(IOException.class, () -> HttpUtils.sendRequest(apiUrl));
    }

    /**
     * Tests sendRequest when an InterruptedException occurs.
     * Verifies that the InterruptedException is thrown by sendRequest in case of a request interruption.
     *
     * @throws Exception if there is an error in sending the request
     * @author sushanth
     */
    @Test
    public void testSendRequestInterruptedException() throws Exception {
        String apiUrl = "http://example.com/api";

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new InterruptedException("Request interrupted"));


        assertThrows(InterruptedException.class, () -> HttpUtils.sendRequest(apiUrl));
    }

    /**
     *
     * Tests sendRequest with an empty JSON response.
     * Verifies that an empty JsonNode is returned without any errors.
     *
     * @throws Exception if there is an error in sending the request or parsing JSON
     * @author durai
     */
    @Test
    public void testSendRequestEmptyJsonResponse() throws Exception {
        String apiUrl = "http://example.com/api";
        String emptyJson = "{}";
        JsonNode expectedNode = objectMapper.readTree(emptyJson);

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn(emptyJson);


        JsonNode actualNode = HttpUtils.sendRequest(apiUrl);

        assertNotNull(actualNode);
        assertEquals(expectedNode, actualNode);
    }


}
