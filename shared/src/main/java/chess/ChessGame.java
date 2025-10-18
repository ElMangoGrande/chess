package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private ChessBoard board;
    private boolean whiteTurn;

    public ChessGame() {
        this.board = new ChessBoard();
        this.board.resetBoard();
        this.whiteTurn = true;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        if (whiteTurn) {
            return TeamColor.WHITE;
        } else {
            return TeamColor.BLACK;
        }
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        whiteTurn = team == TeamColor.WHITE;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        Collection<ChessMove> validMoves = new ArrayList<>();
        //gets and stores useful information
        Collection<ChessMove> possibleMoves = board.getPiece(startPosition).pieceMoves(board, startPosition);
        ChessPiece piece = board.getPiece(startPosition);
        ChessGame.TeamColor teamColor = piece.getTeamColor();

        //goes through all possible moves for a piece at a given position
        for (ChessMove move : possibleMoves) {
            //creates a new board for each theoretical future board
            ChessBoard newBoard = board.copy();
            newBoard.addPiece(move.getEndPosition(), board.getPiece(startPosition));
            newBoard.addPiece(startPosition, null);

            // temporarily swap in the simulated board
            ChessBoard oldBoard = this.board;
            this.board = newBoard;
            boolean inCheck = isInCheck(piece.getTeamColor());
            this.board = oldBoard; // restore the real board

            //checks to see if the king of the same team as start would be in check
            if (!inCheck) {
                // if not in check adds the move
                validMoves.add(move);
            }
        }
        return validMoves;
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        //checks to see if there is a piece to move
        if (board.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("nothing to move here!");
        }
        Collection<ChessMove> valid = validMoves(move.getStartPosition());
        //checks if it is the correct turn
        if (getTeamTurn() != board.getPiece(move.getStartPosition()).getTeamColor()) {
            throw new InvalidMoveException("not your turn!");
        }
        //checks to see that there are moves to be made
        if (valid.contains(move) && board.getPiece(move.getStartPosition()) != null) {
            if (move.getPromotionPiece() != null) {
                ChessPiece oldPiece = board.getPiece(move.getStartPosition());
                ChessPiece promotedPiece = new ChessPiece(oldPiece.getTeamColor(), move.getPromotionPiece());
                board.addPiece(move.getEndPosition(), promotedPiece);
            } else {
                board.addPiece(move.getEndPosition(), board.getPiece(move.getStartPosition()));
            }
            board.addPiece(move.getStartPosition(), null);
            whiteTurn = !whiteTurn;

        } else {
            whiteTurn = !whiteTurn;
            throw new InvalidMoveException("invalid move");
        }

    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        //find the king of our team
        ChessPosition kingPosition = null;
        //go through every piece
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece != null && piece.getTeamColor() == teamColor && piece.getPieceType() == ChessPiece.PieceType.KING) {
                    kingPosition = new ChessPosition(row, col);
                    break;
                }
            }
        }
        //checks to see if there is no king
        if (kingPosition == null) {
            throw new IllegalStateException("No king found for " + teamColor);
        }

        TeamColor opponent = (teamColor == TeamColor.WHITE ? TeamColor.BLACK : TeamColor.WHITE);
        //go through all chess pieces in the board on the opposing team
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row, col));
                if (piece == null || piece.getTeamColor() != opponent){ continue;}
                for (ChessMove move : piece.pieceMoves(board, new ChessPosition(row, col))) {
                    if (move.getEndPosition().equals(kingPosition)) {
                        return true;
                    }
                }

            }
        }
        //if any pieces can get to where the king is, then we are in check
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        //checks to see that a team is in check
        if (!isInCheck(teamColor)) return false;

        // Go through all pieces
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition pos = new ChessPosition(row, col);
                ChessPiece piece = board.getPiece(pos);

                // Skip empty squares or pieces not on this team
                if (piece == null || piece.getTeamColor() != teamColor){ continue;}

                // Check each valid move
                for (ChessMove move : validMoves(pos)) {
                    // Simulate the move on a copy of the board
                    ChessBoard newBoard = board.copy();
                    newBoard.addPiece(move.getEndPosition(), piece);
                    newBoard.addPiece(pos, null);

                    // Temporarily swap boards
                    ChessBoard oldBoard = this.board;
                    this.board = newBoard;
                    boolean stillInCheck = isInCheck(teamColor);
                    this.board = oldBoard;

                    // If any move gets us out of check, it's not checkmate
                    if (!stillInCheck) return false;
                }
            }
        }

        // No moves escape check -> checkmate
        return true;
    }



    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        //List of ValidMoves
        Collection<ChessMove> validMoves = new ArrayList<>();

        //goes through every piece
        for (int row = 1; row < 9; row++) {
            for (int col = 1; col < 9; col++) {
                ChessPiece piece = board.getPiece(new ChessPosition(row,col));
                //in the team
                if(piece != null && piece.getTeamColor() == teamColor) {
                    //collects all valid moves
                    validMoves.addAll(validMoves(new ChessPosition(row,col)));
                }
            }
        }
        //if there are any valid moves return true
        return !isInCheck(teamColor) && validMoves.isEmpty();
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return whiteTurn == chessGame.whiteTurn && Objects.equals(board, chessGame.board);
    }

    @Override
    public int hashCode() {
        return Objects.hash(board, whiteTurn);
    }
}
