package pieces;
import board.Piece;

public class Rook extends Piece {
    public Rook(boolean isWhite, int col, int row) {
        super("rook", isWhite, col, row);
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }
}
