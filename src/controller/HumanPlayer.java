package controller;

import model.Board;
import model.Move;
import model.Piece;
import view.ChessGUI;
import view.ChessGUI.ClickListener;

import java.util.ArrayList;

public class HumanPlayer implements Player, ClickListener {
    ChessGUI gui;
    Board board;
    Piece selectedPiece;
    ArrayList<int[]> legalMoves;
    Move currentMove;

    public HumanPlayer() {
        gui = new ChessGUI(this);
    }

    public void initializeBoard(Board b) {
        gui.initializeBoard(b);
    }


    public Move decideMove(Board b) {
        currentMove = null; // reset move before starting
        board = b;
        selectedPiece = null;

        // enable clicks for user pieces
        ArrayList<Piece> availablePiecesToMove = board.getWhitePieces();
        gui.enableUserClicks(availablePiecesToMove);

        // wait until user finishes decision
        while (currentMove == null) {
            try {
                Thread.sleep(100); // Prevent busy waiting by pausing briefly
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("Interrupted while waiting for the move.");
            }
        }
        board = null;
        return currentMove;
    }

    @Override
    public void onClick(int row, int col, boolean captured) {
        System.out.println("User clicked row:" + row + "and col:" + col);
        Piece piece = board.findPieceByLocation(row, col);

        // If selecting piece for first time
        if (selectedPiece == null) {
            selectedPiece = piece;
            legalMoves = selectedPiece.legalMoves(board);
            gui.highlightLegalMoves(legalMoves);

        } else if (piece!= null && piece.isWhite()) {
            // if user is selecting another piece to move
            gui.removeHighlight(legalMoves);
            selectedPiece = piece;
            legalMoves = selectedPiece.legalMoves(board);
            gui.highlightLegalMoves(legalMoves);
        } else {

            currentMove = new Move(selectedPiece, row, col, captured);
        }
    }

    public void update(Board b, Move move) {
        gui.disableUserClicks();
        gui.removeHighlight(legalMoves);
        legalMoves = null;

        // update board model
        b.movePiece(move);
        // update game panel
        gui.update(move);

        // update captured panel
        if(move.isCaptured()) {
            System.out.println("piece was captured");
        }


    }

    @Override
    public boolean isHuman() {
        return true;
    }

}