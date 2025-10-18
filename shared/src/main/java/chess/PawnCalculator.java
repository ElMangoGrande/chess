package chess;

import java.util.ArrayList;
import java.util.List;

public class PawnCalculator {
    private final List<ChessMove> possibleMoves;
    private final ChessPosition startPosition;
    private final ChessBoard board;

    public PawnCalculator(ChessBoard board, ChessPosition startPosition) {
        this.possibleMoves = new ArrayList<>();
        this.board = board;
        this.startPosition = startPosition;
    }

    public boolean inBounds(int row, int col){
        if(row > 8 || row < 1 || col > 8 || col < 1){
            return false;
        }
        return true;
    }

    public void promoteMe(ChessPosition newPosition){
        possibleMoves.add(new ChessMove(startPosition,newPosition, ChessPiece.PieceType.KNIGHT));
        possibleMoves.add(new ChessMove(startPosition,newPosition, ChessPiece.PieceType.QUEEN));
        possibleMoves.add(new ChessMove(startPosition,newPosition, ChessPiece.PieceType.ROOK));
        possibleMoves.add(new ChessMove(startPosition,newPosition, ChessPiece.PieceType.BISHOP));

    }

    private void tryCaptureMove(int row, int col, int promotionRow) {
        if (inBounds(row, col)) {
            ChessPosition newPosition = new ChessPosition(row, col);
            ChessPiece target = board.getPiece(newPosition);
            ChessPiece current = board.getPiece(startPosition);

            if (target != null && target.getTeamColor() != current.getTeamColor()) {
                if (row == promotionRow) {
                    promoteMe(newPosition);
                } else {
                    possibleMoves.add(new ChessMove(startPosition, newPosition, null));
                }
            }
        }
    }


    public List<ChessMove> calculateMoves(){
        int startRow = this.startPosition.getRow();
        int startCol = this.startPosition.getColumn();

        //front move(considers starting location)
        //White Team
        if(board.getPiece(startPosition).getTeamColor() == ChessGame.TeamColor.WHITE){
            //checks for starting positon
            if(startRow == 2){
                int row = startRow + 2;
                int col = startCol;
                ChessPosition newPosition = new ChessPosition(row,col);
                ChessPiece target = board.getPiece(newPosition);
                if(target == null){
                    if(board.getPiece(new ChessPosition(row -1, col))==null) {
                        possibleMoves.add(new ChessMove(startPosition, newPosition, null));
                    }
                }
            }
            //check forward move
            int row = startRow + 1;
            int col = startCol;
            if(inBounds(row,col)) {
                ChessPosition newPosition = new ChessPosition(row,col);
                ChessPiece target = board.getPiece(newPosition);
                if (target == null) {
                    if(row == 8){
                        promoteMe(newPosition);
                    }else {
                        possibleMoves.add(new ChessMove(startPosition, newPosition, null));
                    }
                }
            }
           //check right white capture
            tryCaptureMove(startRow + 1, startCol + 1, 8); // right capture
            tryCaptureMove(startRow + 1, startCol - 1, 8); // left capture



        }
        //Black Team
        if(board.getPiece(startPosition).getTeamColor() == ChessGame.TeamColor.BLACK){
            if(startRow == 7){
                int row = startRow - 2;
                int col = startCol;
                ChessPosition newPosition = new ChessPosition(row,col);
                ChessPiece target = board.getPiece(newPosition);
                if(target == null){
                    if(board.getPiece(new ChessPosition(row +1, col))==null) {
                        possibleMoves.add(new ChessMove(startPosition, newPosition, null));
                    }
                }
            }
            //check forward move
            int row = startRow - 1;
            int col = startCol;
            if(inBounds(row,col)) {
                ChessPosition newPosition = new ChessPosition(row,col);
                ChessPiece target = board.getPiece(newPosition);
                if (target == null) {
                    if(row == 1){
                        promoteMe(newPosition);
                    }else {
                        possibleMoves.add(new ChessMove(startPosition, newPosition, null));
                    }
                }
            }
            //check right black capture
            tryCaptureMove(startRow - 1, startCol + 1, 1); // right capture
            tryCaptureMove(startRow - 1, startCol - 1, 1); // left capture

        }

        //promotion(if needed)
        return possibleMoves;
    }
}
