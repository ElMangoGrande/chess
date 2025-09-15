package chess;

import java.util.ArrayList;
import java.util.List;

public class QueenCalculator {
    private final List<ChessMove> possibleMoves;
    private final ChessPosition startPosition;
    private final ChessBoard board;

    public QueenCalculator(ChessBoard board, ChessPosition startPosition) {
        this.possibleMoves = new ArrayList<>();
        this.board = board;
        this.startPosition = startPosition;
    }

    public List<ChessMove> calculateMoves(){
        int startRow = this.startPosition.getRow();
        int startCol = this.startPosition.getColumn();

        //upper right Diagonal path
        int row = startRow + 1;
        int col = startCol + 1;
        while(row < 9 && col < 9){
            ChessPosition newPosition = new ChessPosition(row,col);
            ChessPiece target = board.getPiece(newPosition);
            if (target == null) {
                possibleMoves.add(new ChessMove(startPosition, newPosition, null));
            } else if (target.getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
                possibleMoves.add(new ChessMove(startPosition, newPosition, null));
                break;
            } else {
                break;
            }
            row++;
            col++;
        }
        //upper left diagonal path
        row = startRow + 1;
        col = startCol - 1;
        while(row < 9 && col > 0){
            ChessPosition newPosition = new ChessPosition(row,col);
            ChessPiece target = board.getPiece(newPosition);
            if (target == null) {
                possibleMoves.add(new ChessMove(startPosition, newPosition, null));
            } else if (target.getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
                possibleMoves.add(new ChessMove(startPosition, newPosition, null));
                break;
            } else {
                break;
            }
            row++;
            col--;
        }
        //lower right diagonal path
        row = startRow - 1;
        col = startCol + 1;
        while(row > 0 && col < 9){
            ChessPosition newPosition = new ChessPosition(row,col);
            ChessPiece target = board.getPiece(newPosition);
            if (target == null) {
                possibleMoves.add(new ChessMove(startPosition, newPosition, null));
            } else if (target.getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
                possibleMoves.add(new ChessMove(startPosition, newPosition, null));
                break;
            } else {
                break;
            }
            row--;
            col++;
        }
        //lower left diagonal path
        row = startRow - 1;
        col = startCol - 1;
        while(row > 0 && col > 0){
            ChessPosition newPosition = new ChessPosition(row,col);
            ChessPiece target = board.getPiece(newPosition);
            if (target == null) {
                possibleMoves.add(new ChessMove(startPosition, newPosition, null));
            } else if (target.getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
                possibleMoves.add(new ChessMove(startPosition, newPosition, null));
                break;
            } else {
                break;
            }
            row--;
            col--;
        }

        //up path
        row = startRow + 1;
        col = startCol;
        while(row < 9){
            ChessPosition newPosition = new ChessPosition(row,col);
            ChessPiece target = board.getPiece(newPosition);
            if (target == null) {
                possibleMoves.add(new ChessMove(startPosition, newPosition, null));
            } else if (target.getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
                possibleMoves.add(new ChessMove(startPosition, newPosition, null));
                break;
            } else {
                break;
            }
            row++;
        }
        //down path
        row = startRow - 1;
        while(row > 0){
            ChessPosition newPosition = new ChessPosition(row,col);
            ChessPiece target = board.getPiece(newPosition);
            if (target == null) {
                possibleMoves.add(new ChessMove(startPosition, newPosition, null));
            } else if (target.getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
                possibleMoves.add(new ChessMove(startPosition, newPosition, null));
                break;
            } else {
                break;
            }
            row--;
        }
        //left path
        row = startRow;
        col = startCol -1 ;
        while(col > 0){
            ChessPosition newPosition = new ChessPosition(row,col);
            ChessPiece target = board.getPiece(newPosition);
            if (target == null) {
                possibleMoves.add(new ChessMove(startPosition, newPosition, null));
            } else if (target.getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
                possibleMoves.add(new ChessMove(startPosition, newPosition, null));
                break;
            } else {
                break;
            }
            col--;
        }
        //right path
        row = startRow;
        col = startCol +1 ;
        while(col < 9){
            ChessPosition newPosition = new ChessPosition(row,col);
            ChessPiece target = board.getPiece(newPosition);
            if (target == null) {
                possibleMoves.add(new ChessMove(startPosition, newPosition, null));
            } else if (target.getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
                possibleMoves.add(new ChessMove(startPosition, newPosition, null));
                break;
            } else {
                break;
            }
            col++;
        }

        return possibleMoves;
    }

}
