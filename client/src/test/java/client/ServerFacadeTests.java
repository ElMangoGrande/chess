package client;

import model.*;
import org.junit.jupiter.api.*;
import server.Server;
import serverhandling.ResponseException;
import serverhandling.ServerFacade;
import service.AlreadyTakenException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @Test
    void register() throws ResponseException {
        facade.clear();
       RegistrationResult res = facade.register(new RegistrationRequest("Chris","420","G"));
       assertEquals("Chris", res.username());
    }

    @Test
    void registerFail() throws ResponseException {
        facade.clear();
        facade.register(new RegistrationRequest("Chris","420","G"));
        assertThrows(ResponseException.class, () -> facade.register(new RegistrationRequest("Chris","420","G")));
    }


    @Test
    void login() throws ResponseException {
        facade.clear();
        facade.register(new RegistrationRequest("Chris","420","G"));
        LoginResult res = facade.login(new LoginRequest("Chris","420"));
        assertEquals("Chris", res.username());
    }

    @Test
    void loginFail() throws ResponseException {
        facade.clear();
        facade.register(new RegistrationRequest("Chris","420","G"));
        LoginResult res = facade.login(new LoginRequest("Chris","420"));
        assertThrows(ResponseException.class, () -> facade.login(new LoginRequest("Chris","421")));
    }

    @Test
    void logout() throws ResponseException {
        facade.clear();
        facade.register(new RegistrationRequest("Chris","420","G"));
        LoginResult res = facade.login(new LoginRequest("Chris","420"));
        facade.logout(new LogoutRequest(res.authToken()));
        assertThrows(ResponseException.class, () -> facade.createGame(new CreateGameRequest(res.authToken(),"Game")));
    }

    @Test
    void logoutFail() throws ResponseException {
        facade.clear();
        facade.register(new RegistrationRequest("Chris","420","G"));
        LoginResult res = facade.login(new LoginRequest("Chris","420"));
        assertThrows(ResponseException.class, () -> facade.logout(new LogoutRequest("beans")));
    }

    @Test
    void joinGame() throws ResponseException {
        facade.clear();
        facade.register(new RegistrationRequest("Chris","420","G"));
        LoginResult res = facade.login(new LoginRequest("Chris","420"));
        facade.createGame(new CreateGameRequest(res.authToken(),"Game"));
        facade.joinGame(new JoinGameRequest("WHITE",1,res.authToken()));
        assertThrows(ResponseException.class, () -> facade.createGame(new CreateGameRequest(res.authToken(),"Game")));
    }

    @Test
    void joinGameFail() throws ResponseException {
        facade.clear();
        facade.register(new RegistrationRequest("Chris","420","G"));
        LoginResult res = facade.login(new LoginRequest("Chris","420"));
        facade.createGame(new CreateGameRequest(res.authToken(),"Game"));
        assertThrows(ResponseException.class, () -> facade.joinGame(new JoinGameRequest("WHIE",1,res.authToken())));
    }

    @Test
    void createGame() throws ResponseException {
    }

    @Test
    void createGameFail() throws ResponseException {
    }

    @Test
    void listGames() throws ResponseException{
    }

    @Test
    void listGamesFail() throws ResponseException{
    }

    @Test
    void clear() throws ResponseException {
    }
}
