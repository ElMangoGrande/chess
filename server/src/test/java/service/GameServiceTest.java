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
    private static UserService userService;
    static String authToken;

    @BeforeAll
    static void setup() throws DataAccessException {
        GameDao gameDao = new MemoryGameDAO();
        AuthDao authDao = new MemoryAuthDAO();
        UserDao userDao = new MemoryUserDAO();

        gameService = new GameService(gameDao,authDao);
        userService = new UserService(userDao,authDao);

        RegistrationRequest registrationRequest = new RegistrationRequest("Hyrum","gamer420","nonyabusiness@gmail.com");
        authToken = userService.register(registrationRequest).authToken();
    }

    @Test
    void listGamesPass() throws DataAccessException, InvalidMoveException {
        gameService.createGame(new CreateGameRequest(authToken,"New Game"));
        ListGamesResult results = gameService.listGames(new ListGamesRequest(authToken));
        gameService.joinGame(new JoinGameRequest("WHITE",1,authToken));
        GameData game = results.games().iterator().next();
        assertEquals(1,game.gameID());
        assertEquals("Hyrum",game.whiteUsername());
    }

    @Test
    void listGamesFail() {
        ListGamesRequest listGamesRequest = new ListGamesRequest("gamerWord");
        assertThrows(UnauthorizedResponse.class, () -> gameService.listGames(listGamesRequest));
    }

    @Test
    void createGamePass() throws DataAccessException {
        CreateGameResult game1 = gameService.createGame(new CreateGameRequest(authToken,"New Game"));
        CreateGameResult game2 = gameService.createGame(new CreateGameRequest(authToken,"New Game2"));
        CreateGameResult game3 = gameService.createGame(new CreateGameRequest(authToken,"New Game3"));
        assertEquals(1, game1.gameID());
        assertEquals(2, game2.gameID());
        assertEquals(3, game3.gameID());
    }

    @Test
    void createGameFail() throws DataAccessException {
        gameService.createGame(new CreateGameRequest(authToken,"New Game"));
        assertThrows(AlreadyTakenException.class, () -> gameService.createGame(new CreateGameRequest(authToken,"New Game")));
    }

    @Test
    void joinGamePass() throws DataAccessException, InvalidMoveException {
        RegistrationRequest registrationRequest = new RegistrationRequest("Chris","25565","business@gmail.com");
        String authToken2 = userService.register(registrationRequest).authToken();
        CreateGameResult game1 = gameService.createGame(new CreateGameRequest(authToken,"New Game"));
        JoinGameRequest request = new JoinGameRequest("WHITE",1,authToken);
        JoinGameRequest request2 = new JoinGameRequest("BLACK",1,authToken2);
        gameService.joinGame(request);
        gameService.joinGame(request2);
        ListGamesResult results = gameService.listGames(new ListGamesRequest(authToken));
        GameData game = results.games().iterator().next();
        assertEquals("Hyrum",game.whiteUsername());
        assertEquals("Chris",game.blackUsername());
    }

    @Test
    void joinGameFail() throws DataAccessException, InvalidMoveException {
        RegistrationRequest registrationRequest = new RegistrationRequest("Chris","25565","business@gmail.com");
        String authToken2 = userService.register(registrationRequest).authToken();
        CreateGameResult game1 = gameService.createGame(new CreateGameRequest(authToken,"New Game"));
        JoinGameRequest request = new JoinGameRequest("WHITE",1,authToken);
        JoinGameRequest request2 = new JoinGameRequest("WHITE",1,authToken2);
        gameService.joinGame(request);
        assertThrows(AlreadyTakenException.class, () -> gameService.joinGame(request2));
    }

    @Test
    void clearGamesPass() throws DataAccessException, InvalidMoveException {
        RegistrationRequest registrationRequest = new RegistrationRequest("Chris","25565","business@gmail.com");
        String authToken2 = userService.register(registrationRequest).authToken();
        CreateGameResult game1 = gameService.createGame(new CreateGameRequest(authToken,"New Game"));
        JoinGameRequest request = new JoinGameRequest("WHITE",1,authToken);
        JoinGameRequest request2 = new JoinGameRequest("BLACK",1,authToken2);
        gameService.joinGame(request);
        gameService.joinGame(request2);
        gameService.clearGames();
        ListGamesResult results = gameService.listGames(new ListGamesRequest(authToken));
        assertTrue(results.games().isEmpty());
    }

}