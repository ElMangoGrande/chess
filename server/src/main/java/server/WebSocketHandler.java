package server;

import chess.*;
import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.router.Endpoint;
import io.javalin.websocket.*;
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
import java.util.Objects;

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
        }catch(Exception e){
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
        sessions.broadcast(ctxt.session, notification,gameID);
    }

    public void makeMove(WsMessageContext ctxt, UserGameCommand command,String json) throws IOException {
        MakeMoveCommand moveCommand = new Gson().fromJson(json, MakeMoveCommand.class);
        //auth check
        AuthData authData = auth(command);
        if(authData == null){
            errorMessage(ctxt,"Error: User not logged in");
            return;
        }
        int gameID = moveCommand.getGameID();

        try{
            GameData game = gameSQL.getGame(gameID);
            //game over check
            if(game.game().getGameOver()){
                errorMessage(ctxt, "Error: Game Over");
                return;
            }
            boolean white = authData.username().equals(game.whiteUsername());
            boolean black = authData.username().equals(game.blackUsername());
            if(!white && !black){
                errorMessage(ctxt,"Error:observers cannot make moves");
                return;
            }
            ChessGame chessGame = game.game();
            ChessMove move = moveCommand.getMove();
            ChessPiece piece = chessGame.getBoard().getPiece(move.getStartPosition());
            if (piece == null) {
                errorMessage(ctxt, "Error: no piece at starting square");
                return;
            }
            if(white && piece.getTeamColor() == ChessGame.TeamColor.BLACK){
                errorMessage(ctxt, "Error: can't move enemy pieces");
                return;
            }
            if(black && piece.getTeamColor() == ChessGame.TeamColor.WHITE){
                errorMessage(ctxt, "Error: can't move enemy pieces");
                return;
            }
            if ((white && game.game().getTeamTurn() != ChessGame.TeamColor.WHITE) ||
                    (black && game.game().getTeamTurn() != ChessGame.TeamColor.BLACK)) {
                errorMessage(ctxt, "Error: not your turn!");
                return;
            }

            ChessGame.TeamColor opponent = white ?
                    ChessGame.TeamColor.BLACK :
                    ChessGame.TeamColor.WHITE;
            String opponentUser = white
                    ? game.blackUsername()
                    : game.whiteUsername();
            String moverUser = white
                    ? game.whiteUsername()
                    : game.blackUsername();

            gameSQL.updateGame(gameID,null,null,moveCommand.getMove());
            //to get updated game
            game = gameSQL.getGame(gameID);
            chessGame = game.game();

            LoadGameMessage load = new LoadGameMessage(LOAD_GAME,game.game());
            sessions.broadcast(null,load,gameID);
            String notification = authData.username() + " moved " + piece.getPieceType() + " from " +
                    move.getStartPosition() + " to " + move.getEndPosition();
            NotificationMessage notificationMessage = new NotificationMessage(NOTIFICATION,notification);
            sessions.broadcast(ctxt.session,notificationMessage,gameID);

            if(chessGame.isInCheckmate(opponent)){
                gameSQL.gameOver(gameID);
                String note = "Game Over " + opponentUser + " was checkmated by " + moverUser +".";
                NotificationMessage m = new NotificationMessage(NOTIFICATION, note);
                sessions.broadcast(null,m,gameID);
                return;
            }
            if(chessGame.isInStalemate(opponent)){
                String note = "Game Over: " + opponentUser + " was Stalemated by " + moverUser;
                NotificationMessage m = new NotificationMessage(NOTIFICATION,note);
                sessions.broadcast(null,m,gameID);
                return;
            }
            if(chessGame.isInCheck(opponent)){
                String note = opponentUser + " is in check.";
                NotificationMessage m = new NotificationMessage(NOTIFICATION,note);
                sessions.broadcast(null,m,gameID);
            }

        }catch(DataAccessException | InvalidMoveException e){
            errorMessage(ctxt,e.getMessage());
        }

    }

    public void leave(WsMessageContext ctxt, UserGameCommand command) throws IOException {
        AuthData authData = auth(command);
        if(authData == null){
            errorMessage(ctxt,"Error: User not logged in");
            return;
        }
        int gameID = command.getGameID();

        GameData gameData;
        try {
            gameData = gameSQL.getGame(gameID);
        }
        catch(DataAccessException e){
            errorMessage(ctxt,e.getMessage());
            return;
        }
        String username = authData.username();
        sessions.remove(ctxt.session);
        boolean isWhite = (Objects.equals(username, gameData.whiteUsername()));
        boolean isBlack = (Objects.equals(username,gameData.blackUsername()));
        boolean isObserver = (!isWhite && !isBlack);

        if(isWhite){
            try{
                gameSQL.leaveGame(gameID,"WHITE");
            } catch (DataAccessException e) {
                errorMessage(ctxt,e.getMessage());
                return;
            }
        } else if(isBlack){
            try{
                gameSQL.leaveGame(gameID,"BLACK");
            } catch (DataAccessException e) {
                errorMessage(ctxt,e.getMessage());
                return;
            }
        }
        String note = isObserver
                ? username + " left the game (observer)."
                : username + " left the game.";
        NotificationMessage n = new NotificationMessage(NOTIFICATION,note);
        sessions.broadcast(ctxt.session,n,gameID);
    }

    public void resign(WsMessageContext ctxt, UserGameCommand command) throws IOException {
        AuthData authData = auth(command);
        if(authData == null){
            errorMessage(ctxt,"Error: User not logged in");
            return;
        }
        int gameID = command.getGameID();
        GameData gameData;
        try {
            gameData = gameSQL.getGame(gameID);
        } catch (DataAccessException e) {
            errorMessage(ctxt, e.getMessage());
            return;
        }
        String username = authData.username();
        boolean isWhite = username.equals(gameData.whiteUsername());
        boolean isBlack = username.equals(gameData.blackUsername());
        if(!isWhite && !isBlack){
            errorMessage(ctxt,"Error: observers cannot resign");
            return;
        }
        if(gameData.game().getGameOver()){
            errorMessage(ctxt, "Error: Game is already over silly");
            return;
        }
        try{
            gameSQL.gameOver(gameID);
        } catch (DataAccessException e) {
            errorMessage(ctxt,e.getMessage());
        }
        String note = username + " resigned. Game Over";
        NotificationMessage n = new NotificationMessage(NOTIFICATION,note);
        sessions.broadcast(null,n,gameID);
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
