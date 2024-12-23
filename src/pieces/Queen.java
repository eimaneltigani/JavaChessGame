package pieces;
import board.Piece;

public class Queen extends Piece {
    public Queen(boolean isWhite, int col, int row) {
        super("queen", isWhite, col, row);
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }
}
