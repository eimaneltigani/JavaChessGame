package model.pieces;
import model.Board;
import model.Piece;

import java.util.ArrayList;

/**
 * King rules - Moves one square in any direction
 */
public class King extends Piece {
    int[][] directions = {
            {-1, 0}, // Up
            {1, 0},  // Down
            {0, -1}, // Left
            {0, 1},  // Right
            {-1, -1}, // Top-left
            {-1, 1},  // Top-right
            {1, -1},  // Bottom-left
            {1, 1}    // Bottom-right
    };

    public King(boolean isWhite, int col, int row) {
        super("king", isWhite, col, row);
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

        // Process each direction
        for (int[] direction : directions) {
            int nextRow = currRow + direction[0];
            int nextCol = currCol + direction[1];

            if (isWithinBounds(nextRow, nextCol)) {
                Piece targetPiece = board.findPieceByLocation(nextRow, nextCol);

                if (targetPiece == null || targetPiece.isWhite() == getOppositeColor()) {
                    // Add empty square or opponent's square as a legal move
                    legalMoves.add(new int[]{nextRow, nextCol});
                }
            }
        }

        return legalMoves;
    }
}
