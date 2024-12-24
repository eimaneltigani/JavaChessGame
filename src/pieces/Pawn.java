package pieces;
import board.Board;
import board.Piece;

import java.util.ArrayList;

/**
 * Pawn rules - moves one square forward, but on its first move, it can move
 * two squares forward. It captures diagonally one square forward.
 */
public class Pawn extends Piece {
    int direction;

    public Pawn(boolean isWhite, int col, int row) {
        super("pawn", isWhite, col, row);
        direction = isWhite ? -1 : 1; // White moves up (-1), Black moves down (+1)
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }

    @Override
    public ArrayList<int[]> legalMoves() {
        ArrayList<int[]> legalMoves= new ArrayList<>();
        int currRow = this.row;
        int currCol = this.col;

        // Normal forward move
        int nextRow = currRow + direction;
        if(isWithinBounds(nextRow, currCol) && Board.findPieceByLocation(nextRow,currCol)==null) {
            legalMoves.add(new int[]{nextRow, currCol});

            // Starting position move (only if first square is empty)
            int twoStepRow = currRow + 2 * direction;
            if (firstMove && isWithinBounds(twoStepRow, currCol)
                    && (Board.findPieceByLocation(nextRow, currCol) == null)) {
                    legalMoves.add(new int[]{twoStepRow, currCol});
            }
        }

        // check capture moves (diagonal left and right)
        int[] captureCols = {currCol -1, currCol+1};
        for (int col : captureCols) {
            Piece potentialCapture = Board.findPieceByLocation(nextRow,col);
            if(isWithinBounds(nextRow, col) && potentialCapture!=null
                    && potentialCapture.isWhite() == getOppositeColor()) {
                legalMoves.add(new int[]{nextRow, col});
            }
        }

        return legalMoves;
    }
}
