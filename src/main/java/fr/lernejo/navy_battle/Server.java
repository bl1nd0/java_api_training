package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final PlayerBoard b;
    private final HttpServer server;
    public Server(int _port) throws IOException {
        port = _port;
        b = new PlayerBoard(port,this);
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.setExecutor(Executors.newFixedThreadPool(1));
        server.createContext("/ping", new PingHandler());
        server.createContext("/api/game/start", new GameStartHttpHandler(port, b));
        server.createContext("/api/game/fire", new GameFire(b,this));
    }
    public PlayerBoard StartServer(){
        server.start();
        return b;
    }

    public void CloseServer(){
        server.stop(0);
    }
}
