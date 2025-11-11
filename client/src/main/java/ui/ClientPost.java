package ui;

import model.*;
import serverhandling.ResponseException;
import serverhandling.ServerFacade;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.lang.Integer.parseInt;
import static ui.EscapeSequences.*;

public class ClientPost {

    private final ServerFacade server;
    private String authToken;
    private final Map<Integer,Integer> mappyTheMap;

    public ClientPost(ServerFacade server) {
        this.server = server;
        mappyTheMap = new HashMap<>();
    }

    public String eval(String input, String auth) {
        authToken = auth;
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";

            return switch (cmd) {
                case "create" -> create(tokens);
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

    private String create(String[] tokens) throws ResponseException {
        if (tokens.length < 2) {
            return "Usage: create <NAME>";
        }
        var req = new CreateGameRequest(authToken,tokens[1]);
        CreateGameResult res = server.createGame(req);
        return "created game" + res.gameID();
    }

    private void listHelper(Set<GameData> games){
        mappyTheMap.clear();
        int gameNum = 1;
        for( GameData game : games){
            mappyTheMap.put(gameNum,game.gameID());
            System.out.println(
                    "---------------------\n"+
                    "Game" + gameNum +"\n" +
                            "Game name" + game.gameName() +"\n" +
                            "White user: " + game.whiteUsername() +"\n"
                            + "Black user: " + game.blackUsername() + "\n"

                    + "---------------------\n"

            );
            gameNum++;
        }
    }

    private String list(String[] tokens) throws ResponseException{
        var req = new ListGamesRequest(authToken);
        ListGamesResult res = server.listGames(req);
        Set<GameData> games = res.games();
        listHelper(games);
        return "Games have been listed";
    }

    private String join(String[] tokens) throws ResponseException {
        if (tokens.length < 2) {
            return "Usage: join <ID> [WHITE|BLACK]";
        }
        var req = new JoinGameRequest(tokens[2].toUpperCase(),parseInt(tokens[1]),authToken);
        server.joinGame(req);
        return "joined game";
    }

}

