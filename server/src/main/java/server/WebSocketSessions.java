package server;

import org.eclipse.jetty.websocket.api.Session;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketSessions {
    public final Map<Integer, Map<String,Session>> connections = new HashMap<>();

    public void add(Session session, Integer gameID, String username) {
        connections.computeIfAbsent(gameID,myHash -> new HashMap<>()).put(username, session);
    }

    public void remove(Session session) {
        for (Map<String, Session> gameMap : connections.values()) {
            gameMap.values().removeIf(s -> s.equals(session));
        }
    }

    public void broadcast(Session excludeSession, ServerMessage notification) throws IOException {
        String json = new com.google.gson.Gson().toJson(notification);

        for (Map<String,Session> gameMap : connections.values()) {
            for(Session s : gameMap.values()){
                if(s.isOpen() && !s.equals(excludeSession)){
                    s.getRemote().sendString(json);
                }
            }
        }
    }
}