package fr.lernejo.navy_battle;

import java.io.IOException;

public class Launcher {
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length == 0 || args.length >= 3)
            throw new IllegalArgumentException("Need 1 port as a first argument");
        int port = Integer.parseInt(args[0]);
        Server s = new Server(port);
        PlayerBoard b = s.startServer();
        if (args.length == 2) {
            b.setEnnemyPort(Integer.parseInt(args[1].split(":")[2]));
            new PostRq(args, port);
        }
    }
}
