package model.pieces;
import model.Board;
import model.Piece;

import java.util.ArrayList;

public class Knight extends Piece {
    public Knight(boolean isWhite, int col, int row) {
        super("knight", isWhite, col, row);
    }

    @Override
    public boolean canMove(int targetCol, int targetRow) {
        return false;
    }

    @Override
    public ArrayList<int[]> legalMoves(Board board) {
        return null;
    }
}