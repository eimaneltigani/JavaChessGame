package model;

import model.pieces.*;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;


/**
 * Game board data structure.
 */
public class Board {

    private Piece[][] board;
    private ArrayList<Piece> allPieces;
    private ArrayList<Piece> whitePieces;
    private ArrayList<Piece> blackPieces;
    private ArrayList<Piece> capturedPieces;
    private Stack<Move> lastMoves;
    private King whiteKing;
    private King blackKing;


    public Board() {
        board = new Piece[8][8]; // Define chess board as 2D array
        initializeBoard();
        populateLists();
    }

    // Initializes board to classic chess start position
    private void initializeBoard() {

        /*
         * (row,col)
         * (0,0) ... ... ... (0,7)
         * ...           ...
         * ...           ...
         * ...       ...
         * (5,0)     ...
         * (6,0) ...
         * (7,0) ... ... ... (7,7)
         */

        boolean white = true;
        boolean black = false;
        whiteKing = new King(white,7,4);
        blackKing = new King(black,0,4);

        // Set white piece row
        board[7][0] = new Rook(white,7,0);
        board[7][1] = new Knight(white,7,1);
        board[7][2] = new Bishop(white,7,2);
        board[7][3] = new Queen(white,7,3);
        board[7][4] = whiteKing;
        board[7][5] = new Bishop(white,7,5);
        board[7][6] = new Knight(white,7,6);
        board[7][7] = new Rook(white,7,7);

        // Set white pawns
        for (int i=0;i<8;i++) {
            board[6][i] = new Pawn(white,6,i);
        }

        // Set empty rows
        for (int row = 2; row < 6; row++) {
            for (int col = 0; col < 8; col ++) {
                board[row][col] = null;
            }
        }

        // Set black pawns
        for (int i=0;i<8;i++) {
            board[1][i] = new Pawn(black,1,i);
        }

        // Set black piece row
        board [0][0] = new Rook(black,0,0);
        board [0][1] = new Knight(black,0,1);
        board [0][2] = new Bishop(black,0,2);
        board [0][3] = new Queen(black,0,3);
        board [0][4] = blackKing;
        board [0][5] = new Bishop(black,0,5);
        board [0][6] = new Knight(black,0,6);
        board [0][7] = new Rook(black,0,7);
    }

    // Adds each team's model pieces to their respective list
    private void populateLists() {
        allPieces = new ArrayList<>();
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                Piece p = board[row][col];
                if(p != null) {
                    if(p.isWhite()) {
                        whitePieces.add(p);
                    } else {
                        blackPieces.add(p);
                    }
                    allPieces.add(p);
                }
            }
        }

        // additional lists
        capturedPieces = new ArrayList<>();
        lastMoves = new Stack<>();
    }

    public Piece findPieceByLocation(int row, int col) {
        return board[row][col];
    }


    // Function to eliminate the possible moves that will put the king in danger
    public HashMap<Piece, ArrayList<int[]>> getAllPossibleMoves(boolean color) {
        if (inCheck(color)) {
            System.out.println(color + "player is in check!");
        }

        HashMap<Piece, ArrayList<int[]>> map = new HashMap<>();
        ArrayList<Piece> playerPieces = new ArrayList<>(color ? whitePieces : blackPieces); // Cloning Collections for Safe Iteration

        for (Piece p : playerPieces) {
            ArrayList<int[]> legalMoves = new ArrayList<>();
            // Generate ALL moves of a given piece first
            ArrayList<int[]> pseudoLegalMoves = new ArrayList<>(p.availableMoves(this)); // Create copy of moves
            // Add to legal moves after checking that it is safe
            for (int[] move : pseudoLegalMoves) {
                if (!putsKingInDanger(p, move[0], move[1])) {
                    legalMoves.add(move);
                }
            }
            if(!legalMoves.isEmpty()) {
                map.put(p, legalMoves);
            }
        }

        return map;
    }

    // Function to check if moving a piece will put players king in danger
    private boolean putsKingInDanger(Piece p, int targetRow, int targetCol) {
        boolean inCheck = false;

        movePiece(new Move(p, targetRow, targetCol)); // simulate move

        if (inCheck(p.isWhite())) {
            inCheck = true;
        }

        undoLastMove(); // undo move

        return inCheck;
    }


    public void movePiece(Move move) {
        Piece p = move.getPiece();
        int currRow = move.getCurrRow();
        int currCol = move.getCurrCol();
        int targetRow = move.getTargetRow();
        int targetCol = move.getTargetCol();

        ArrayList<Piece> playerPieces = p.isWhite() ? whitePieces : blackPieces;
        ArrayList<Piece> opponentPieces = p.isWhite() ? blackPieces : whitePieces;

        // Handle piece removal by updating lists
        Piece capturedPiece = board[targetRow][targetCol];
        if(capturedPiece != null) {
            if (capturedPiece.isWhite()!=p.isWhite()) { // capturing opponent piece
                move.setCaptured(true);
                opponentPieces.remove(capturedPiece);
                capturedPieces.add(capturedPiece);
            } else { // replacing own piece - pawn promotion
                playerPieces.add(p); // add queen or whatever promotion
                allPieces.add(p);
                playerPieces.remove(capturedPiece); // remove pawn
            }
            allPieces.remove(capturedPiece);
        }

        // Update piece coordinates
        p.setRow(targetRow);
        p.setCol(targetCol);
        board[currRow][currCol] = null;
        board[targetRow][targetCol] = p;
        lastMoves.add(move);


        // Keep track of first move if King, Rook, or Pawn
        if (p instanceof King king) {
            king.addMove(move);
        } else if (p instanceof Rook rook) {
            rook.addMove(move);
        } else if (p instanceof Pawn pawn && pawn.getFirstMove()) {
            pawn.setFirstMove(false);
        }


        /* Handling special cases that require an additional move  **/

        // 1. Castling, create and process additional move for Rook
        if (isCastling(move)) {
            boolean kingside = targetCol > currCol;
            int rookCurrCol = kingside ? 7 : 0;
            int rookTargetCol = kingside ? targetCol-1 : targetCol+1;
            Piece rook = findPieceByLocation(currRow, rookCurrCol);
            movePiece(new Move(rook, currRow, rookTargetCol));
        }

        // 2. Handle extra promotion move for computer
        // Note - Don't need to do it for Human Player because it'll be passed in controller since user must select piece first
        if (!p.isWhite() && isPromotePawn(move)) {
            // computer picks Queen everytime
            Piece newPiece = new Queen(false, targetRow, targetCol);
            movePiece(new Move(newPiece, targetRow, targetCol));
        }
    }

    public boolean isCastling(Move move) {
        Piece p = move.getPiece();
        int currCol = move.getCurrCol();
        int targetCol = move.getTargetCol();

        return p instanceof King && Math.abs(targetCol - currCol) == 2;
    }

    // this is first move of promotion where pawn reaches end of board
    public boolean isPromotePawn(Move move) {
        Piece p = move.getPiece();
        boolean color = p.isWhite();
        int expectedRow = color ? 0 : 7;

        return p instanceof Pawn && p.getRow() == expectedRow;
    }

    public void undoLastMove() {
        if(lastMoves.isEmpty()) return;

        Move lastMove = lastMoves.pop();
        Piece movedPiece = lastMove.getPiece();
        int prevRow = lastMove.getCurrRow();
        int prevCol = lastMove.getCurrCol();
        int currRow = lastMove.getTargetRow();
        int currCol = lastMove.getTargetCol();

        ArrayList<Piece> playerPieces = lastMove.piece.isWhite() ? whitePieces : blackPieces;
        ArrayList<Piece> opponentPieces = lastMove.piece.isWhite() ? blackPieces : whitePieces;

        // bring back captured
        Piece capturedPiece = null;
        if (lastMove.isCaptured()) {
            capturedPiece = capturedPieces.get(capturedPieces.size()-1);

            allPieces.add(capturedPiece);
            opponentPieces.add(capturedPiece);
            capturedPieces.remove(capturedPiece);
        }

        // restore piece position and board
        movedPiece.setRow(prevRow);
        movedPiece.setCol(prevCol);
        board[currRow][currCol] = capturedPiece;
        board[prevRow][prevCol] = movedPiece;


        // for firstMove instances
        if(movedPiece instanceof Pawn pawn) {
            if((prevRow == 1 && !movedPiece.isWhite()) || (prevRow == 6 && movedPiece.isWhite())) {
                pawn.setFirstMove(true);
            }
        } else if (movedPiece instanceof King king) {
            king.removeLastMove();
        } else if (movedPiece instanceof Rook rook) {
            rook.removeLastMove();
        }

        // undo both moves for special cases
        if(movedPiece instanceof Rook && !lastMoves.isEmpty() && isCastling(lastMoves.peek())) { // castling
            undoLastMove();
        } else if(prevRow == currRow && prevCol == currCol) { // if pawn upgrade, remove promotion piece and undo move
            allPieces.remove(movedPiece);
            playerPieces.remove(movedPiece);
            undoLastMove();
        } else if (isPromotePawn(lastMove)) { // if original pawn, add back to board
            playerPieces.add(movedPiece);
            allPieces.add(movedPiece);
        }
    }

    public boolean inCheck(boolean isWhite) {
        King currKing = isWhite ? whiteKing : blackKing;
        int[] kingsLocation = new int[]{currKing.getRow(), currKing.getCol()};
        ArrayList<Piece> opponentPieces = new ArrayList<>(isWhite ? blackPieces : whitePieces);
        for(Piece opp : opponentPieces) {
            ArrayList<int[]> potentialMoves = new ArrayList<>(opp.availableMoves(this));
            for (int[] move : potentialMoves) {
                if (Arrays.equals(move, kingsLocation)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isCheckMate(boolean color) {
        if(inCheck(color)) {
            return getAllPossibleMoves(color).isEmpty();
        }

        return false;
    }

    public void copyBoard(ArrayList<Piece> source, ArrayList<Piece> target) {
        target.clear();
        target.addAll(source);
    }

    public void setBoard(Piece[][] board) {
        this.board = board;
    }

    public Piece[][] getBoard() {
        return board;
    }

    public void setAllPieces(ArrayList<Piece> allPieces) {
        this.allPieces = allPieces;
    }

    public ArrayList<Piece> getAllPieces() {
        return allPieces;
    }

    public void setWhitePieces(ArrayList<Piece> whitePieces) {
        this.whitePieces = whitePieces;
    }

    public ArrayList<Piece> getWhitePieces() {
        return whitePieces;
    }

    public void setBlackPieces(ArrayList<Piece> blackPieces) {
        this.blackPieces = blackPieces;
    }

    public ArrayList<Piece> getBlackPieces() {
        return blackPieces;
    }

    public ArrayList<Piece> getCapturedPieces() {
        return capturedPieces;
    }

    public ArrayList<Piece> getPlayersPieces(boolean color) {
        return color ? whitePieces : blackPieces;
    }

    public Stack<Move> getLastMoves() {
        return lastMoves;
    }

    public Move getLastMove() {
        return lastMoves.peek();
    }

    /** for testing purposes **/
    public void placePiece(Piece p, int row, int col) {
        if(p!=null) {
            p.setRow(row);
            p.setCol(col);
        }
        board[row][col] = p;
    }

    public Piece getKing(boolean color) {
        return color ? whiteKing : blackKing;
    }


}
