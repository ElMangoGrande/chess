package dataaccess;

import chess.ChessMove;
import model.CreateGameRequest;
import model.CreateGameResult;
import model.GameData;

import java.util.Set;

public interface GameDao {
    GameData createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    Set<GameData> listGames() throws DataAccessException;
    void updateGame(int gameID, String color, String username, ChessMove move) throws DataAccessException;
    void clear();
}
