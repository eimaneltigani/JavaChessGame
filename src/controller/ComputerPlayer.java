package controller;

import model.Board;
import model.Move;
import model.Piece;
import view.ChessGUI;

public class ComputerPlayer implements Player {
    ChessGUI gui;
    Board board;
    Move currMove;


    public Move decideMove(Board board) {
        return currMove;
    }

    public void update(Board b, Move move) {
        gui.update(move);
    }


    @Override
    public void initializeBoard(Board board) {
        board = board;
    }

    @Override
    public boolean isHuman() {
        return false;
    }
}
