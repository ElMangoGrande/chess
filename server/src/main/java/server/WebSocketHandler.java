package server;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.router.Endpoint;
import io.javalin.websocket.*;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.common.WebSocketSession;
import websocket.commands.JoinCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;
import java.net.URI;

import static websocket.messages.ServerMessage.ServerMessageType.*;

public class WebSocketHandler implements WsConnectHandler, WsMessageHandler, WsCloseHandler {
    private final WebSocketSessions sessions = new WebSocketSessions();
    private final GameSQL gameSQL = new GameSQL();


    @Override
    public void handleConnect(WsConnectContext ctxt){
        ctxt.enableAutomaticPings();
    }

    @Override
    public void handleMessage(WsMessageContext ctxt) throws IOException {
        String json = ctxt.message();
        UserGameCommand command = new Gson().fromJson(json,UserGameCommand.class);

        switch(command.getCommandType()){
            case CONNECT -> connect(ctxt,command,json);
            case MAKE_MOVE -> makeMove(ctxt,command,json);
            case LEAVE -> leave(ctxt, command);
            case RESIGN -> resign(ctxt, command);
        }
    }

    public void connect(WsMessageContext ctxt, UserGameCommand command,String json) throws IOException {
        JoinCommand joinCommand = new Gson().fromJson(json,JoinCommand.class);
        AuthData authData = auth(command);
        if(authData == null){
            errorMessage(ctxt,"Error: User not logged in");
        }

        int gameID = joinCommand.getGameID();
        GameData game;
        try{
            game = gameSQL.getGame(gameID);
        }catch(DataAccessException e){
            errorMessage(ctxt,"Error: Game not found");
        }

        sessions.add(ctxt.session,gameID, authData.username());

        LoadGameMessage load = new LoadGameMessage(LOAD_GAME,game);
        ctxt.send(new Gson().toJson(load));

        boolean isObserver = (joinCommand.getColor() == null);

        String note = (isObserver ? authData.username() + " is observing the game."
                : authData.username() + " joined as "+ joinCommand.getColor());
        NotificationMessage notification = new NotificationMessage(NOTIFICATION, note);
        sessions.broadcast(ctxt.session, notification);
    }

    public void makeMove(WsMessageContext ctxt, UserGameCommand command,String json){
        MakeMoveCommand moveCommand = new Gson().fromJson(json, MakeMoveCommand.class);
    }

    public void leave(WsMessageContext ctxt, UserGameCommand command){

    }

    public void resign(WsMessageContext ctxt, UserGameCommand command){

    }

    public AuthData auth(UserGameCommand command){
        AuthSQL authDao = new AuthSQL();
        try {
            return authDao.getAuth(command.getAuthToken());
        } catch (DataAccessException e) {
            return null;
        }
    }

    public void errorMessage(WsMessageContext ctxt, String error) throws IOException {
        ErrorMessage message = new ErrorMessage(ERROR,error);
        String errorMessage = new Gson().toJson(message);
        ctxt.session.getRemote().sendString(errorMessage);
    }

    @Override
    public void handleClose(WsCloseContext ctxt){
        sessions.remove(ctxt.session);
    }

}
