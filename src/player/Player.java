package player;

import board.Board;
import board.Piece;

public interface Player {
    void makeMove(Piece piece, int targetRow, int targetCol);
    void update(Board board, Player player);
    void setOpponent(Player player);
    Player getOpponent();
    int getColor();
}
