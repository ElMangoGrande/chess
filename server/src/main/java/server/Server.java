package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryAuthDAO;
import dataaccess.MemoryGameDAO;
import dataaccess.MemoryUserDAO;
import io.javalin.*;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import model.*;
import org.eclipse.jetty.http.BadMessageException;
import service.AlreadyTakenException;
import service.GameService;
import service.UserService;
import chess.InvalidMoveException;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private static final Gson MY_GSON = new Gson();

    private static UserService user;
    private static GameService game;

    public Server() {
        //create daos
        MemoryUserDAO memoryUserDAO = new MemoryUserDAO();
        MemoryAuthDAO memoryAuthDAO = new MemoryAuthDAO();
        MemoryGameDAO memoryGameDAO = new MemoryGameDAO();

        //new Services
        user = new UserService(memoryUserDAO,memoryAuthDAO);
        game = new GameService(memoryGameDAO,memoryAuthDAO);

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
        String errorJson = MY_GSON.toJson(Map.of("message", e.getMessage()));
        ctx.status(errorCode).result(errorJson).contentType("application/json");
    }

    private static void handleRegister(Context ctx){
        try {
            RegistrationRequest request = MY_GSON.fromJson(ctx.body(), RegistrationRequest.class);
            RegistrationResult result = user.register(request);
            String resultJson = MY_GSON.toJson(result);
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
            LoginRequest request = MY_GSON.fromJson(ctx.body(), LoginRequest.class);
            LoginResult result = user.login(request);
            String resultJson = MY_GSON.toJson(result);
            ctx.status(200).result(resultJson).contentType("application/json");
        }
        catch (UnauthorizedResponse e) {
            displayError(e, 401, ctx);
        }
        catch (BadMessageException e) {
            displayError(e, 400, ctx);
        }
        catch (DataAccessException e) {
            displayError(e, 500, ctx);
        }
    }

    private static void handleLogout(Context ctx){
        try {
            LogoutRequest request = new LogoutRequest(ctx.header("authorization"));
            user.logout(request);
            ctx.status(200);
        }
        catch (UnauthorizedResponse e) {
            displayError(e, 401, ctx);
        }
        catch (DataAccessException e) {
            displayError(e, 500, ctx);
        }
    }

    private static void handleListGames(Context ctx){
        try {
            ListGamesRequest request = new ListGamesRequest(ctx.header("authorization"));
            ListGamesResult result = game.listGames(request);
            String resultJson = MY_GSON.toJson(result);
            ctx.status(200).result(resultJson).contentType("application/json");
        }
        catch (UnauthorizedResponse e) {
            displayError(e, 401, ctx);
        }
        catch (DataAccessException e) {
            displayError(e, 500, ctx);
        }
    }

    private static void handleCreateGame(Context ctx){
        try {
            CreateGameRequest body = MY_GSON.fromJson(ctx.body(), CreateGameRequest.class);
            CreateGameRequest request = new CreateGameRequest(ctx.header("authorization"),body.gameName());
            CreateGameResult result = game.createGame(request);
            String resultJson = MY_GSON.toJson(result);
            ctx.status(200).result(resultJson).contentType("application/json");
        }
        catch (UnauthorizedResponse e) {
            displayError(e, 401, ctx);
        }
        catch (BadMessageException e) {
            displayError(e, 400, ctx);
        }
        catch (DataAccessException e) {
            displayError(e, 500, ctx);
        }
    }

    private static void handleJoinGame(Context ctx){
        try {
            JoinGameRequest body = MY_GSON.fromJson(ctx.body(), JoinGameRequest.class);
            JoinGameRequest request = new JoinGameRequest(body.playerColor(),body.gameID(),ctx.header("authorization"));
            game.joinGame(request);
            ctx.status(200);
        }
        catch (UnauthorizedResponse e) {
            displayError(e, 401, ctx);
        }
        catch (BadMessageException e) {
            displayError(e, 400, ctx);
        }
        catch (AlreadyTakenException e) {
            displayError(e, 403, ctx);
        }
        catch (DataAccessException | InvalidMoveException e) {
            displayError(e, 500, ctx);
        }

    }

    private static void handleClear(Context ctx){
            game.clearGames();
            user.clearUsers();
            ctx.status(200);

    }


}
