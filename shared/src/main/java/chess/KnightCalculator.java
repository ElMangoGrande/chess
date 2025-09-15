package chess;

import java.util.ArrayList;
import java.util.List;

public class KnightCalculator {
    private final List<ChessMove> possibleMoves;
    private final ChessPosition startPosition;
    private final ChessBoard board;

    public KnightCalculator(ChessBoard board, ChessPosition startPosition) {
        this.possibleMoves = new ArrayList<>();
        this.board = board;
        this.startPosition = startPosition;
    }

    public boolean Checkmove(int row,int col){
        if(col >8 || col < 1 || row> 8 || row < 1){
            return false;
        }
        ChessPosition newPosition = new ChessPosition(row, col);
        ChessPiece target = board.getPiece(newPosition);
        if(target == null){
            return true;
        }
        if(target.getTeamColor() == board.getPiece(startPosition).getTeamColor()){
            return false;
        }
        return true;
    }

    public List<ChessMove> calculateMoves(){
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();
        if(Checkmove(startRow + 2, startCol + 1)) {
            ChessPosition newPosition = new ChessPosition(startRow + 2, startCol + 1);
            possibleMoves.add(new ChessMove(startPosition, newPosition, null));
        }
        if(Checkmove(startRow + 2, startCol - 1)) {
            ChessPosition newPosition = new ChessPosition(startRow + 2, startCol - 1);
            possibleMoves.add(new ChessMove(startPosition, newPosition, null));
        }
        if(Checkmove(startRow + 1, startCol + 2)) {
            ChessPosition newPosition = new ChessPosition(startRow + 1, startCol + 2);
            possibleMoves.add(new ChessMove(startPosition, newPosition, null));
        }
        if(Checkmove(startRow + 1, startCol - 2)) {
            ChessPosition newPosition = new ChessPosition(startRow + 1, startCol - 2);
            possibleMoves.add(new ChessMove(startPosition, newPosition, null));
        }
        if(Checkmove(startRow - 2, startCol + 1)) {
            ChessPosition newPosition = new ChessPosition(startRow - 2, startCol + 1);
            possibleMoves.add(new ChessMove(startPosition, newPosition, null));
        }
        if(Checkmove(startRow - 2, startCol - 1)) {
            ChessPosition newPosition = new ChessPosition(startRow - 2, startCol - 1);
            possibleMoves.add(new ChessMove(startPosition, newPosition, null));
        }
        if(Checkmove(startRow - 1, startCol + 2)) {
            ChessPosition newPosition = new ChessPosition(startRow - 1, startCol + 2);
            possibleMoves.add(new ChessMove(startPosition, newPosition, null));
        }
        if(Checkmove(startRow - 1, startCol - 2)) {
            ChessPosition newPosition = new ChessPosition(startRow - 1, startCol - 2);
            possibleMoves.add(new ChessMove(startPosition, newPosition, null));
        }
        return possibleMoves;
    }
}
