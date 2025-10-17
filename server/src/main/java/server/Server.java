package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.*;
import io.javalin.http.Context;
import model.LoginRequest;
import model.LoginResult;
import model.RegistrationRequest;
import model.RegistrationResult;
import org.eclipse.jetty.http.BadMessageException;
import service.AlreadyTakenException;
import service.GameService;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private static final Gson gson = new Gson();

    private static UserService userService;
    private static GameService gameService;

    public Server() {
        //create daos
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();

        //new Services
        userService = new UserService(memoryUserDAO,memoryAuthDAO);
        gameService = new GameService(memoryGameDAO,memoryAuthDAO);

        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        javalin.delete("/db", Server::handleClear);
        javalin.post("/user", Server::handleRegister);
        javalin.post("/session", Server::handleLogin);
        javalin.delete("/session", Server::handleLogout);
        javalin.get("/game", Server::handleListGames);
        javalin.post("/game", Server::handleCreateGame);
        javalin.put("/game", Server::handleJoinGame);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private static void displayError(Exception e, int errorCode, Context ctx) {
        String errorJson = gson.toJson(Map.of("message", e.getMessage()));
        ctx.status(errorCode).result(errorJson).contentType("application/json");
    }

    private static void handleRegister(Context ctx){
        try {
            RegistrationRequest request = gson.fromJson(ctx.body(), RegistrationRequest.class);
            RegistrationResult result = UserService.register(request);
            String resultJson = new Gson().toJson(result);
            ctx.status(200).result(resultJson).contentType("application/json");
        }
        catch (AlreadyTakenException e) {
            displayError(e, 403, ctx);
        }
        catch (BadMessageException e) {
            displayError(e, 400, ctx);
        }
        catch (DataAccessException e) {
            displayError(e, 500, ctx);
        }
    }

    private static void handleLogin(Context ctx){
        try {
            LoginRequest request = gson.fromJson(ctx.body(), LoginRequest.class);
            LoginResult result = UserService.login(request);
            String resultJson = new Gson().toJson(result);
            ctx.status(200).result(resultJson).contentType("application/json");
        }
        catch (AlreadyTakenException e) {
            displayError(e, 403, ctx);
        }
        catch (BadMessageException e) {
            displayError(e, 400, ctx);
        }
        catch (DataAccessException e) {
            displayError(e, 500, ctx);
        }
    }






}
