package player;

import board.Board;

public interface Player {
//    void makeMove(Board board);

    void update(Board board);

    int getColor();
}
