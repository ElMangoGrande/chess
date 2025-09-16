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

    public boolean InBounds(int row, int col){
        if(row > 8 || row < 1 || col > 8 || col < 1){
            return false;
        }
        return true;
    }

    public void PromoteMe(ChessPosition newPosition){
        possibleMoves.add(new ChessMove(startPosition,newPosition, ChessPiece.PieceType.KNIGHT));
        possibleMoves.add(new ChessMove(startPosition,newPosition, ChessPiece.PieceType.QUEEN));
        possibleMoves.add(new ChessMove(startPosition,newPosition, ChessPiece.PieceType.ROOK));
        possibleMoves.add(new ChessMove(startPosition,newPosition, ChessPiece.PieceType.BISHOP));

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
            if(InBounds(row,col)) {
                ChessPosition newPosition = new ChessPosition(row,col);
                ChessPiece target = board.getPiece(newPosition);
                if (target == null) {
                    if(row == 8){
                        PromoteMe(newPosition);
                    }else {
                        possibleMoves.add(new ChessMove(startPosition, newPosition, null));
                    }
                }
            }
           //check right white capture

           row = startRow + 1;
           col = startCol + 1;
           if(InBounds(row,col)) {
               ChessPosition newPosition = new ChessPosition(row,col);
               ChessPiece target = board.getPiece(newPosition);
               if (target != null) {
                   if (target.getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
                       if(row == 8){
                           PromoteMe(newPosition);
                       }else {
                           possibleMoves.add(new ChessMove(startPosition, newPosition, null));
                       }
                   }
               }
           }
           //check left white capture
           row = startRow + 1;
           col = startCol - 1;
           if(InBounds(row,col)) {
               ChessPosition newPosition = new ChessPosition(row,col);
               ChessPiece target = board.getPiece(newPosition);
                if (target != null) {
                    if (target.getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
                        if(row == 8){
                            PromoteMe(newPosition);
                        }else {
                            possibleMoves.add(new ChessMove(startPosition, newPosition, null));
                        }
                    }
                }
            }



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
            if(InBounds(row,col)) {
                ChessPosition newPosition = new ChessPosition(row,col);
                ChessPiece target = board.getPiece(newPosition);
                if (target == null) {
                    if(row == 1){
                        PromoteMe(newPosition);
                    }else {
                        possibleMoves.add(new ChessMove(startPosition, newPosition, null));
                    }
                }
            }
            //check right black capture
            row = startRow - 1;
            col = startCol + 1;
            if(InBounds(row,col)) {
                ChessPosition newPosition = new ChessPosition(row,col);
                ChessPiece target = board.getPiece(newPosition);
                if (target != null) {
                    if (target.getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
                        if(row == 1){
                            PromoteMe(newPosition);
                        }else {
                            possibleMoves.add(new ChessMove(startPosition, newPosition, null));
                        }
                    }
                }
            }
            //check left black capture
            row = startRow - 1;
            col = startCol - 1;
            if(InBounds(row,col)) {
                ChessPosition newPosition = new ChessPosition(row,col);
                ChessPiece target = board.getPiece(newPosition);
                if (target != null) {
                    if (target.getTeamColor() != board.getPiece(startPosition).getTeamColor()) {
                        if(row == 1){
                            PromoteMe(newPosition);
                        }else {
                            possibleMoves.add(new ChessMove(startPosition, newPosition, null));
                        }
                    }
                }
            }
        }

        //promotion(if needed)
        return possibleMoves;
    }
}
