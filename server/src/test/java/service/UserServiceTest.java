package service;

import io.javalin.http.UnauthorizedResponse;
import model.*;
import org.eclipse.jetty.http.BadMessageException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import chess.InvalidMoveException;
import dataaccess.*;
import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private static UserService userService;
    private static GameService gameService;

    @BeforeAll
    static void setup() throws DataAccessException {
        GameDao gameDao = new MemoryGameDAO();
        AuthDao authDao = new MemoryAuthDAO();
        UserDao userDao = new MemoryUserDAO();

        gameService = new GameService(gameDao,authDao);
        userService = new UserService(userDao,authDao);

    }

    @Test
    void registerPass() throws DataAccessException {
        RegistrationRequest registrationRequest = new RegistrationRequest("Hyrum","gamer420","nonyabusiness@gmail.com");
        RegistrationResult result = userService.register(registrationRequest);
        assertEquals("Hyrum",result.username());
    }

    @Test
    void registerFail() throws DataAccessException {
        RegistrationRequest registrationRequest = new RegistrationRequest("Hyrum","gamer420","nonyabusiness@gmail.com");
        RegistrationRequest registrationRequest2 = new RegistrationRequest("Hyrum","gamer420","nonyabusiness@gmail.com");
        RegistrationRequest registrationRequest3 = new RegistrationRequest(null,"gamer420","nonyabusiness@gmail.com");
        RegistrationRequest registrationRequest4 = new RegistrationRequest("Hyrum",null,"nonyabusiness@gmail.com");
        RegistrationRequest registrationRequest5 = new RegistrationRequest("Hyrum","gamer420",null);
        userService.register(registrationRequest);
        assertThrows(AlreadyTakenException.class, () -> userService.register(registrationRequest2));
        assertThrows(BadMessageException.class, () -> userService.register(registrationRequest3));
        assertThrows(BadMessageException.class, () -> userService.register(registrationRequest4));
        assertThrows(BadMessageException.class, () -> userService.register(registrationRequest5));

    }

    @Test
    void loginPass() throws DataAccessException {
        RegistrationRequest registrationRequest = new RegistrationRequest("Hyrum","gamer420","nonyabusiness@gmail.com");
        RegistrationResult result = userService.register(registrationRequest);
        String authToken = result.authToken();
        LoginRequest loginRequest = new LoginRequest("Hyrum","gamer420");
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        userService.logout(logoutRequest);
        LoginResult loginResult = userService.login(loginRequest);
        assertEquals("Hyrum",loginResult.username());
    }

    @Test
    void loginFail() throws DataAccessException {
        RegistrationRequest registrationRequest = new RegistrationRequest("Hyrum","gamer420","nonyabusiness@gmail.com");
        RegistrationResult result = userService.register(registrationRequest);
        String authToken = result.authToken();
        LoginRequest loginRequest = new LoginRequest("Chris","gamer420");
        LoginRequest loginRequest2 = new LoginRequest(null,"gamer420");
        LoginRequest loginRequest3 = new LoginRequest("Chris",null);
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        userService.logout(logoutRequest);
        assertThrows(UnauthorizedResponse.class, () -> userService.login(loginRequest));
        assertThrows(BadMessageException.class, () -> userService.login(loginRequest2));
        assertThrows(BadMessageException.class, () -> userService.login(loginRequest3));
    }

    @Test
    void logoutPass() throws DataAccessException {
        RegistrationRequest registrationRequest = new RegistrationRequest("Hyrum","gamer420","nonyabusiness@gmail.com");
        RegistrationResult result = userService.register(registrationRequest);
        String authToken = result.authToken();
        LogoutRequest logoutRequest = new LogoutRequest(authToken);
        userService.logout(logoutRequest);
        CreateGameRequest createGameRequest = new CreateGameRequest(authToken, "PostLogoutGame");
        assertThrows(UnauthorizedResponse.class, () -> gameService.createGame(createGameRequest));

    }

    @Test
    void logoutFail() throws DataAccessException {
        RegistrationRequest registrationRequest = new RegistrationRequest("Hyrum","gamer420","nonyabusiness@gmail.com");
        RegistrationResult result = userService.register(registrationRequest);
        LogoutRequest logoutRequest = new LogoutRequest("SomethingStupid");
        assertThrows(UnauthorizedResponse.class, () -> userService.logout(logoutRequest));
    }

    @Test
    void clearUsersPass() throws DataAccessException {
        RegistrationRequest registrationRequest = new RegistrationRequest("Hyrum","gamer420","nonyabusiness@gmail.com");
        RegistrationRequest registrationRequest2 = new RegistrationRequest("Chris","Motorcycle","nonbusiness@gmail.com");
        RegistrationRequest registrationRequest3 = new RegistrationRequest("Eli","printerIhardlyknowher","yabusiness@gmail.com");
        userService.register(registrationRequest);
        userService.register(registrationRequest2);
        userService.register(registrationRequest3);
        userService.clearUsers();
        RegistrationResult result = userService.register(new RegistrationRequest("Hyrum","newgamer420","google.com"));
        assertEquals("Hyrum", result.username());
    }

}