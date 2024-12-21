public abstract class Piece {
    String type;
    boolean isWhite;

    /**
     * Constructor
     * @param typeIn
     * @param isWhiteIn
     */
    public Piece(String typeIn, boolean isWhiteIn){
        this.type = typeIn;
        this.isWhite = isWhiteIn;
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

    public abstract boolean canMove(Board board, Tile start, Tile end);

}
