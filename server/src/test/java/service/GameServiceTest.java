package service;

import chess.InvalidMoveException;
import dataaccess.*;
import io.javalin.http.UnauthorizedResponse;
import model.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameServiceTest {

    private static GameService gameService;
    static String authToken;

    @BeforeAll
    static void setup() throws DataAccessException {
        GameDao gameDao = new MemoryGameDAO();
        AuthDao authDao = new MemoryAuthDAO();
        UserDao userDao = new MemoryUserDAO();

        gameService = new GameService(gameDao,authDao);
        UserService userService = new UserService(userDao,authDao);

        RegistrationRequest registrationRequest = new RegistrationRequest("Hyrum","gamer420","nonyabusiness@gmail.com");
        authToken = userService.register(registrationRequest).authToken();
    }

    @Test
    void listGamesPass() throws DataAccessException, InvalidMoveException {
        gameService.createGame(new CreateGameRequest(authToken,"New Game"));
        ListGamesResult results = gameService.listGames(new ListGamesRequest(authToken));
        gameService.joinGame(new JoinGameRequest("WHITE",1,authToken));
        GameData game = results.allGameData().iterator().next();
        assertEquals(1,game.gameID());
        assertEquals("Hyrum",game.whiteUsername());
    }

    @Test
    void listGamesFail() {
        ListGamesRequest listGamesRequest = new ListGamesRequest("gamerWord");
        assertThrows(UnauthorizedResponse.class, () -> gameService.listGames(listGamesRequest));
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