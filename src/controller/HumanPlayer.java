package controller;

import model.Board;
import model.Move;
import model.Piece;
import model.pieces.*;
import view.ChessGUI;
import view.ChessGUI.ClickListener;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;

public class HumanPlayer implements Player, ClickListener {
    ChessGUI gui;
    Board board;
    Piece selectedPiece;
    ArrayList<int[]> legalMoves;
    Move currentMove;
    boolean handlingPromotion;
    boolean color;

    public HumanPlayer() {
        this.color = true;
    }

    public void initializeBoard(ChessGUI gui) {
        this.gui = gui;
    }

    public Move decideMove(Board b) {
        currentMove = null; // reset move before starting
        board = b;
        selectedPiece = null;

        if (board.inCheck(color)) {
            gui.setCheck(board.getKing(color));
        }

        // enable clicks for user pieces
        HashMap<Piece, ArrayList<int[]>> availablePiecesToMove = board.getAllPossibleMoves(color);
        gui.enableUserClicks(availablePiecesToMove);
        gui.setTurn(true);

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
        Piece piece = board.findPieceByLocation(row, col);

        // If selecting piece for first time
        if (selectedPiece == null && piece.isWhite()) {
            selectedPiece = piece;
            legalMoves = new ArrayList<>(selectedPiece.availableMoves(board));
            gui.highlightLegalMoves(legalMoves);
        } else if (piece!= null && piece.isWhite()) {
            // if user is selecting another piece to move
            gui.removeHighlight(legalMoves);
            selectedPiece = piece;
            legalMoves = new ArrayList<>(selectedPiece.availableMoves(board));
            gui.highlightLegalMoves(legalMoves);
        } else {
            currentMove = new Move(selectedPiece, row, col);
        }
    }

    @Override
    public void handlePromotionSelection(String piece) {

        // Create new promotional piece based off user selection
        Piece promotionalPiece = switch (piece) {
            case "queen" -> new Queen(true, currentMove.getTargetRow(), currentMove.getTargetCol());
            case "rook" -> new Rook(true, currentMove.getTargetRow(), currentMove.getTargetCol());
            case "bishop" -> new Bishop(true, currentMove.getTargetRow(), currentMove.getTargetCol());
            case "knight" -> new Knight(true, currentMove.getTargetRow(), currentMove.getTargetCol());
            default -> null;
        };

        // Create new move and apply to the board
        Move promotionalMove = new Move(promotionalPiece, currentMove.getTargetRow(), currentMove.getTargetCol());
        board.movePiece(promotionalMove);
        gui.update(promotionalMove);
        handlingPromotion = false;

    }


    public void update(Board b, Move move) {
        // need to deactivate red background if king WAS in check last round but no longer is
        // also made me think about what if checkmate, move would be equal to null here?
        b.movePiece(move);

        Move castlingMove;
        if(b.getLastMove()!=move) {
            castlingMove = b.getLastMove();
        } else {
            castlingMove = null;
        }

        SwingUtilities.invokeLater(() -> {
            gui.disableUserClicks();
            gui.removeHighlight(legalMoves);
            gui.update(move);
            if (move.isCaptured()) {
                gui.updateCapturedPiecePanel(b.getCapturedPieces());
            }
            if(castlingMove!=null) { // update extra castling move (rook)
                gui.update(castlingMove);
            }
        });


        // handle pawn promotion
        if(move.getPiece() instanceof Pawn && move.getTargetRow() == 0) {
            handlingPromotion = true;
            while (handlingPromotion) {
                currentMove = move;
                gui.showPromotionalPanel();
            }
        }
    }

    @Override
    public boolean getColor() {
        return color;
    }
}
