public class Queen extends Piece{
    public Queen(boolean isWhite) {
        super("queen", isWhite);
    }

    @Override
    public boolean canMove(Board board, Tile start, Tile end) {
        return false;
    }
}
