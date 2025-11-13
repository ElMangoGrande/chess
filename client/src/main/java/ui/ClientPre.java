package ui;

import java.util.Arrays;

import model.*;
import serverhandling.ResponseException;
import serverhandling.ServerFacade;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class ClientPre {

    private final ServerFacade server;
    private String authToken;

    public ClientPre(ServerFacade server) {
        this.server = server;
    }

    public String eval(String input) {
        try {
            String[] tokens = input.split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            cmd = cmd.toLowerCase();
            return switch (cmd) {
                case "login" -> login(tokens);
                case "register" -> register(tokens);
                case "quit" -> "quit";
                default -> preHelp();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }

    public static String preHelp() {
        return SET_TEXT_COLOR_BLUE +"register <USERNAME> <PASSWORD> <EMAIL>"
                + RESET_TEXT_COLOR + " - to create an account\n" +
                SET_TEXT_COLOR_BLUE +"login <USERNAME> <PASSWORD>"
                + RESET_TEXT_COLOR +" - to play chess\n"
                + SET_TEXT_COLOR_BLUE+"quit" + RESET_TEXT_COLOR+ " - playing chess\n" +
                SET_TEXT_COLOR_BLUE+"help" + RESET_TEXT_COLOR +" - lists possible commands\n";
    }

    private String register(String[] tokens) throws ResponseException {
        if (tokens.length < 3) {
            return "Usage: register <username> <password> <email>";
        }
        var req = new RegistrationRequest(tokens[1], tokens[2], tokens[3]);
        RegistrationResult res = server.register(req);
        authToken = res.authToken();
        return "registered";
    }

    private String login(String[] tokens) throws ResponseException{
        if(tokens.length <3){
            return "Usage: login <username> <password>";
        }
        var req = new LoginRequest(tokens[1], tokens[2]);
        LoginResult res = server.login(req);
        authToken = res.authToken();
        return "login successful";
    }

    public String getAuthToken() {
        return authToken;
    }



}
