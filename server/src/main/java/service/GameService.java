package service;

import dataaccess.DataAccessException;
import chess.InvalidMoveException;
import dataaccess.GameDao;
import dataaccess.AuthDao;
import model.*;
import org.eclipse.jetty.http.BadMessageException;

import java.util.Set;

public class GameService {

    private static GameDao gameDao;
    private static AuthDao authDao;

    public GameService(GameDao passedGameDao, AuthDao passedAuthDao) {
        gameDao = passedGameDao;
        authDao = passedAuthDao;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DataAccessException{
        Set<GameData> gameDataSet;
        //gets auth token
        authDao.getAuth(listGamesRequest.authToken());
        //trys to get list of games
        gameDataSet = gameDao.listGames();
        //returns the set of games
        return new ListGamesResult(gameDataSet);
    }

    public  CreateGameResult createGame(CreateGameRequest createGameRequest) throws DataAccessException{
        if(createGameRequest.gameName()==null){
            throw new BadMessageException("Error: no game name given");
        }
        //gets auth token
        authDao.getAuth(createGameRequest.authToken());
        //creates a game
        GameData newGame = gameDao.createGame(createGameRequest.gameName());
        return new CreateGameResult(newGame.gameID());
    }

    public void joinGame(JoinGameRequest joinGameRequest) throws DataAccessException, InvalidMoveException{

        //gets auth token
        AuthData authData = authDao.getAuth(joinGameRequest.authToken());
        //gets username
        String username = authData.username();
        //gets the game
        GameData game = gameDao.getGame(joinGameRequest.gameID());
        //updates the game
        gameDao.updateGame(joinGameRequest.gameID(), joinGameRequest.playerColor(), username, null);
    }

    //new func
        //get game
        //uses checkmove
        //call update game

    public void clearGames() throws DataAccessException {
        gameDao.clear();
    }
}
