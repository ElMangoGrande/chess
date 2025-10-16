package service;

import dataaccess.DataAccessException;
import dataaccess.GameDao;
import dataaccess.AuthDao;

import dataaccess.UserDao;
import model.*;

import java.util.Set;

public class GameService {

    private GameDao GameDao;
    private AuthDao AuthDao;
    private UserDao UserDao;

    public GameService(GameDao passedGameDao, AuthDao authDao, UserDao userDao) {
        GameDao = passedGameDao;
        AuthDao = authDao;
        UserDao = userDao;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) throws DoesNotExistException{
        Set<GameData> gameDataSet;
        //gets auth token
        try{
            AuthData authData = AuthDao.getAuth(listGamesRequest.authToken());}
        catch (DataAccessException e) {
            throw new DoesNotExistException(e.getMessage());
        }
        //trys to get list of games
        try{
            gameDataSet = GameDao.listGames();
        } catch (Exception e) {
            throw new DoesNotExistException(e.getMessage());
        }
        //returns the set of games
        return new ListGamesResult(gameDataSet);
    }

    public CreateGameResult createGame(CreateGameRequest createGameRequest)



    public void clearGames(){
        GameDao.clear();
        AuthDao.clear();
        UserDao.clear();
    }
}
