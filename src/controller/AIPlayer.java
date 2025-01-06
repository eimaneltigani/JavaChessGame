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
        currMove = getBestMoveAlphaBeta(board);

        return currMove;
    }

    public void update(Board b, Move move) {
        // Update model
        b.movePiece(move);

        // Update view
        SwingUtilities.invokeLater(() -> {
            gui.update(move);
            if (move.isCaptured()) {
                gui.updateCapturedPanel(b.getCapturedPieces());
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


    public long totalTime = 0;
    public long totalNodes = 0;


    public Move getBestMoveAlphaBeta(Board board) {
        long start = System.nanoTime();
        int bestEval = MIN;
        totalNodes = 0;
        totalTime = 0;
        Move bestMove = null;

        HashMap<Piece, ArrayList<int[]>> moves = board.getAllPossibleMoves(color);

        for (Piece piece : moves.keySet()) {
            ArrayList<int[]> pMoves = new ArrayList<>(moves.get(piece));
            for (int[] move : pMoves) {
                Move currMove = new Move(piece, move[0], move[1]);
                board.movePiece(currMove);
                int eval = - negamax(board, depth - 1, MIN, MAX, color); // negative --> good for opponent = bad for us
                board.undoLastMove();

                if (eval > bestEval) {
                    bestEval = eval;
                    bestMove = currMove;
                }
            }
        }

        totalTime = (System.nanoTime() - start);

        return bestMove;
    }

    /**
     * Negamax is slightly optimized of min-max that flips score rather than writing two separate functions
     * Alpha Beta works by eliminating nodes that cannot beat already analyzed positions at tree level
     *
     * @return - Evaluation of board from current player's perspective
     */
    public int negamax(Board board, int depth, int alpha, int beta, boolean color) {
        totalNodes++;

        if (depth == 0) {
            return evaluate(board, color);
        }

        int bestEval = MIN;

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
                int eval = - negamax(board, depth - 1, -beta, -alpha, !color); // negative --> good for opponent = bad for us
                board.undoLastMove();

                bestEval = Math.max(bestEval, eval);
                alpha = Math.max(alpha, eval);

                if (alpha >= beta) {
                    // Move too good, opponent will avoid this position
                    break;
                }
            }
        }

        return bestEval;
    }

    public int evaluate(Board board, boolean color) {
        int score = 0;

        int numK = 0, numEK = 0;
        int numQ = 0, numEQ = 0;
        int numR = 0, numER = 0;
        int numB = 0, numEB = 0;
        int numN = 0, numEN = 0;
        int numP = 0, numEP = 0;
        int doubledPawns = 0;
        int doubledEPawns = 0;
        int blockedPawns = 0;
        int blockedEPawns = 0;
        int isolatedPawns = 0;
        int isolatedEPawns = 0;
        int mobility = 0;
        int mobilityE = 0;

        /* Symmetric evaluation function:
         * f(p) = 200(K-K')
         *        + 9(Q-Q')
         *        + 5(R-R')
         *        + 3(B-B' + N-N')
         *        + 1(P-P')
         *        - 0.5(D-D' + S-S' + I-I')
         *        + 0.1(M-M') + ...
         *
         * KQRBNP = number of kings, queens, rooks, bishops, knights and pawns
         * D,S,I = doubled, blocked and isolated pawns
         * M = Mobility (the number of legal moves)
         */

        boolean[] pawnPresence = new boolean[8];
        boolean[] pawnEPresence = new boolean[8];
        for (int col = 0; col < 8; col++) {
            boolean columnHasPawn = false;
            boolean columnHasEPawn = false;
            for (int row = 0; row < 8; row++) {
                Piece p = board.findPieceByLocation(row, col);
                if (p==null) {
                    continue;
                }
                boolean sameColor = p.isWhite() == color;
                if (sameColor) {
                    switch(p.getType()) {
                        case "king":
                            numK++;
                            break;
                        case "queen":
                            numQ++;
                            break;
                        case "rook":
                            numR++;
                            break;
                        case "bishop":
                            numB++;
                            break;
                        case "knight":
                            numN++;
                            break;
                        case "pawn":
                            numP++;
                            // double pawns - when two or more pawns of same color are on the same column
                            if(columnHasPawn) doubledPawns++;
                            columnHasPawn = true;

                            // blocked pawns - pawns that can't move forward because another piece (regardless of color) is directly in front of it
                            int forwardDirection = color ? -1 : 1;
                            int nextRow = forwardDirection + row;
                            if (nextRow >= 0 && nextRow < 8
                                    && board.findPieceByLocation(nextRow,col)!=null) {
                                blockedPawns++;
                            }
                    }
                } else {
                    switch(p.getType()) {
                        case "king":
                            numEK++;
                            break;
                        case "queen":
                            numEQ++;
                            break;
                        case "rook":
                            numER++;
                            break;
                        case "bishop":
                            numEB++;
                            break;
                        case "knight":
                            numEN++;
                            break;
                        case "pawn":
                            numEP++;
                            // double pawns - when two or more pawns of same color are on the same column
                            if(columnHasPawn) doubledEPawns++;
                            columnHasPawn = true;

                            // blocked pawns - pawns that can't move forward because another piece (regardless of color) is directly in front of it
                            int forwardDirection = color ? -1 : 1;
                            int nextRow = forwardDirection + row;
                            if (nextRow >= 0 && nextRow < 8
                                    && board.findPieceByLocation(nextRow,col)!=null) {
                                blockedEPawns++;
                            }
                    }
                }

            }
            pawnPresence[col] = columnHasPawn;
            pawnEPresence[col] = columnHasEPawn;
        }

        // Check for isolates pawns - pawns that don't have any pawns on their left column and right column
        for (int i = 0; i < 8; i++) {
            boolean isoPawn = true;
            boolean isoEPawn = true;
            if (i > 0) { // check left column
                if(pawnPresence[i - 1]) isoPawn = false;
                if(pawnEPresence[i - 1]) isoEPawn = false;
            }
            if (i < 7) { // check right column
                if(pawnPresence[i + 1]) isoPawn = false;
                if(pawnPresence[i + 1]) isoEPawn = false;
            }
            if(isoPawn) isolatedPawns++;
            if(isoEPawn) isolatedEPawns++;
        }

        // Update mobility
        mobility = countMobility(board, color);
        mobilityE = countMobility(board, !color);


        score = 2000 * (numK - numEK) +
                90 * (numQ - numEQ) +
                50 * (numR - numER) +
                30 * ((numB - numEB) + (numN - numEN)) +
                10 * (numP - numEP) -
                5 * ((doubledPawns - doubledEPawns) +
                        (blockedPawns - blockedEPawns) +
                        (isolatedPawns - isolatedEPawns)) +
                1 * (mobility - mobilityE);

        return score;
    }

    public int countMobility(Board board, boolean color) {
        int count = 0;
        HashMap<Piece, ArrayList<int[]>> moves = board.getAllPossibleMoves(color);
        for (Piece p : moves.keySet()) {
            ArrayList<int[]> pMoves = new ArrayList<>(moves.get(p));
            count += pMoves.size();
        }
        return count;
    }


    /*
    Below are simpler versions of the above search and evaluation methods,
    kept for benchmark testing and learning documentation purposes.
    Note: None are used in the actual game.
    */

    public Move getBestMoveMinimax(Board board) {
        int bestEval = MIN;
        Move bestMove = null;

        long start = System.nanoTime();
        totalNodes = 0;
        totalTime = 0;

        HashMap<Piece, ArrayList<int[]>> moves = board.getAllPossibleMoves(color);

        for (Piece piece : moves.keySet()) {
            ArrayList<int[]> pMoves = new ArrayList<>(moves.get(piece));
            for (int[] move : pMoves) {
                Move currMove = new Move(piece, move[0], move[1]);
                board.movePiece(currMove);
                int eval = minimax(board, depth - 1, color); // negative --> good for opponent = bad for us
                board.undoLastMove();
                if (eval > bestEval) {
                    bestEval = eval;
                    bestMove = currMove;
                }
            }
        }

        totalTime = (System.nanoTime() - start);

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
        totalNodes++;
        if (depth == 0) {
            return basicMinimaxEvaluation(board);
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

    Map<String, Integer> pieceValues = Map.of(
            "pawn", 1,
            "knight", 3,
            "bishop", 3,
            "rook", 5,
            "queen", 9
    );

    public int basicMinimaxEvaluation(Board board) {
        int whiteEval = countMaterial(true, board);
        int blackEval = countMaterial(false, board);

        return whiteEval - blackEval;
    }

    public int basicNegamaxEvaluation(Board board, boolean color) {
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
