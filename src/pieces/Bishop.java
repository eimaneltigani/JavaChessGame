package pieces;
import board.Piece;

public class Bishop extends Piece {
    public Bishop(boolean isWhite, int col, int row) {
        super("bishop", isWhite, col, row);
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }
}
