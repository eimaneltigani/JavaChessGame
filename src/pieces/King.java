package pieces;
import board.Piece;

public class King extends Piece {
    public King(boolean isWhite, int col, int row) {
        super("king", isWhite, col, row);
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }
}
