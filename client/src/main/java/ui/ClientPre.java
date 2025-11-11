package ui;

import java.util.Arrays;

import model.RegistrationRequest;
import model.RegistrationResult;
import serverhandling.ResponseException;
import serverhandling.ServerFacade;

public class ClientPre {

    private final ServerFacade server;

    public ClientPre(ServerFacade server) {
        this.server = server;
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";

            return switch (cmd) {
                case "login" -> login(params);
                case "register" -> register(params);
                case "quit" -> "quit";
                default -> preHelp();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public static String preHelp() {
        return """
                - register <USERNAME> <PASSWORD> <EMAIL>
                - login <USERNAME> <PASSWORD>
                - quit
                -help
                """;
    }

    private String register(String[] tokens) throws ResponseException {
        if (tokens.length < 3) {
            return "Usage: register <username> <password>";
        }
        var req = new RegistrationRequest(tokens[1], tokens[2]);
        RegistrationResult res = server.register(req);
        return "Registered new user: " + res.username();
    }

    private String login(String[] tokens) throws ResponseException{
        if(tokens.length)
    }

}
