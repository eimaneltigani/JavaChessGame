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
    HashMap<Piece, ArrayList<int[]>> availablePiecesToMove;
    Move currentMove;
    boolean handlingPromotion;
    boolean color;
    boolean inCheck;

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
            inCheck = true;
            gui.setCheck(board.getKing(color));
        }

        // enable clicks for user pieces
        availablePiecesToMove = board.getAllPossibleMoves(color);
        if(availablePiecesToMove.isEmpty()) {
            if (inCheck) { System.out.println("~~~~~~Games over, human is defeated ~~~~~~~~~"); }
            else { System.out.println("~~~~~~Games over, it's a draw ~~~~~~~~~"); }
        }
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
    public void onClick(int row, int col) {
        Piece piece = board.findPieceByLocation(row, col);

        // If selecting piece for first time
        if (selectedPiece == null && piece.isWhite()) {
            selectedPiece = piece;
            gui.highlightLegalMoves(availablePiecesToMove.get(selectedPiece));
        } else if (piece!= null && piece.isWhite()) {
            // if user is selecting another piece to move
            gui.removeHighlight(availablePiecesToMove.get(selectedPiece));
            selectedPiece = piece;
            gui.highlightLegalMoves(availablePiecesToMove.get(selectedPiece));
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
        // Update model board
        b.movePiece(move);

        // Update GUI
        SwingUtilities.invokeLater(() -> {
            gui.disableUserClicks();
            gui.removeHighlight(availablePiecesToMove.get(selectedPiece));
            gui.update(move);

            // If captured, remove piece from board
            if (move.isCaptured()) {
                gui.updateCapturedPanel(b.getCapturedPieces());
            }
            // If castling, update additional rook move
            if(b.isCastling(move)) {
                Move castlingMove = b.getLastMove();
                gui.update(castlingMove);
            }
            // if pawn promotion, display options panel to let user pick new piece/move
            if(b.isPromotePawn(move)) {
                handlingPromotion = true;
                while (handlingPromotion) {
                    currentMove = move;
                    gui.showPromotionalPanel();
                }
            }
            // If king was in check, undo check
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

            gui.setTurn(false); // switch to Computer player
        });
    }

    @Override
    public boolean getColor() {
        return color;
    }
}
