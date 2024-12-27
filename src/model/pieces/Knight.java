package model.pieces;
import model.Board;
import model.Piece;

import java.util.ArrayList;

/**
 * Knight rules -  Moves in an ‘L-shape,’ two squares in a straight direction
 * and then one square perpendicular to that.
 */
public class Knight extends Piece {
    public Knight(boolean isWhite, int col, int row) {
        super("knight", isWhite, col, row);
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }

    @Override
    public ArrayList<int[]> legalMoves(Board board) {
        ArrayList<int[]> legalMoves = new ArrayList<>();
        int currRow = this.row;
        int currCol = this.col;

        // Define all possible moves for a Knight (L-shaped moves)
        int[][] potentialMoves = {
                {currRow - 2, currCol - 1},
                {currRow - 2, currCol + 1},
                {currRow - 1, currCol - 2},
                {currRow - 1, currCol + 2},
                {currRow + 1, currCol - 2},
                {currRow + 1, currCol + 2},
                {currRow + 2, currCol - 1},
                {currRow + 2, currCol + 1}
        };

        // Check each potential move
        for (int[] move : potentialMoves) {
            int targetRow = move[0];
            int targetCol = move[1];

            // Ensure the move is within bounds
            if (isWithinBounds(targetRow, targetCol)) {
                Piece targetPiece = board.findPieceByLocation(targetRow, targetCol);

                // If the target square is empty or contains an opponent's piece
                if (targetPiece == null || targetPiece.isWhite() == getOppositeColor()) {
                    legalMoves.add(new int[]{targetRow, targetCol});
                }
            }
        }

        return legalMoves;
    }
}
