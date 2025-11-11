package ui;

import model.*;
import serverhandling.ResponseException;
import serverhandling.ServerFacade;

import static ui.EscapeSequences.*;

public class ClientPost {

    private final ServerFacade server;

    public ClientPost(ServerFacade server) {
        this.server = server;
    }

    public String eval(String input) {
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";

            return switch (cmd) {
                case "create" -> login(tokens);
                case "list" -> list(tokens);
                case "join" -> join(tokens);
                case "observe" -> observe(tokens);
                case "logout" -> logout(tokens);
                case "quit" -> "quit";
                default -> preHelp();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }
    public static String help() {
        return SET_TEXT_COLOR_BLUE +"create <NAME>" + RESET_TEXT_COLOR + "- a game\n" +
                 SET_TEXT_COLOR_BLUE +"list" + RESET_TEXT_COLOR +"- games\n" +
                SET_TEXT_COLOR_BLUE + "join <ID> [WHITE|BLACK]" + RESET_TEXT_COLOR + "- a game\n"
                +SET_TEXT_COLOR_BLUE + "observe <ID>"+ RESET_TEXT_COLOR + "- a game\n"
                + SET_TEXT_COLOR_BLUE+"logout" + RESET_TEXT_COLOR+ "When you are done\n"
                + SET_TEXT_COLOR_BLUE+"quit" + RESET_TEXT_COLOR+ " - playing chess\n" +
                SET_TEXT_COLOR_BLUE+"help" + RESET_TEXT_COLOR +"lists possible commands\n";
    }
    private String login(String[] tokens) throws ResponseException {
        if (tokens.length < 2) {
            return "Usage: login <username> <password>";
        }
        var req = new LoginRequest(tokens[1], tokens[2]);
        LoginResult res = server.login(req);
        return "Logged in: " + res.username();
    }


}

