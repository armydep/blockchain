package org.am.com.blockchain.wallet.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.am.com.blockchain.wallet.controller.exceptions.NodeException;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Component
public class RestClient {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final HttpClient httpClient = HttpClient.newHttpClient();

    public <T> T sendGetRequest(String url, Class<T> responseType) {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new NodeException("Node response status for GET: " + response.statusCode());
            }
            return objectMapper.readValue(response.body(), responseType);
        } catch (IOException | InterruptedException e) {
            throw new NodeException("Failed to send GET request", e);
        }
    }

    public <T> T sendPostRequest(String url, Object payload, Class<T> responseType) {
        try {
            String jsonPayload = objectMapper.writeValueAsString(payload);
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonPayload))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new NodeException("Node response status for POST: " +
                        response.statusCode() + ". " + response.body());
            }
            return objectMapper.readValue(response.body(), responseType);
        } catch (IOException | InterruptedException e) {
            throw new NodeException("Failed to send POST request", e);
        }
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
