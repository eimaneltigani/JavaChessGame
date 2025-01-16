package model.pieces;
import model.Board;
import model.Move;
import model.Piece;

import java.util.ArrayList;
import java.util.Stack;

/**
 * Rook rules - Moves any number of squares horizontally or vertically
 */
public class Rook extends Piece {
    int[][] directions = {
            {-1, 0}, // Up
            {1, 0},  // Down
            {0, -1}, // Left
            {0, 1}   // Right
    };
    Stack<Move> lastMoves;

    public Rook(boolean isWhite, int col, int row) {
        super("rook", isWhite, col, row);
        lastMoves = new Stack<>();
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

        // Iterate in each direction until blocked
        for (int[] direction : directions) {
            int nextRow = currRow + direction[0];
            int nextCol = currCol + direction[1];

            while (isWithinBounds(nextRow, nextCol)) {
                Piece targetPiece = board.findPieceByLocation(nextRow, nextCol);

                if (targetPiece == null) {
                    // Empty square, add to legal moves
                    legalMoves.add(new int[]{nextRow, nextCol});
                } else {
                    // Square occupied by a piece
                    if (targetPiece.isWhite() == getOppositeColor()) {
                        // Opponent's piece can be captured
                        legalMoves.add(new int[]{nextRow, nextCol});
                    }
                    // Break once we've encountered a piece
                    break;
                }

                // Move next step in  current direction
                nextRow += direction[0];
                nextCol += direction[1];
            }
        }

        return legalMoves;
    }

    public Stack<Move> getLastMoves() {
        return lastMoves;
    }

    public void addMove(Move move) {
        lastMoves.add(move);
    }

    public void removeLastMove() {
        if(!lastMoves.isEmpty()) {
            lastMoves.pop();
        }
    }
}
