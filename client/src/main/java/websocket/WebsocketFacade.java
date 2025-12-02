package websocket;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.*;
import serverhandling.ResponseException;
import ui.REPL;
import websocket.commands.JoinCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WebsocketFacade extends Endpoint {

    private Session sesh;
    private REPL repl;

    public WebsocketFacade(REPL repl, String url) throws Exception {
        this.repl = repl;
        String ur = url.replace("http","ws");
        ur += "/ws";
        URI uri = new URI(ur);
        WebSocketContainer contain = ContainerProvider.getWebSocketContainer();
        contain.connectToServer(this,uri);

    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {
        sesh = session;
        session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message){
                try {
                    Gson gson = new Gson();
                    ServerMessage msg = gson.fromJson(message, ServerMessage.class);

                    switch (msg.getServerMessageType()) {
                        case LOAD_GAME -> {
                            LoadGameMessage load = gson.fromJson(message, LoadGameMessage.class);
                            ChessGame game = load.getCoolGame();
                            repl.updateGame(game);
                            repl.renderBoard();
                        }
                        case NOTIFICATION -> {
                            NotificationMessage note = gson.fromJson(message, NotificationMessage.class);
                            repl.printMessage(note.getNotificationMessage());
                        }
                        case ERROR -> {
                            ErrorMessage error = gson.fromJson(message, ErrorMessage.class);
                            repl.printMessage(error.getErrorMessage());
                        }
                    }
                } catch (Exception e) {
                    repl.printMessage(e.getMessage());
                }
            }
        });
    }



    public void connect(String authToken, int gameID, String color) throws ResponseException {
        try{
            JoinCommand joinCommand = new JoinCommand(UserGameCommand.CommandType.CONNECT,authToken,gameID,color);
            sesh.getBasicRemote().sendText(new Gson().toJson(joinCommand));
        } catch (IOException e) {
            throw new ResponseException(e.getMessage());
        }
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws ResponseException {
        try{
            MakeMoveCommand moveCommand = new MakeMoveCommand(UserGameCommand.CommandType.MAKE_MOVE,authToken,gameID,move);
            sesh.getBasicRemote().sendText(new Gson().toJson(moveCommand));
        } catch (IOException e) {
            throw new ResponseException(e.getMessage());
        }
    }

    public void leaveGame(String authToken, int gameID) throws ResponseException {
        try{
            UserGameCommand leaveCommand = new UserGameCommand(UserGameCommand.CommandType.LEAVE,authToken,gameID);
            sesh.getBasicRemote().sendText(new Gson().toJson(leaveCommand));
        } catch (IOException e) {
            throw new ResponseException(e.getMessage());
        }
    }

    public void resignGame(String authToken, int gameID) throws ResponseException {
        try{
            UserGameCommand resignCommand = new UserGameCommand(UserGameCommand.CommandType.RESIGN,authToken,gameID);
            sesh.getBasicRemote().sendText(new Gson().toJson(resignCommand));
        } catch (IOException e) {
            throw new ResponseException(e.getMessage());
        }
    }

}