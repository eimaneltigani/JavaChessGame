package pieces;
import board.Piece;

public class Knight extends Piece {
    public Knight(boolean isWhite, int col, int row) {
        super("knight", isWhite, col, row);
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }
}
