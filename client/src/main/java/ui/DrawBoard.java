package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static ui.EscapeSequences.*;

public class DrawBoard {

    private static final List<ChessPosition> highlighted = new ArrayList<>();
    private static ChessPosition start;


    public static void drawBoard(boolean whitePerspective,ChessPiece[][] tiles) {
        System.out.print(RESET_TEXT_COLOR);
        System.out.print(ERASE_SCREEN);
        System.out.println();
        String whiteLabels = "     a  b  c   d   e   f  g  h";
        String blackLabels = "     h  g  f   e   d   c  b  a";

        if (whitePerspective) {
            System.out.println(whiteLabels);
            // FIX: Start at row 7 (Internal Rank 8) and count DOWN to 0
            for (int row = 7; row >= 0; row--) {
                System.out.print(" " + (row + 1) + " ");
                for (int col = 0; col < 8; col++) {
                    boolean isLight = (row + col) % 2 != 0;
                    // We pass (row + 1) to match the 1-8 coordinate system
                    printTile(tiles[row][col], isLight, new ChessPosition(row + 1, col + 1));
                }
                System.out.println(" " + (row + 1));
            }
            System.out.println(whiteLabels);
        } else {
            // Black Perspective (Rotated 180 degrees)
            System.out.println(blackLabels);
            // FIX: Start at row 0 (Internal Rank 1) and count UP to 7
            // For Black, Rank 1 is visually at the TOP.
            for (int row = 0; row < 8; row++) {
                System.out.print(" " + (row + 1) + " ");
                for (int col = 7; col >= 0; col--) {
                    boolean isLight = (row + col) % 2 != 0;
                    printTile(tiles[row][col], isLight, new ChessPosition(row + 1, col + 1));
                }
                System.out.println(" " + (row + 1));
            }
            System.out.println(blackLabels);
        }
        start= null;
        highlighted.clear();
    }

    private static void printTile(ChessPiece piece, boolean isLightSquare, ChessPosition position) {
        String bgColor = isLightSquare ? SET_BG_COLOR_LIGHT_GREY : SET_BG_COLOR_DARK_GREY;
        if(position.equals(start)){
            bgColor = SET_BG_COLOR_YELLOW;
        }else if(highlighted.contains(position)){
            bgColor = isLightSquare ? SET_BG_COLOR_MAGENTA : SET_BG_COLOR_BLUE;
        }
        String pieceStr;
        if (piece == null) {
            pieceStr = EMPTY;
        } else {
            pieceStr = getPieceSymbol(piece);
        }

        System.out.print(bgColor + pieceStr + RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    static void highlightSquares(ChessGame game, ChessPosition position){
        start = position;
        if (position == null) {
            System.out.println("Error: no position chosen");
            return;
        }
        if(game.getBoard().getPiece(start) == null){
            System.out.println("Error: you chose an empty square");
            start = null;
            return;
        }
        Collection<ChessMove> goodMoves = game.validMoves(position);
        for( ChessMove move : goodMoves){
            highlighted.add(move.getEndPosition());
        }
    }


    private static String getPieceSymbol(ChessPiece piece) {
        boolean isWhite = piece.getTeamColor() == ChessGame.TeamColor.WHITE;
        ChessPiece.PieceType type = piece.getPieceType();

        if (!isWhite) {
            return switch (type) {
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
                case ROOK -> WHITE_ROOK;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case PAWN -> WHITE_PAWN;
            };
        } else {
            return switch (type) {
                case KING -> BLACK_KING;
                case QUEEN -> BLACK_QUEEN;
                case ROOK -> BLACK_ROOK;
                case BISHOP -> BLACK_BISHOP;
                case KNIGHT -> BLACK_KNIGHT;
                case PAWN -> BLACK_PAWN;
            };
        }

    }
}
