package client;

import model.RegistrationRequest;
import model.RegistrationResult;
import org.junit.jupiter.api.*;
import server.Server;
import serverhandling.ResponseException;
import serverhandling.ServerFacade;

import static org.junit.jupiter.api.Assertions.assertEquals;


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
    void login() {
    }

    @Test
    void logout() {
    }

    @Test
    void joinGame() {
    }

    @Test
    void createGame() {
    }

    @Test
    void listGames() {
    }

    @Test
    void clear() {
    }
}
