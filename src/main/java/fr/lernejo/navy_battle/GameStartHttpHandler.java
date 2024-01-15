package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

import org.json.JSONObject;

class GameStartHttpHandler implements HttpHandler {
    private final int _port;
    private final PlayerBoard _b;
    public GameStartHttpHandler(int port, PlayerBoard b){
        _port = port;
        _b = b;

    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("POST"))
            sendResponse(exchange,404,"Not Found");
        InputStreamReader requestBodyReader = new InputStreamReader(exchange.getRequestBody());
        StringBuilder requestBody = new StringBuilder();
        if (!isSchemaMatching(requestBodyReader,requestBody))
            sendResponse(exchange, 400, "Bad Request");
        else {
            String s = requestBody.toString().replaceAll("\"","");
            if(requestBody.toString().contains("{"))
                _b.SetEnnemyPort(Integer.parseInt(s.split(",")[1].split(":")[3]));
            else
                _b.SetEnnemyPort(Integer.parseInt(requestBody.toString().split("&")[1].split("localhost%3A")[1]));
            sendResponse(exchange, 202, createJson(requestBody.toString()));
        }
    }
    public String createJson(String requestBody){
        JSONObject json = new JSONObject(requestBody);
        json.put("id","7");
        json.put("url","http://localhost:" + _b.getMyport());
        json.put("message","Estoy un JSON de la muerte");
        return json.toString();
    }
    public boolean isSchemaMatching(InputStreamReader requestBodyReader,StringBuilder requestBody) throws IOException {
        int c;
        while ((c = requestBodyReader.read()) != -1)
            requestBody.append((char) c);
        String s = requestBody.toString();
        System.out.println(s);
        return s.contains("id") && s.contains("url") && s.contains("message");
    }
    public void sendResponse(HttpExchange exchange,int code, String message) throws IOException {
        exchange.sendResponseHeaders(code, message.length());
        OutputStream os = exchange.getResponseBody();
        os.write(message.getBytes());
        os.close();
        System.out.println(message);
        try {
            String s = _b.Indexx();
            System.out.println(s);
            _b.Play(s);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public int getPort() {
        return _port;
    }
}
