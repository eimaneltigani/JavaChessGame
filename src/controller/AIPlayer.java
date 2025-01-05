package controller;

import model.Board;
import model.Move;
import model.Piece;
import view.ChessGUI;

import javax.swing.*;
import java.util.*;

public class AIPlayer implements Player {
    ChessGUI gui;
    Board board;
    Move currMove;
    boolean color;
    boolean inCheck;

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
            inCheck = true;
            gui.setCheck(board.getKing(color));
        }

        // Set delay in milliseconds (1000ms = 1 second)
        int delay = 600;

        // Create delay for a more natural user experience
        try {
            Thread.sleep(delay); // Pause for the specified delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Restore interrupted status
            e.printStackTrace();
        }

        // Find the best move
        currMove = findBestMove(board);

        return currMove;
    }

    public void update(Board b, Move move) {
        // Update model
        b.movePiece(move);

        // Update view
        SwingUtilities.invokeLater(() -> {
            gui.update(move);
            if (move.isCaptured()) {
                gui.updateCapturedPiecePanel(b.getCapturedPieces());
            }

            // If castling, update additional rook move
            if(b.isCastling(move)) {
                Move castlingMove = b.getLastMove();
                gui.update(castlingMove);
            }

            // update extra pawn promotion move
            if(b.isPromotePawn(move)) {
                gui.update(b.getLastMove());
            }

            // undo previous check highlight
            if(inCheck) {
                Piece king = b.getKing(color);
                ArrayList<int[]> kingsPosition = new ArrayList<>();
                // If King moved, need to find its previous panel
                if(move.getPiece() == king) {
                    kingsPosition.add(new int[]{move.getCurrRow(), move.getCurrCol()});
                } else {
                    kingsPosition.add(new int[]{king.getRow(),king.getCol()});
                }
                gui.removeHighlight(kingsPosition);
                inCheck = false;
            }

            gui.setTurn(true); // Switch to users turn
        });
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
     * @return - Score evaluated from single players POV
     *           Returns best score based on opponents move if possible, or heuristic value if exact value not possible.
     */
    private int minimax(Board board, int depth, boolean maximizingPLayer) {
        if (depth == 0) {
            return evaluateBoard(board);
        }

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

            return minEval;
        }
    }

    /**
     * Negamax is slightly optimized of min-max that flips score rather than writing two separate functions
     * Alpha Beta works by eliminating nodes that cannot beat already analyzed positions at tree level
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
