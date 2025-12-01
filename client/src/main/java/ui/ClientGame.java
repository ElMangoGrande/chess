package ui;

import chess.ChessMove;
import chess.ChessPosition;
import serverhandling.ServerFacade;

import java.util.Arrays;

import static ui.EscapeSequences.RESET_TEXT_COLOR;
import static ui.EscapeSequences.SET_TEXT_COLOR_BLUE;

public class ClientGame {

    private final ServerFacade server;
    private ChessMove moveToMake;


    public ClientGame(ServerFacade server) {
        this.server = server;
    }



    public String eval(String input) {
            String[] tokens = input.split(" ");
            String cmd = (tokens.length > 0) ? tokens[0] : "help";
            String[] params = Arrays.copyOfRange(tokens,1,tokens.length);
            cmd = cmd.toLowerCase();
            return switch (cmd) {
                //case "exit" -> "exit";
                //case "quit" -> "quit";
                case "leave" -> leave(params);
                case "make move" -> makeMove();
                case "highlight" -> legalMoves();
                case "resign" -> resign(params);
                default -> gameHelp();
            };
    }

    public String leave(String[] params){
        if(params.length != 0){
            return "Usage: leave";
        }
        return "leave";
    }

    public String resign(String[] params) {
        if(params.length != 0){
            return "Usage: resign";
        }
        return "resign";
    }

    public String makeMove(String[] params){
        //parse moves
        if(params.length !=2){
            return "usage: makeMove a2 b3";
        }
        if(params[0].length() !=2 || params[1].length() != 2){
            return "usage: makeMove a2 b3";
        }
        if(params[0].matches("^[a-h][1-8]$") && params[1].matches("^[a-h][1-8]$")){
            ChessPosition start = new ChessPosition(params[0].charAt(1)-'0',(params[0].charAt(0)-'a')+1);
            ChessPosition finish = new ChessPosition(params[1].charAt(1)-'0',(params[1].charAt(0)-'a')+1);
            moveToMake = new ChessMove(start,finish,null);
            return "move";
        }
        return "usage: makeMove a2 b3";
    }

    public String legalMoves(){

    }

    public ChessMove getMoveToMake() {
        return moveToMake;
    }

    public String gameHelp() {
        return SET_TEXT_COLOR_BLUE +"exit"
                + RESET_TEXT_COLOR + " - to return to logged in\n" +
                SET_TEXT_COLOR_BLUE +"quit"
                + RESET_TEXT_COLOR +" - to leave chess\n"
                + SET_TEXT_COLOR_BLUE+"help" + RESET_TEXT_COLOR +"lists possible commands\n";
    }

}
