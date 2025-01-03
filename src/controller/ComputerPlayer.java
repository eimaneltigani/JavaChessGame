package controller;

import model.Board;
import model.Move;
import model.Piece;
import view.ChessGUI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class ComputerPlayer implements Player {
    ChessGUI gui;
    Piece selectedPiece;
    Board board;
    Move currMove;
    boolean color;

    public ComputerPlayer() {
        this.color = false;
    }

    @Override
    public void initializeBoard(ChessGUI gui) {
        this.gui = gui;
    }

    public Move decideMove(Board board) {
        HashMap<Piece, ArrayList<int[]>> bPieces = board.getAllPossibleMoves(color);

        // choosing random play for now
        Random r = new Random();
        int pIndex = r.nextInt(bPieces.size());
        int i = 0;
        Piece randomP = null;
        for(Piece key : bPieces.keySet()) {
            if (i == pIndex) {
                randomP = key;
                break;
            }
            i++;
        }

        ArrayList<int[]> pMoves = bPieces.get(randomP);
        int mIndex = r.nextInt(pMoves.size());
        int[] randomMove = pMoves.get(mIndex);

        currMove = new Move(randomP, randomMove[0], randomMove[1]);
        return currMove;
    }

    public void update(Board b, Move move) {
        b.movePiece(move);
        gui.update(move);

        if (move.isCaptured()) {
            gui.updateCapturedPiecePanel(b.getCapturedPieces());
        }

        currMove = null;
    }

    @Override
    public boolean getColor() {
        return color;
    }
}
