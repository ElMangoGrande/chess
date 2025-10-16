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

    public CreateGameResult createGame(CreateGameRequest createGameRequest) throws AlreadyTakenException, DoesNotExistException{
        //gets auth token
        GameData newGame;
        try{
            AuthData authData = AuthDao.getAuth(createGameRequest.authToken());}
        catch (DataAccessException e) {
            throw new DoesNotExistException(e.getMessage());
        }
        //checks to see if game already exists by going over list and comparing names
        try{
            Set<GameData> games = GameDao.listGames();
            for(GameData game : games){
                if(game.gameName().equals(createGameRequest.gameName())){
                    throw new AlreadyTakenException("Error:game already exists by that name");
                }
            }
        } catch (Exception e) {
            throw new DoesNotExistException(e.getMessage());
        }

        try{
            newGame = GameDao.createGame(createGameRequest.gameName());
        } catch (DataAccessException e) {
            throw new AlreadyTakenException(e.getMessage());
        }
        return new CreateGameResult(newGame.gameId());
    }

    public void JoinGame(JoinGameRequest joinGameRequest){
        AuthData authData;
        GameData game;

        try{
            authData = AuthDao.getAuth(joinGameRequest.authToken());}
        catch (DataAccessException e) {
            throw new DoesNotExistException(e.getMessage());
        }

        String username = authData.username();

        try{
            game = GameDao.getGame(joinGameRequest.gameID());
        } catch (DataAccessException e) {
            throw new DoesNotExistException(e.getMessage());
        }

        try {
            GameDao.updateGame(game.gameId(), joinGameRequest.playerColor(), username, null);
        } catch (DataAccessException e) {
            throw new DoesNotExistException(e.getMessage());
        }
    }

    public void clearGames(){
        GameDao.clear();
        AuthDao.clear();
        UserDao.clear();
    }
}
