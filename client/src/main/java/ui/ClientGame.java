package ui;

import serverhandling.ResponseException;
import serverhandling.ServerFacade;
import websocket.WebsocketFacade;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class ClientGame {

    private final ServerFacade server;
    private WebsocketFacade ws;
    private String authToken;
    private int gameID;
    private boolean whitePerspective;

    public ClientGame(ServerFacade server) {
        this.server = server;
    }

    public void attachWebSocket(WebsocketFacade ws, String auth, int id, boolean white) {
        this.ws = ws;
        this.authToken = auth;
        this.gameID = id;
        this.whitePerspective = white;
    }

    public String eval(String input) {
            String[] tokens = input.split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            cmd = cmd.toLowerCase();
            return switch (cmd) {
                //case "exit" -> "exit";
                //case "quit" -> "quit";
                case "leave" -> leave();
                case "make move" -> makeMove();
                case "highlight" -> legalMoves();
                case "resign" -> resign();
                default -> gameHelp();
            };
    }

    public String leave(){
        try {
            ws.leaveGame(authToken, gameID);
            return "left game";
        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }

    public String resign() {
        try {
            ws.resignGame(authToken, gameID);
            return "You have resigned.\n";
        } catch (ResponseException e) {
            return "Error: " + e.getMessage();
        }
    }

    public String gameHelp() {
        return SET_TEXT_COLOR_BLUE +"exit"
                + RESET_TEXT_COLOR + " - to return to logged in\n" +
                SET_TEXT_COLOR_BLUE +"quit"
                + RESET_TEXT_COLOR +" - to leave chess\n"
                + SET_TEXT_COLOR_BLUE+"help" + RESET_TEXT_COLOR +"lists possible commands\n";
    }

}
