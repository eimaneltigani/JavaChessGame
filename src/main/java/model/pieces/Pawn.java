package model.pieces;
import model.Board;
import model.Piece;

import java.util.ArrayList;

/**
 * Pawn rules - Moves one square forward, but on its first move, it can move
 * two squares forward. It captures diagonally one square forward.
 */
public class Pawn extends Piece {
    int direction;
    boolean firstMove;

    public Pawn(boolean isWhite, int col, int row) {
        super("pawn", isWhite, col, row);
        direction = isWhite ? -1 : 1; // White moves up (-1), Black moves down (+1)
        firstMove = true;
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }

    @Override
    public ArrayList<int[]> availableMoves(Board board) {
        ArrayList<int[]> legalMoves= new ArrayList<>();
        int currRow = this.row;
        int currCol = this.col;

        // Normal forward move
        int nextRow = currRow + direction;
        if(isWithinBounds(nextRow, currCol) && board.findPieceByLocation(nextRow,currCol)==null) {
            legalMoves.add(new int[]{nextRow, currCol});

            // Starting position move (only if next square is empty)
            int twoStepRow = currRow + 2 * direction;
            if (firstMove && isWithinBounds(twoStepRow, currCol)
                    && (board.findPieceByLocation(twoStepRow, currCol) == null)) {
                    legalMoves.add(new int[]{twoStepRow, currCol});
            }
        }

        // check capture moves (diagonal left and right)
        int[] captureCols = {currCol -1, currCol+1};
        for (int col : captureCols) {
            if(isWithinBounds(nextRow,col)) {
                Piece potentialCapture = board.findPieceByLocation(nextRow,col);
                if(potentialCapture!=null && potentialCapture.isWhite() == getOppositeColor()) {
                    legalMoves.add(new int[]{nextRow, col});
                }
            }
        }

        return legalMoves;
    }

    public boolean getFirstMove() {
        return firstMove;
    }

    public void setFirstMove(boolean isFirst) {
        this.firstMove = isFirst;
    }
}
