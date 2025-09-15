package chess;

import java.util.ArrayList;
import java.util.List;

public class BishopCalculator {
    private final List<ChessMove> possibleMoves;
    private final ChessPosition startPosition;
    private final ChessBoard board;

    public BishopCalculator(ChessBoard board, ChessPosition startPosition) {
        this.possibleMoves = new ArrayList<>();
        this.board = board;
        this.startPosition = startPosition;
    }

    public boolean CheckPosition(ChessPosition position){
        if(board.getPiece(position).getTeamColor() == board.getPiece(startPosition).getTeamColor()){
            return true;
        }else{
            return false;
        }
    }

    public void calculateMoves(){
        int startRow = this.startPosition.getRow();
        int startCol = this.startPosition.getColumn();
        //upper right Diagonal path
        int row = startRow + 1;
        int col = startCol + 1;
        while(row < 8 && col < 8){
            ChessPosition newPosition = new ChessPosition(row,col);
            if(CheckPosition(newPosition)) {
                break;
            }
            possibleMoves.add(new ChessMove(startPosition,newPosition,null));
            row++;
            col++;
        }


    }

    public List<ChessMove> getPossibleMoves(){
        return possibleMoves;
    }

}
