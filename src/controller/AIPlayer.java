package controller;

import model.Board;
import model.Move;
import model.Piece;
import view.ChessGUI;

import javax.swing.*;
import javax.swing.Timer;
import java.util.*;

public class AIPlayer implements Player {
    ChessGUI gui;
    Piece selectedPiece;
    Board board;
    Move currMove;
    boolean color;

    public AIPlayer() {
        this.color = false;
    }

    @Override
    public void initializeBoard(ChessGUI gui) {
        this.gui = gui;
    }

    int depth = 3;

    public Move decideMove(Board board) {
        if (board.inCheck(color)) {
            gui.setCheck(board.getKing(color));
        }
        gui.setTurn(false);

        return findBestMove(board);
    }

    public void update(Board b, Move move) {
        b.movePiece(move);

        // Set delay, 1000ms = 1 second
        int delay = 1000;

        Move castlingMove;
        if(b.getLastMove()!=move) {
            castlingMove = b.getLastMove();
        } else {
            castlingMove = null;
        }

        // Create Timer
        Timer timer = new Timer(delay, e -> {
            // GUI update logic after the delay
            SwingUtilities.invokeLater(() -> {
                gui.update(move);
                if (move.isCaptured()) {
                    gui.updateCapturedPiecePanel(b.getCapturedPieces());
                }
                if(castlingMove!=null) { // update extra castling move (rook)
                    gui.update(castlingMove);
                }
            });
            // Stop the timer (only needed if the Timer is a one-shot timer)
            ((Timer) e.getSource()).stop();
        });

        // Start the Timer
        timer.setRepeats(false); // Ensure the timer fires only once
        timer.start();
    }

    @Override
    public boolean getColor() {
        return color;
    }

    public static final int MAX = Integer.MAX_VALUE;
    public static final int MIN = Integer.MIN_VALUE;


    public Move findBestMove(Board board) {
        int bestEval = MIN;
        Move bestMove = null;

        HashMap<Piece, ArrayList<int[]>> moves = board.getAllPossibleMoves(color);

        for (Piece piece : moves.keySet()) {
            ArrayList<int[]> pMoves = new ArrayList<>(moves.get(piece));
            for (int[] move : pMoves) {
                Move currMove = new Move(piece, move[0], move[1]);
                board.movePiece(currMove);
                int eval = minimax(board, depth - 1, false); // negative --> good for opponent = bad for us
                board.undoLastMove();
                if (eval > bestEval) {
                    bestEval = eval;
                    bestMove = currMove;
                }
            }
        }

        return bestMove;
    }


    /**
     * Search algorithm that traverses game tree to explore all possible moves for each player
     *
     * @return - evaluation of board, important to note we are not measuring moves
     *             but rather the subsequent state reached by taking a particular move
     */
    private int minimax(Board board, int depth, boolean maximizingPLayer) {
        if (depth == 0) {
            return evaluateBoard(board);
        }

        // Zero-sum game where opponents advantage = players disadvantage
        // If players turn, return the highest possible outcome given a certain move
        if(maximizingPLayer) {
            int maxEval = MIN;
            HashMap<Piece, ArrayList<int[]>> moves = board.getAllPossibleMoves(color);
            for (Piece p : moves.keySet()) {
                ArrayList<int[]> pMoves = new ArrayList<>(moves.get(p));
                for (int[] move : pMoves) {
                    board.movePiece(new Move(p, move[0], move[1]));
                    int eval = minimax(board, depth - 1, false); // negative --> good for opponent = bad for us
                    board.undoLastMove();
                    maxEval =  Math.max(maxEval, eval);
                }
            }
            return maxEval;
        } else { // Opponent will always play their strongest move (minimizing score)
            int minEval = MAX;HashMap<Piece, ArrayList<int[]>> moves = board.getAllPossibleMoves(color);
            for (Piece p : moves.keySet()) {
                ArrayList<int[]> pMoves = new ArrayList<>(moves.get(p));
                for (int[] move : pMoves) {
                    board.movePiece(new Move(p, move[0], move[1]));
                    int eval = minimax(board, depth - 1, false); // negative --> good for opponent = bad for us
                    board.undoLastMove();
                    minEval =  Math.min(minEval, eval);
                }
            }
            return minEval;
        }
    }

    Map<String, Integer> pieceValues = Map.of(
            "pawn", 1,
            "knight", 3,
            "bishop", 3,
            "rook", 5,
            "queen", 9
    );

    public int evaluateBoard(Board board) {
        int whiteEval = countMaterial(true, board);
        int blackEval = countMaterial(false, board);

        return whiteEval - blackEval;
    }

    public int countMaterial(boolean color, Board board) {
        int totalValue = 0;
        ArrayList<Piece> pieces = board.getPlayersPieces(color);
        for(Piece piece : pieces) {
            String pieceType = piece.getType();
            if(pieceValues.containsKey(pieceType)) {
                totalValue += pieceValues.get(pieceType);
            }
        }

        return totalValue;
    }
}
