package websocket.messages;

import chess.ChessGame;

public class LoadGameMessage extends ServerMessage{
    public LoadGameMessage(ServerMessageType type, ChessGame game) {
        super(type);
        this.game = game;
    }

    private ChessGame game;

    public ChessGame getCoolGame() {
        return game;
    }
}