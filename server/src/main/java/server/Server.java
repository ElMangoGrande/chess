package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import io.javalin.*;
import io.javalin.http.Context;
import model.RegistrationRequest;
import model.RegistrationResult;
import service.AlreadyTakenException;
import service.GameService;
import service.UserService;

import java.util.Map;

public class Server {

    private final Javalin javalin;
    private static final Gson SERIALIZER = new Gson();

//    private static final UserService userService = new UserService();
//    private static final GameService gameService = new GameService();

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // Register your endpoints and exception handlers here.
        // === Register all endpoints ===
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

    private static void displayErrorMessage(String message, int errorCode, Context ctx) {
        String errorJson = new Gson().toJson(Map.of("message", message));
        ctx.status(errorCode).result(errorJson).contentType("application/json");
    }

    private static void handleRegister(Context ctx){
        try {
            RegistrationRequest request = SERIALIZER.fromJson(ctx.body(), RegistrationRequest.class);
            RegistrationResult result = UserService.register(request);
            String resultJson = new Gson().toJson(result);
            ctx.status(200).result(resultJson).contentType("application/json");
        }
        catch (AlreadyTakenException e) {
            displayErrorMessage("Error: already taken", 403, ctx);
        }
//        catch (BadMessageException e) {
//            displayErrorMessage("Error: bad request", 400, ctx);
//        }
        catch (DataAccessException e) {
            displayErrorMessage("Error: " + e.getMessage(), 500, ctx);
        }
    }




}
