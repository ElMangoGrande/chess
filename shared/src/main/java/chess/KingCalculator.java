package chess;

import java.util.ArrayList;
import java.util.List;

public class KingCalculator {
    private final List<ChessMove> possibleMoves;
    private final ChessPosition startPosition;
    private final ChessBoard board;

    public KingCalculator(ChessBoard board, ChessPosition startPosition) {
        this.possibleMoves = new ArrayList<>();
        this.startPosition = startPosition;
        this.board = board;
    }

    public List<ChessMove> calculateMoves() {
        int startRow = startPosition.getRow();
        int startCol = startPosition.getColumn();
        int row = startRow + 1;
        while (row > startRow - 2) {
            int col = startCol + 1;
            while (col > startCol - 2) {
                if(col >8 || col <= 0 | row> 8 | row<=0){
                    col--;
                    continue;
                }
                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);
                if (target == null) {
                    possibleMoves.add(new ChessMove(startPosition, newPosition, null));
                } else if (target.getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
                    possibleMoves.add(new ChessMove(startPosition, newPosition, null));
                }else if(board.getPiece(newPosition).getPieceType()== board.getPiece(startPosition).getPieceType()){
                    col--;
                    continue;
                }
                col--;
            }
            row--;
        }
        return possibleMoves;
    }
}
