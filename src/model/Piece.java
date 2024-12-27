package model;


import java.util.ArrayList;

/**
 * Abstract class to represent common functionality of all chess model.pieces
 */
public abstract class Piece {
    String type;
    boolean isWhite;
    public int row, col;
    protected boolean firstMove;

    /**
     * Constructor
     * @param typeIn
     * @param isWhiteIn
     * @param col
     * @param row
     */
    public Piece(String typeIn, boolean isWhiteIn, int row, int col) {
        this.type = typeIn;
        this.isWhite = isWhiteIn;
        this.col = col;
        this.row = row;
        this.firstMove = true;
    }

    public abstract boolean canMove(int targetCol, int targetRow);

    public abstract ArrayList<int[]> legalMoves(Board board);

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public void setWhite(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getRow() {
        return row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getCol() {
        return col;
    }

    public boolean getOppositeColor() {
        return !this.isWhite;
    }

    public void setFirstMove() {
        firstMove = false;
    }

    public boolean getFirstMove() {
        return firstMove;
    }

    public boolean isWithinBounds(int row, int col) {
        return row >= 0 && row <8 && col >= 0 && col < 8;
    }
}
