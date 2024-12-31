package model.pieces;
import model.Board;
import model.Move;
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
    boolean inCheck = false;

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

        // Check for castling
        if(this.isFirstMove()) {
            // Check Kingside castling
            if(canCastle(board, true)) {
                int newCol = currCol + 2;
                legalMoves.add(new int[]{currRow, newCol});
            }

            // Check Queenside castling
            if(canCastle(board, false)) {
                int newCol = currCol - 2;
                legalMoves.add(new int[]{currRow, newCol});
            }
        }

        return legalMoves;
    }

    // Castling rules - permitted only if neither king nor rook have previously moved
    // and squares between are vacant
    private boolean canCastle(Board board, boolean kingside) {
        int rookCol = kingside ? 7 : 0;
        int direction = kingside ? 1 : -1;

        // Check if rook hasn't moved
        Piece rook = board.findPieceByLocation(row, rookCol);
        if(!(rook instanceof Rook) || !rook.isFirstMove()) {
            return false;
        }

        // Check that all squares between king & rook are empty
        int currCol = col + direction;
        while(currCol != rookCol) {
            if(board.findPieceByLocation(row,currCol) != null) {
                return false;
            }
            currCol += direction;
        }

        return true;
    }

    public void setInCheck(boolean inCheck) {
        this.inCheck = inCheck;
    }

    public boolean inCheck() {
        return inCheck;
    }

}
