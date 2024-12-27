package controller;

import model.Board;
import model.Move;

/**
 * Base class to ensure players (both human and computer)
 * have the functionality needed to run game loop.
 */
public interface Player {

    Move decideMove(Board board);

    void update(Board board, Move move);

    void initializeBoard(Board board);

    boolean isHuman();
}
