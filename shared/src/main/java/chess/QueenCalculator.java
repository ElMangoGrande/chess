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
        // Get all diagonal moves from BishopCalculator
        BishopCalculator bishopCalc = new BishopCalculator(board, startPosition);
        bishopCalc.calculateMoves();
        List<ChessMove> bishopMoves = bishopCalc.getPossibleMoves();


        // Get all straight moves from RookCalculator
        RookCalculator rookCalc = new RookCalculator(board, startPosition);
        List<ChessMove> rookMoves = rookCalc.calculateMoves();

        // Combine both
        possibleMoves.addAll(bishopMoves);
        possibleMoves.addAll(rookMoves);

        return possibleMoves;
    }

}
