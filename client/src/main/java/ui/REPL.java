package ui;

import java.util.Locale;
import java.util.Objects;
import java.util.Scanner;

import static ui.ClientPost.postHelp;
import static ui.ClientPre.preHelp;
import static ui.DrawBoard.drawBoard;
import static ui.EscapeSequences.*;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import serverhandling.ResponseException;
import serverhandling.ServerFacade;
import ui.*;
import websocket.WebsocketFacade;

public class REPL {

    private ClientGame game;
    private ClientPost post;
    private ClientPre pre;
    private final ServerFacade server;
    private WebsocketFacade ws;


    private enum State { PRELOGIN, POSTLOGIN, INGAME }
    private State state = State.PRELOGIN;
    private String authToken;
    private Boolean teamColor;
    private ChessGame currentGame;
    private int currentGameID;
    private String currentColor;
    private String serverUrl;
    private Scanner scanner;

    public REPL(String serverUrl){
        this.server = new ServerFacade(serverUrl);
        game = new ClientGame(server);
        post = new ClientPost(server);
        pre = new ClientPre(server);
        this.serverUrl = serverUrl;

    }

    private void printPrompt() {
        System.out.print("\n" + RESET_TEXT_COLOR + ">>> " + SET_TEXT_COLOR_BLUE);
    }

    public void run(){
        System.out.println("Welcome to Chess. Type help or else");
        System.out.print(preHelp());

        this.scanner = new Scanner(System.in);
        var result = "";

        while(!result.equals("quit")){
            printPrompt();
            String line = scanner.nextLine();

            try {
                result = eval(line);
                System.out.print(SET_TEXT_COLOR_WHITE + result);
            } catch (ResponseException e) {
                System.out.print(SET_TEXT_COLOR_RED + e.getMessage());
            } catch (Throwable e) {
                System.out.print(SET_TEXT_COLOR_RED + "Error: " + e.getMessage());
            }

        }
    }

    public String eval(String input) throws Exception {
        if (input.equalsIgnoreCase("quit")) {
            return "quit";
        }
        input = input.trim();
        if(input.isEmpty()){
            return "";
        }

        String result;

        //switch state
        switch(state){
            case PRELOGIN -> {
                result = pre.eval(input);
                if(result.equals("registered") || result.equals("login successful")){
                    authToken = pre.getAuthToken();
                }
            }
            case POSTLOGIN -> result = post.eval(input,authToken);
            case INGAME -> result = game.eval(input);
            default -> result = "Invalid state.";
        }

        //switch for result
        switch(result.toLowerCase()){
            case "login successful" ->{
                state = State.POSTLOGIN;
                return "login successful.\n" + postHelp();
            }
            case "registered" -> {
                state = State.POSTLOGIN;
                return "Registration successful.\n" + postHelp();
            }
            case "logged out"->{
                state = State.PRELOGIN;
                return "logged out. \n" + preHelp();
            }
            case "observe", "joined game"->{
                state = State.INGAME;
                teamColor = post.isWhitePerspective();
                currentGameID = post.getGameID();
                currentColor = post.getColor();
                if(ws == null){
                    ws = new WebsocketFacade(this, this.serverUrl);
                }
                ws.connect(authToken,currentGameID,currentColor);
                //drawBoard(teamColor,new ChessGame().getBoard().getTiles());
                return "entering game...\n" + game.gameHelp();
            }
            case "leave" ->{
                state = State.POSTLOGIN;
                ws.leaveGame(authToken,currentGameID);
                return "Leaving Game\n" + postHelp();
            }
            case "resign" ->{
                System.out.println("Are you sure you want to resign?");
                if(Objects.equals(scanner.nextLine().toLowerCase(), "yes")){
                    ws.resignGame(authToken,currentGameID);
                    return "You have resigned.";
                }
                return "You have not resigned\n" + game.gameHelp();
            }
            case "move" ->{
                ChessMove move = game.getMoveToMake();
                move = new ChessMove(move.getStartPosition(),move.getEndPosition(),promotionHelper(move));
                ws.makeMove(authToken,currentGameID,move);
                return "move made!";
            }

            default ->{
                return result;
            }

        }
    }
    
    public ChessPiece.PieceType promotionHelper(ChessMove move){
        if(currentGame.getBoard().getPiece(move.getStartPosition()).getTeamColor() == ChessGame.TeamColor.WHITE &&
                move.getEndPosition().getRow()== 8 || currentGame.getBoard().getPiece(move.getStartPosition()).getTeamColor()
                == ChessGame.TeamColor.BLACK && move.getEndPosition().getRow()== 1){
            switch(scanner.nextLine().toLowerCase()){
                case "queen" -> {return ChessPiece.PieceType.QUEEN;}
                case "knight" -> {return ChessPiece.PieceType.KNIGHT;}
                case "bishop" -> {return ChessPiece.PieceType.BISHOP;}
                case "rook" -> {return ChessPiece.PieceType.ROOK;}
            }
        }
        return null;
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    public void updateGame(ChessGame game) {
        this.currentGame = game;
    }

    public void renderBoard() {
        ChessBoard board = currentGame.getBoard();
        drawBoard(teamColor, board.getTiles());
    }

}
