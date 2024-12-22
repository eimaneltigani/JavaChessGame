public class Knight extends Piece{
    public Knight(boolean isWhite) {
        super("knight", isWhite);
    }

    @Override
    public boolean canMove(Board board, Tile start, Tile end) {
        return false;
    }
}
