package org.am.com.blockchain.wallet.rest;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class RestClient {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    // HTTP Client Instance
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public <T> T sendGetRequest(String url, String address,
                                Class<T> responseType) throws IOException {
        try {
            String fullUrl = String.format("%s/%s", url, address);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(fullUrl))
                    .GET()
                    .build();
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new RuntimeException("Backend error: " + response.statusCode());
            }
            return objectMapper.readValue(response.body(), responseType);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to send Get request", e);
        }
    }

    /*
    // Forward a POST request and handle List<AA> response
    @PostMapping("/forward")
    public List<AA> forwardRequest(@RequestBody Object payload) {
        return restClient.sendPostRequest(
                "http://localhost:8080/api/data",
                payload,
                new TypeReference<List<AA>>() {}
        );
     */
    public <T> T sendPostRequest(String url, Object payload, TypeReference<T> responseType) {
        try {
            // Convert payload to JSON
            String jsonPayload = objectMapper.writeValueAsString(payload);

            // Build HTTP request
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();

            // Send request and parse JSON response
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(response.body(), responseType);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException("Failed to send POST request", e);
        }
    }
}
