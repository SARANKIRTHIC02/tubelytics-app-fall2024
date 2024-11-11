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

    @BeforeEach
    public void setUp() {
        objectMapper = new ObjectMapper();
        mockedHttpClientStatic = Mockito.mockStatic(HttpClient.class);
        mockedHttpClientStatic.when(HttpClient::newHttpClient).thenReturn(mockHttpClient);
    }

    @AfterEach
    public void tearDown() {
        mockedHttpClientStatic.close();
    }

    @Test
    public void testSendRequestValidJsonResponse() throws Exception {
        System.out.println("HttpUtilss 1");
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

    @Test
    public void testSendRequestNonJsonResponse() throws Exception {
        System.out.println("HttpUtilss 2");
        String apiUrl = "http://example.com/api";
        String nonJsonResponse = "<html><body>Not JSON</body></html>";


        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenReturn(mockResponse);
        when(mockResponse.body()).thenReturn(nonJsonResponse);

        assertThrows(JsonParseException.class, () -> HttpUtils.sendRequest(apiUrl));
    }

    @Test
    public void testSendRequestIOException() throws Exception {
        System.out.println("HttpUtilss 3");
        String apiUrl = "http://example.com/api";

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new IOException("Network error"));

        assertThrows(IOException.class, () -> HttpUtils.sendRequest(apiUrl));
    }

    @Test
    public void testSendRequestInterruptedException() throws Exception {
        System.out.println("HttpUtilss 4");
        String apiUrl = "http://example.com/api";

        when(mockHttpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class)))
                .thenThrow(new InterruptedException("Request interrupted"));


        assertThrows(InterruptedException.class, () -> HttpUtils.sendRequest(apiUrl));
    }

    @Test
    public void testSendRequestEmptyJsonResponse() throws Exception {
        System.out.println("HttpUtilss 5");
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
