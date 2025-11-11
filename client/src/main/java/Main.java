import chess.*;
import ui.REPL;

public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        REPL myRepl = new REPL("http://localhost:8080");
        myRepl.run();
    }
}