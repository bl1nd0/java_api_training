package fr.lernejo.navy_battle;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class PostRq {
    private final String _response;
    public PostRq(String[] args, int port) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        String adversaryUrl = args[1];
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(adversaryUrl + "/api/game/start"))
            .setHeader("Accept", "application/json")
            .setHeader("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString("{\"id\":\"1\", \"url\":\"http://localhost:" + port + "\", \"message\":\"hello\"}"))
            .build();
        HttpResponse<String> response = null;
        response = client.send(request, HttpResponse.BodyHandlers.ofString());
        _response = response.body();
    }
    public String GetResponse(){
        return _response;
    }
}
