package fr.lernejo.navy_battle_test;
import fr.lernejo.navy_battle.PlayerBoard;
import fr.lernejo.navy_battle.Server;
import fr.lernejo.navy_battle.PostRq;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class Tests {
    @Test
    public void testPingAPI() throws IOException {
        Server s = new Server(9876);
        s.StartServer();
        URL url = new URL("http://localhost:9876/ping");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");
        assertEquals(200, con.getResponseCode());
        String responseBody = new BufferedReader(new InputStreamReader(con.getInputStream()))
            .lines().collect(Collectors.joining("\n"));
        assertEquals("OK",responseBody);
        s.CloseServer();
    }

    @Test
    public void testStartAPI404() throws IOException, InterruptedException {
        Server s = new Server(1234);
        s.StartServer();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:1234/api/game/start"))
            .setHeader("Accept", "application/json")
            .setHeader("Content-Type", "application/json")
            .GET()
            .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Not Found",response.body());
        s.CloseServer();
    }
    @Test
    public void testStartAPI400() throws IOException, InterruptedException {
        Server s = new Server(4444);
        s.StartServer();
        String template = "{\"IA\":\"1\",\"url\":\"http://localhost:4334\",\"message\":\"bjr\"}";
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:4444/api/game/start"))
            .setHeader("Accept", "application/json")
            .setHeader("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(template))
            .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400,response.statusCode());
        assertEquals("Bad Request",response.body());
        s.CloseServer();
    }
    @Test
    public void testStartAPI202() throws IOException, InterruptedException {
        Server s = new Server(4321);
        s.StartServer();
        String template = "{\"id\":\"1\",\"url\":\"http://localhost:4334\",\"message\":\"bjr\"}";
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:4321/api/game/start"))
            .setHeader("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(template))
            .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(202,response.statusCode());
        assertTrue(response.body().contains("id"));
        assertTrue(response.body().contains("url"));
        assertTrue(response.body().contains("message"));
        s.CloseServer();
    }
    @Test
    public void testFireAPI404() throws IOException, InterruptedException {
        Server s = new Server(9876);
        s.StartServer();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:9876/api/game/fire"))
            .setHeader("Accept", "application/json")
            .setHeader("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString("{}"))
            .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(404, response.statusCode());
        assertEquals("Not Found",response.body());
        s.CloseServer();
    }
    @Test
    public void testFireAPI400() throws IOException, InterruptedException {
        Server s = new Server(7777);
        s.StartServer();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:7777/api/game/fire?admin=true"))
            .setHeader("Content-Type", "application/json")
            .GET()
            .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(400,response.statusCode()); // miss sunk hit
        assertEquals("Bad Request",response.body());
        s.CloseServer();
    }
    @Test
    public void testFireAPI202() throws IOException, InterruptedException {
        Server s = new Server(9876);
        s.StartServer();
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:9876/api/game/fire?cell=B2"))
            .setHeader("Content-Type", "application/json")
            .GET()
            .build();
        HttpClient client = HttpClient.newHttpClient();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(202,response.statusCode()); // miss sunk hit
        String[] temp = {"miss","sunk","hit"};
        assertTrue(response.body().contains(temp[0]) || response.body().contains(temp[1]) || response.body().contains(temp[2]));
        s.CloseServer();
    }

    @Test
    public void testNewPostRq() throws IOException, InterruptedException {
        Server s = new Server(9876);
        s.StartServer();
        String[] args = {"0","http://localhost:9876"};
        PostRq p = new PostRq(args,8576);
        assertTrue(p.GetResponse().contains("id") && p.GetResponse().contains("url") && p.GetResponse().contains("message"));
        s.CloseServer();
    }

    @Test
    public  void testPlayerBoardMethods() throws IOException{
        Server s = new Server(9876);
        PlayerBoard p = new PlayerBoard(9876,s);
        assertEquals(0,p.GetBoardVal(0,0));
        p.Indexx();
        p.SetBoardTo0(1,0);
        assertEquals(0,p.GetBoardVal(1,0));
        assertEquals(1,p.GetInc());
        for(int i = 0;i < 10;i++){
            for(int j = 0;j < 10; j++){
                p.SetBoardTo0(i,j);
            }
        }
        assertTrue(!p.ShipLeft());
    }
}
