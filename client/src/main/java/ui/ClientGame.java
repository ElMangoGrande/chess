package ui;

import serverhandling.ResponseException;
import serverhandling.ServerFacade;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class ClientGame {

    private final ServerFacade server;

    public ClientGame(ServerFacade server) {
        this.server = server;
    }

    public String eval(String input) {
            String[] tokens = input.split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            cmd = cmd.toLowerCase();
            return switch (cmd) {
                case "exit" -> "exit";
                case "quit" -> "quit";
                default -> gameHelp();
            };
    }

    public static String gameHelp() {
        return SET_TEXT_COLOR_BLUE +"exit"
                + RESET_TEXT_COLOR + " - to return to logged in\n" +
                SET_TEXT_COLOR_BLUE +"quit"
                + RESET_TEXT_COLOR +" - to leave chess\n"
                + SET_TEXT_COLOR_BLUE+"help" + RESET_TEXT_COLOR +"lists possible commands\n";
    }

}
