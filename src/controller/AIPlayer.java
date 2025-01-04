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
        } else {
            ArrayList<int[]> kingsPosition = new ArrayList<>();
            kingsPosition.add(new int[]{board.getKing(color).getRow(), board.getKing(color).getCol()});
            gui.removeHighlight(kingsPosition);
        }

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
                gui.setTurn(true); // Switch to users turn
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
     * Note: Not efficient due to the excessive branching factor of ~ 35 on average for possible chess paths
     *
     * @return - Evaluation of board where score is always evaluated from single players POV
     *           Returns best score based on opponents move if possible, or heuristic value if exact value not possible.
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
                    int eval = minimax(board, depth - 1, false); // returns other players lowest score and minimizes impact
                    board.undoLastMove();
                    maxEval =  Math.max(maxEval, eval);
                }
            }
            return maxEval;
        } else { // Opponent will always play their strongest move (minimizing score)
            int minEval = MAX;
            HashMap<Piece, ArrayList<int[]>> moves = board.getAllPossibleMoves(color);
            for (Piece p : moves.keySet()) {
                ArrayList<int[]> pMoves = new ArrayList<>(moves.get(p));
                for (int[] move : pMoves) {
                    board.movePiece(new Move(p, move[0], move[1]));
                    int eval = minimax(board, depth - 1, true); // negative --> good for opponent = bad for us
                    board.undoLastMove();
                    minEval =  Math.min(minEval, eval);
                }
            }

            return minEval; // Positive mean player winning, negative mean opponent winning
        }
    }

    /**
     * Negamax is slightly optimized of min-max that flips score rather than writing two separate functions
     * Alpha Beta works by eliminating
     *
     * @return - Evaluation score viewed from perspective of side to move. Negates return value
     *            to reflect change and perspective of successor
     */
    public int negamax(int depth, int alpha, int beta, boolean color) {
        if (depth == 0) {
            return negaEvaluation(board, color);
        }

        HashMap<Piece, ArrayList<int[]>> moves = board.getAllPossibleMoves(color);

        if(moves.isEmpty()) {
            if(board.inCheck(color)) {
                return MIN;
            }
            return 0;
        }

        for (Piece p : moves.keySet()) {
            ArrayList<int[]> pMoves = new ArrayList<>(moves.get(p));
            for (int[] move : pMoves) {
                board.movePiece(new Move(p, move[0], move[1]));
                int eval = - negamax( depth - 1, -beta, -alpha, !color); // negative --> good for opponent = bad for us
                board.undoLastMove();
                if (eval >= beta) {
                    // Move too good, opponent will avoid this position
                    return beta; // *Snip*
                }
                alpha = Math.max(alpha, eval);
            }
        }

        return alpha;
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

    public int negaEvaluation(Board board, boolean color) {
        int whiteEval = countMaterial(true, board);
        int blackEval = countMaterial(false, board);

        int evaluation = whiteEval - blackEval;

        int perspective = color ? -1 : 1;
        return evaluation * perspective;
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
