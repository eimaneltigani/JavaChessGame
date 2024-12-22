public class King extends Piece {
    public King(boolean isWhite) {
        super("king", isWhite);
    }

    @Override
    public boolean canMove(Board board, Tile start, Tile end) {
        return false;
    }
}
