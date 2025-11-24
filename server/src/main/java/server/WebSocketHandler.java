package server;

import chess.*;
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
            return;
        }
        int gameID = joinCommand.getGameID();
        GameData game= null;
        try{
            game = gameSQL.getGame(gameID);
        }catch(DataAccessException e){
            errorMessage(ctxt,"Error: Game not found");
            return;
        }
        sessions.add(ctxt.session,gameID, authData.username());

        LoadGameMessage load = new LoadGameMessage(LOAD_GAME,game.game());
        ctxt.send(new Gson().toJson(load));

        boolean isObserver = (joinCommand.getColor() == null);

        String note = (isObserver ? authData.username() + " is observing the game."
                : authData.username() + " joined as "+ joinCommand.getColor());
        NotificationMessage notification = new NotificationMessage(NOTIFICATION, note);
        sessions.broadcast(ctxt.session, notification);
    }

    public void makeMove(WsMessageContext ctxt, UserGameCommand command,String json) throws IOException {
        MakeMoveCommand moveCommand = new Gson().fromJson(json, MakeMoveCommand.class);
        AuthData authData = auth(command);
        if(authData == null){
            errorMessage(ctxt,"Error: User not logged in");
            return;
        }
        int gameID = moveCommand.getGameID();
        try{
            GameData game = gameSQL.getGame(gameID);
            if(game.game().getGameOver()){
                errorMessage(ctxt,"Error: game is over");
                return;
            }
            boolean white = game.whiteUsername().equals(authData.username());
            boolean black = game.blackUsername().equals(authData.username());
            if(!white && !black){
                errorMessage(ctxt,"Error:observers cannot make moves");
                return;
            }
            ChessGame chessGame = game.game();
            ChessMove move = moveCommand.getMove();
            ChessPiece piece = chessGame.getBoard().getPiece(move.getStartPosition());
            if(white){
                if(piece.getTeamColor() == ChessGame.TeamColor.BLACK){
                    errorMessage(ctxt, "Error: can't move enemy pieces");
                }
            }else{
                if(piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                    errorMessage(ctxt, "Error: can't move enemy pieces");
                }
            }
            gameSQL.updateGame(gameID,null,null,moveCommand.getMove());
            chessGame = game.game();
            if(white){
                if(chessGame.isInCheckmate(ChessGame.TeamColor.BLACK)){
                    String note = "Game Over " + game.blackUsername() + " is in checkmate by " + game.whiteUsername() +
                    "'s " + piece;
                    NotificationMessage notification = new NotificationMessage(NOTIFICATION, note);
                    sessions.broadcast(ctxt.session, notification);
                }else if(chessGame.isInStalemate(ChessGame.TeamColor.BLACK)){

                }else if(chessGame.isInCheck(ChessGame.TeamColor.BLACK)){

                }
            }else{

            }

            LoadGameMessage load = new LoadGameMessage(LOAD_GAME,game.game());
            sessions.broadcast(null,load);
            String notification = authData.username() + " moved " + piece.getPieceType() + " from " +
                    move.getStartPosition() + " to " + move.getEndPosition();
            NotificationMessage notificationMessage = new NotificationMessage(NOTIFICATION,notification);
            sessions.broadcast(ctxt.session,notificationMessage);

        }catch(DataAccessException | InvalidMoveException e){
            errorMessage(ctxt,e.getMessage());
        }





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
