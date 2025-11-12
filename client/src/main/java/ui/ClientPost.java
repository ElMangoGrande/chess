package ui;

import model.*;
import serverhandling.ResponseException;
import serverhandling.ServerFacade;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static java.lang.Integer.parseInt;
import static ui.EscapeSequences.*;

public class ClientPost {

    private final ServerFacade server;
    private String authToken;
    private final Map<Integer,Integer> mappyTheMap;
    private Boolean teamColor;

    public ClientPost(ServerFacade server) {
        this.server = server;
        mappyTheMap = new HashMap<>();
    }

    public String eval(String input, String auth) {
        authToken = auth;
        try {
            String[] tokens = input.toLowerCase().split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens,1,tokens.length);

            return switch (cmd) {
                case "create" -> create(params);
                case "list" -> list(params);
                case "join" -> join(params);
                case "observe" -> observe(params);
                case "logout" -> logout(params);
                case "quit" -> "quit";
                default -> postHelp();
            };
        } catch (ResponseException ex) {
            return ex.getMessage();
        }
    }
    public static String postHelp() {
        return SET_TEXT_COLOR_BLUE +"create <NAME>" + RESET_TEXT_COLOR + " - a game\n" +
                 SET_TEXT_COLOR_BLUE +"list" + RESET_TEXT_COLOR +" - games\n" +
                SET_TEXT_COLOR_BLUE + "join <ID> [WHITE|BLACK]" + RESET_TEXT_COLOR + " - a game\n"
                +SET_TEXT_COLOR_BLUE + "observe <ID>"+ RESET_TEXT_COLOR + " - a game\n"
                + SET_TEXT_COLOR_BLUE+"logout" + RESET_TEXT_COLOR+ " - When you are done\n"
                + SET_TEXT_COLOR_BLUE+"quit" + RESET_TEXT_COLOR+ " - playing chess\n" +
                SET_TEXT_COLOR_BLUE+"help" + RESET_TEXT_COLOR +" - lists possible commands\n";
    }

    private String create(String[] tokens) throws ResponseException {
        if (tokens.length != 1) {
            return "Usage: create <NAME>";
        }
        var req = new CreateGameRequest(authToken,tokens[0]);
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
                            "Game name : " + game.gameName() +"\n" +
                            "White user: " + game.whiteUsername() +"\n"
                            + "Black user: " + game.blackUsername() + "\n"

                    + "---------------------\n"

            );
            gameNum++;
        }
    }

    private String list(String[] tokens) throws ResponseException{
        if(tokens.length != 0){
            return "Usage: list";
        }
        var req = new ListGamesRequest(authToken);
        ListGamesResult res = server.listGames(req);
        Set<GameData> games = res.games();
        listHelper(games);
        return "Games have been listed";
    }

    private String join(String[] tokens) throws ResponseException {
        if (tokens.length != 2) {
            return "Usage: join <ID> [WHITE|BLACK]";
        }
        if(tokens[1].equalsIgnoreCase("WHITE")){
            teamColor = true;
        }else{
            teamColor = false;
        }
        int realID;
        if(tokens[0].matches("-?\\d+")){
            int gameID = parseInt(tokens[0]);
            try{
                realID= mappyTheMap.get(gameID);
            }catch(NullPointerException e){
                return "Error:invalid game ID";
            }
        }else{
            return "Error: GameID must be number";
        }

        var req = new JoinGameRequest(tokens[1].toUpperCase(),realID,authToken);
        server.joinGame(req);
        return "joined game";
    }

    private String observe(String[] tokens) throws ResponseException{
        teamColor = true;
        return "observe";
    }

    private String logout(String[] tokens) throws ResponseException{
        var req = new LogoutRequest(authToken);
        server.logout(req);
        return "logged out";
    }

    public Boolean getColor(){
        return teamColor;
    }

}

