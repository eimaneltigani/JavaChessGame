package model;

/**
 * Class used sort of as receipt/record that a move was made
 * Used as a way to flag captured piece and notify controller
 * Also used to make gui updates faster instead of iterating over entired board
 */
public class Move {

    Piece piece;
    int currRow, currCol;
    int targetRow, targetCol;
    boolean isCaptured;

    public Move(Piece piece, int targetRow, int targetCol) {
        this.piece = piece;
        this.currRow = piece.getRow();
        this.currCol = piece.getCol();
        this.targetRow = targetRow;
        this.targetCol = targetCol;
    }

    public Piece getPiece() {
        return piece;
    }

    public int getCurrRow() {
        return currRow;
    }

    public int getCurrCol() {
        return currCol;
    }

    public int getTargetRow() {
        return targetRow;
    }

    public int getTargetCol() {
        return targetCol;
    }

    public void setCaptured(boolean captured) {
        this.isCaptured = captured;
    }
    public boolean isCaptured() {
        return isCaptured;
    }
}
