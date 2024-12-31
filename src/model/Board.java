package model;

import model.pieces.*;

import java.util.ArrayList;
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
    private Stack<Move> lastMoves = new Stack<>();
    private King whiteKing;
    private King blackKing;

    /**
     * Constructor.
     */
    public Board() {
        board = new Piece[8][8]; // Define chess board as 2D array
        initializeBoard();
        populateLists();
    }

    /**
     * Initializes board to classic chess start position
     */
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

    /**
     * Adds all of each team's model pieces to their respective list
     */
    private void populateLists() {
        allPieces = new ArrayList<>();
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();
        capturedPieces = new ArrayList<>();

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
    }

    public Piece findPieceByLocation(int row, int col) {
        return board[row][col];
    }

    public void movePiece(Move move) {
        Piece p = move.getPiece();
        int currRow = move.getCurrRow();
        int currCol = move.getCurrCol();

        int targetRow = move.getTargetRow();
        int targetCol = move.getTargetCol();

        // if capturing piece, update array lists
        if(board[targetRow][targetCol] != null) {
            move.setCaptured(true);
            Piece capturedPiece = board[targetRow][targetCol];
            if(capturedPiece.isWhite()) {
                whitePieces.remove(capturedPiece);
            } else {
                blackPieces.remove(capturedPiece);
            }
            capturedPieces.add(capturedPiece);
            allPieces.remove(capturedPiece);
        }

        // update Piece coordinates
        p.setRow(targetRow);
        p.setCol(targetCol);
        // if first move, set false
        if(p.isFirstMove()) {
            p.setFirstMove(false);
        }

        // if castling
        if (p instanceof King && Math.abs(targetCol - currCol) == 2) {
            boolean kingside = targetCol > currCol;
            int rookCurrCol = kingside ? 7 : 0;
            int rookTargetCol = kingside ? targetCol-1 : targetCol+1;

            Piece rook = findPieceByLocation(currRow, rookCurrCol);
            rook.setCol(rookTargetCol);
            rook.setFirstMove(false);

            board[currRow][rookTargetCol] = rook;
            board[currRow][rookCurrCol] = null;
        }


        // update board
        board[targetRow][targetCol] = board[currRow][currCol];
        board[currRow][currCol] = null;

        // save last move
        lastMoves.add(move);

//        // check if move puts opposite king in check
//        if(inCheck(!p.isWhite())) {
//            King oppositKing = p.isWhite() ? blackKing : whiteKing;
//            oppositKing.markCheck(true);
//        }
    }

    public HashMap<Piece, ArrayList<int[]>> getAllPossibleMoves(boolean color) {
        HashMap<Piece, ArrayList<int[]>> map = new HashMap<>();
        ArrayList<Piece> playerPieces = color ? whitePieces : blackPieces;

        for (Piece p : playerPieces) {
            ArrayList<int[]> legalMoves = new ArrayList<>();
            ArrayList<int[]> pseudoLegalMoves = p.availableMoves(this);
            for (int[] move : pseudoLegalMoves) {
                if (!putsKingInDanger(p, move[0], move[1])) {
                    legalMoves.add(move);
                }
            }

            map.put(p, legalMoves);
        }

        return map;
    }


    private boolean putsKingInDanger(Piece p, int targetRow, int targetCol) {
        // simulate move
        movePiece(new Move(p, targetRow, targetCol));

        if(inCheck(p.isWhite())) {
            return true;
        }
        // undo move
        undoLastMove();

        return false;
    }

    public void undoLastMove() {
        if(lastMoves.isEmpty()) {
            return;
        }

        Move lastMove = lastMoves.pop();

        int prevRow = lastMove.currRow;
        int prevCol = lastMove.currCol;
        int currRow = lastMove.getTargetRow();
        int currCol = lastMove.getTargetCol();
        Piece movedPiece = lastMove.getPiece();
        Piece capturedPiece = null;
        if (lastMove.isCaptured()) {
            capturedPiece = capturedPieces.get(capturedPieces.size()-1);
            // update lists
            allPieces.add(capturedPiece);
            if(capturedPiece.isWhite()) {
                whitePieces.add(capturedPiece);
            } else {
                blackPieces.add(capturedPiece);
            }
        }

        board[currRow][currCol] = capturedPiece;
        board[prevRow][prevCol] = movedPiece;
        movedPiece.setRow(prevRow);
        movedPiece.setCol(prevCol);

        // extra steps in case it was an elements first move
        if (movedPiece instanceof King && Math.abs(currCol-prevCol) == 2) {
            // set king's first move as true
            movedPiece.setFirstMove(true);

            // put rook back in original position
            if (currCol > prevCol) {
                // kingside
                board[currRow][7] = board[currRow][5];
                board[currRow][7].setFirstMove(true);
                board[currRow][7].setCol(7);
                board[currRow][5] = null;
            } else {
                board[currRow][0] = board[currRow][3];
                board[currRow][0].setFirstMove(true);
                board[currRow][0].setCol(0);
                board[currRow][3] = null;
            }
        }

        if(movedPiece instanceof Pawn) {
            if(prevRow == 1 || prevRow == 6) {
                movedPiece.setFirstMove(true);
            }
        }
    }

    public boolean inCheck(boolean isWhite) {
        King currKing = isWhite ? whiteKing : blackKing;
        int[] kingLocation = new int[]{currKing.getRow(), currKing.getCol()};
        ArrayList<Piece> oppositePieces = isWhite ? blackPieces : whitePieces;
        for(Piece opp : oppositePieces) {
            ArrayList<int[]> potentialMoves = opp.availableMoves(this);
            if(potentialMoves.contains(kingLocation)) {
                return true;
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

//    public void setCapturedPieces(ArrayList<Piece> capturedPieces) {
//        this.capturedPieces = capturedPieces;
//    }

    public ArrayList<Piece> getCapturedPieces() {
        return capturedPieces;
    }

    public Stack<Move> getLastMove() {
        return lastMoves;
    }

}
