package service;

import dataaccess.DataAccessException;
import chess.InvalidMoveException;
import dataaccess.GameDao;
import dataaccess.AuthDao;
import model.*;
import org.eclipse.jetty.http.BadMessageException;

import java.util.Set;

public class GameService {

    private static GameDao GameDao;
    private static AuthDao AuthDao;

    public GameService(GameDao passedGameDao, AuthDao authDao) {
        GameDao = passedGameDao;
        AuthDao = authDao;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException{
        Set<GameData> gameDataSet;
        //gets auth token
        AuthDao.getAuth(listGamesRequest.authToken());
        //trys to get list of games
        gameDataSet = GameDao.listGames();
        //returns the set of games
        return new ListGamesResult(gameDataSet);
    }

    public  CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException{
        if(createGameRequest.gameName()==null){
            throw new BadMessageException("Error: no game name given");
        }
        //gets auth token
        AuthDao.getAuth(createGameRequest.authToken());
        //creates a game
        GameData newGame = GameDao.createGame(createGameRequest.gameName());
        return new CreateGameResult(newGame.gameId());
    }

    public void JoinGame(JoinGameRequest joinGameRequest) throws DataAccessException, InvalidMoveException{

        //gets auth token
        AuthData authData = AuthDao.getAuth(joinGameRequest.authToken());
        //gets username
        String username = authData.username();
        //gets the game
        GameData game = GameDao.getGame(joinGameRequest.gameID());
        //updates the game
        GameDao.updateGame(game.gameId(), joinGameRequest.playerColor(), username, null);
    }

    public void clearGames(){
        GameDao.clear();
    }
}
