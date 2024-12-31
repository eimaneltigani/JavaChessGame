package model.pieces;
import model.Board;
import model.Piece;

import java.util.ArrayList;

/**
 * Knight rules -  Moves in an ‘L-shape,’ two squares in a straight direction
 * and then one square perpendicular to that.
 */
public class Knight extends Piece {
    int[][] directions = {
            {-2,-1},
            {-2,1},
            {-1,-2},
            {-1,2},
            {1,-2},
            {1,2},
            {2,-1},
            {2, 1}
    };

    public Knight(boolean isWhite, int col, int row) {
        super("knight", isWhite, col, row);
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }

    @Override
    public ArrayList<int[]> availableMoves(Board board) {
        ArrayList<int[]> legalMoves = new ArrayList<>();
        int currRow = this.row;
        int currCol = this.col;

        // Check each potential move
        for (int[] direction : directions) {
            int targetRow = currRow + direction[0];
            int targetCol = currCol + direction[1];

            // Ensure move is within bounds
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
