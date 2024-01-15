package fr.lernejo.navy_battle;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.util.Objects;
import org.json.JSONObject;

public class GameFire implements HttpHandler {

    private final PlayerBoard b;
    private final Server s;
    public GameFire(PlayerBoard _b, Server _s){
        b = _b;
        s = _s;
    }
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equals("GET"))
            sendResponse(exchange,404,"Not Found");
        URI rqurl = exchange.getRequestURI();
        String[] c = rqurl.getQuery().split("=");
        if(!Objects.equals(c[0], "cell")){
            sendResponse(exchange,400,"Bad Request");
        }
        String cell = c[1];
        String response = constructResponse(cell);
        sendResponse(exchange,202,response);
    }
    public String constructResponse(String cell){
        String state = navyFormatToCoords(cell);
        boolean shipleft = b.ShipLeft();
        JSONObject json = new JSONObject();
        json.put("consequence",state);
        json.put("shipLeft",shipleft);
        return json.toString();
    }
    public String navyFormatToCoords(String cell){
        int col = cell.charAt(0) - 65;
        String s = String.valueOf(cell.charAt(0));
        cell = cell.replace(s, "");
        int line = Integer.parseInt(cell) - 1;
        return getResult(col,line);
    }

    public String getResult(int col,int line){
        if(b.GetBoardVal(col,line) == 0){
            return "miss";
        }
        else{
            b.SetBoardTo0(col,line);
            int res = isSunked(col,line);
            if(res == 1)
                return "sunk";
            return "hit";
        }
    }
    public int isSunked(int col, int line){
        // Bad way to check if a boat is sunk for a real game but with my boat start disposition it work ... :D
        int res = b.GetBoardVal(col - 1,line - 1) + b.GetBoardVal(col + 1,line + 1);
        res = res + b.GetBoardVal(col - 1,line + 1) + b.GetBoardVal(col + 1,line - 1);
        res = res + b.GetBoardVal(col - 1,line) + b.GetBoardVal(col + 1,line);
        res = res + b.GetBoardVal(col,line - 1) + b.GetBoardVal(col,line + 1);
        if(res == 0)
            return 1;
        return 0;
    }
    public void sendResponse(HttpExchange exchange,int code, String message) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(code, message.length());
        OutputStream os = exchange.getResponseBody();
        os.write(message.getBytes());
        System.out.println(message);
        os.close();
        if(!b.ShipLeft()){
            System.out.println("Dommage Vous Perdez Cette Partie !");
            s.CloseServer();
            System.exit(0);
        }
        else {
            try {b.Play(b.Indexx());}
            catch (InterruptedException e) {throw new RuntimeException(e);}
        }
    }
}

