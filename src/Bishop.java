public class Bishop extends Piece{
    public Bishop(boolean isWhite) {
        super("bishop", isWhite);
    }

    @Override
    public boolean canMove(Board board, Tile start, Tile end) {
        return false;
    }
}
