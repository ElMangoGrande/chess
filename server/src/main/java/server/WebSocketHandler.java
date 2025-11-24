package server;

import com.google.gson.Gson;
import io.javalin.router.Endpoint;
import io.javalin.websocket.*;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import org.eclipse.jetty.websocket.common.WebSocketSession;
import websocket.commands.UserGameCommand;

import java.net.URI;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final WebSocketSessions sessions = new WebSocketSessions();

    @Override
    public void handleConnect(WsConnectContext ctxt){
        ctxt.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctxt){
        String json = ctxt.message();
        UserGameCommand command = new Gson().fromJson(json,UserGameCommand.class);

        switch(command.getCommandType()){
            case CONNECT -> connect();
            case MAKE_MOVE -> makeMove();
            case LEAVE -> leave();
            case RESIGN -> resign();
        }
    }

    public void connect(){
        //connects the user to the game in the server ;)
    }

    public void makeMove(){

    }

    public void leave(){

    }

    public void resign(){

    }

    @Override
    public void handleClose(WsCloseContext ctxt){
        sessions.remove(ctxt.session);
    }

}
