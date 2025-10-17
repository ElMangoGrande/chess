package service;

import dataaccess.*;
import model.CreateGameRequest;
import model.ListGamesRequest;
import model.ListGamesResult;
import model.RegistrationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    private GameService gameService;
    String authToken;

    @BeforeEach
    void setup() throws DataAccessException {
        GameDao gameDao = new MemoryGameDAO();
        AuthDao authDao = new MemoryAuthDAO();
        UserDao userDao = new MemoryUserDAO();

        gameService = new GameService(gameDao,authDao);
        UserService userService = new UserService(userDao,authDao);

        RegistrationRequest registrationRequest = new RegistrationRequest("Hyrum","gamer420","nonyabusiness@gmail.com");
        authToken = userService.register(registrationRequest).authToken();
    }

    @Test
    void listGamesPass() throws DataAccessException {
        gameService.createGame(new CreateGameRequest(authToken,"New Game"));
        ListGamesResult results = gameService.listGames(new ListGamesRequest(authToken));

        System.out.println(results);
    }

    @Test
    void listGamesFail() {

    }

    @Test
    void createGamePass() {

    }

    @Test
    void createGameFail() {

    }

    @Test
    void joinGamePass() {
    }

    @Test
    void joinGameFail() {
    }

    @Test
    void clearGamesPass() {
    }

    @Test
    void clearGamesFail() {
    }
}