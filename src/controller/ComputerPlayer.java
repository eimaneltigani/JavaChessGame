package controller;

import model.Board;
import model.Move;
import model.Piece;
import view.ChessGUI;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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
        if (board.inCheck(color)) {
            gui.setCheck(board.getKing(color));
        }

        gui.setTurn(false);
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
        System.out.println(randomP);
        System.out.println(Arrays.toString(randomMove));
        currMove = new Move(randomP, randomMove[0], randomMove[1]);
        return currMove;
    }

    public void update(Board b, Move move) {
        b.movePiece(move);

        // Set delay, 1000ms = 1 second
        int delay = 1000;

        // Create Timer
        Timer timer = new Timer(delay, e -> {
            // GUI update logic after the delay
            SwingUtilities.invokeLater(() -> {
                gui.update(move);
                if (move.isCaptured()) {
                    gui.updateCapturedPiecePanel(b.getCapturedPieces());
                }
            });
            // Stop the timer (only needed if the Timer is a one-shot timer)
            ((Timer) e.getSource()).stop();
        });

        // Start the Timer
        timer.setRepeats(false); // Ensure the timer fires only once
        timer.start();

        currMove = null;
    }

    @Override
    public boolean getColor() {
        return color;
    }
}
