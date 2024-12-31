package model.pieces;
import model.Board;
import model.Piece;

import java.util.ArrayList;

/**
 * Bishop rules - Moves any number of squares diagonally.
 */
public class Bishop extends Piece {
    int[][] directions = {
            {-1, -1}, // Top-left
            {-1, 1},  // Top-right
            {1, -1},  // Bottom-left
            {1, 1}    // Bottom-right
    };

    public Bishop(boolean isWhite, int col, int row) {
        super("bishop", isWhite, col, row);
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

        // iterate through each direction diagonally until blocked
        for(int[] direction : directions) {
            int nextRow = currRow + direction[0];
            int nextCol = currCol + direction[1];

            while(isWithinBounds(nextRow,nextCol)) {
                Piece targetPiece = board.findPieceByLocation(nextRow, nextCol);

                if (targetPiece == null) {
                    // Empty square, add to legal moves
                    legalMoves.add(new int[]{nextRow, nextCol});
                } else {
                    if (targetPiece.isWhite() == getOppositeColor()) {
                        // Opponent's piece can be captured
                        legalMoves.add(new int[]{nextRow, nextCol});
                    }
                    // Stop moving in this direction after encountering a piece
                    break;
                }
                // Move next step in  current direction
                nextRow += direction[0];
                nextCol += direction[1];
            }
        }

        return legalMoves;
    }
}
