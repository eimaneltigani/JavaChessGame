public class Pawn extends Piece{
    public Pawn(boolean isWhite) {
        super("pawn", isWhite);
    }

    @Override
    public boolean canMove(Board board, Tile start, Tile end) {
        return false;
    }
}
