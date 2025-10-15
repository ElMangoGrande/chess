package dataaccess;

import model.CreateGameRequest;
import model.CreateGameResult;
import model.GameData;

import java.util.List;

public interface GameDao {
    int createGame(String gameName) throws DataAccessException;
    GameData getGame(int gameID) throws DataAccessException;
    List<GameData> listGames() throws DataAccessException;
    void updateGame(GameData game) throws DataAccessException;
}
