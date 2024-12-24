package board;


/**
 * Abstract class to represent common functionality of all chess pieces
 */
public abstract class Piece {
    String type;
    boolean isWhite;
    public int row, col;

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
    }

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

    public abstract boolean canMove(int targetCol, int targetRow);

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
        return this.col;
    }

}
