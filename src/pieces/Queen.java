package pieces;
import board.Board;
import board.Piece;

import java.util.ArrayList;

public class Queen extends Piece {
    public Queen(boolean isWhite, int col, int row) {
        super("queen", isWhite, col, row);
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }

    @Override
    public ArrayList<int[]> legalMoves() {
        return null;
    }
}
