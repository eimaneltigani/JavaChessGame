package pieces;
import board.Piece;

public class Pawn extends Piece {
    public Pawn(boolean isWhite, int col, int row) {
        super("pawn", isWhite, col, row);
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }
}
