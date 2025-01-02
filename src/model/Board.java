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

    public void movePiece(Move move) {
        Piece p = move.getPiece();
        ArrayList<Piece> playerPieces = p.isWhite() ? whitePieces : blackPieces;
        ArrayList<Piece> opponentPieces = p.isWhite() ? blackPieces : whitePieces;
        int currRow = move.getCurrRow();
        int currCol = move.getCurrCol();
        int targetRow = move.getTargetRow();
        int targetCol = move.getTargetCol();

        // First check if capturing opponent piece before updating target dest
        Piece capturedPiece = board[targetRow][targetCol];
        if(capturedPiece != null && capturedPiece.isWhite()!=p.isWhite()) { // update lists accordingly
            move.setCaptured(true);
            opponentPieces.remove(capturedPiece);
            allPieces.remove(capturedPiece);
            capturedPieces.add(capturedPiece);
        }

        // Update piece coordinates
        p.setRow(targetRow);
        p.setCol(targetCol);
        board[currRow][currCol] = null;
        board[targetRow][targetCol] = p;

        // if first move, set false
        if(p.isFirstMove()) {
            p.setFirstMove(false);
        }

        // if castling, find and update rook too
        if (p instanceof King && Math.abs(targetCol - currCol) == 2) {
            boolean kingside = targetCol > currCol;
            int rookCurrCol = kingside ? 7 : 0;
            int rookTargetCol = kingside ? targetCol-1 : targetCol+1;

            Piece rook = findPieceByLocation(currRow, rookCurrCol);
            rook.setCol(rookTargetCol);
            board[currRow][rookCurrCol] = null;
            board[currRow][rookTargetCol] = rook;
            rook.setFirstMove(false);
        }

        // if promotion piece, add new piece to respective lists
        if (currRow==targetRow && currCol==targetCol) {
            playerPieces.add(p);
            allPieces.add(p);
        }

        // save last move
        lastMoves.add(move);

        // handle extra promotion move for computer
        if (p instanceof Pawn && !p.isWhite() && p.getRow() == 7) {
            // computer picks Queen everytime
            Piece newPiece = new Queen(false, targetRow, targetCol);
            movePiece(new Move(newPiece, targetRow, targetCol));
        }
    }


    // Function to return all possible moves for each piece of specified color
    public HashMap<Piece, ArrayList<int[]>> getAllPossibleMoves(boolean color) {
        HashMap<Piece, ArrayList<int[]>> map = new HashMap<>();
        ArrayList<Piece> playerPieces = color ? whitePieces : blackPieces;

        for (Piece p : playerPieces) {
            ArrayList<int[]> legalMoves = new ArrayList<>();
            // Generate ALL moves of a given piece first
            ArrayList<int[]> pseudoLegalMoves = p.availableMoves(this);
            // Add to legal moves after checking that it is safe
            for (int[] move : pseudoLegalMoves) {
                if (!putsKingInDanger(p, move[0], move[1])) {
                    legalMoves.add(move);
                }
            }
            map.put(p, legalMoves);
        }

        return map;
    }

    // Function to check if moving a piece will put players king in danger
    private boolean putsKingInDanger(Piece p, int targetRow, int targetCol) {

        movePiece(new Move(p, targetRow, targetCol)); // simulate move

        if (inCheck(p.isWhite())) {
            return true;
        }

        undoLastMove(); // undo move

        return false;
    }

    public void undoLastMove() {
        if(lastMoves.isEmpty()) return;

        Move lastMove = lastMoves.pop();
        ArrayList<Piece> playerPieces = lastMove.piece.isWhite() ? whitePieces : blackPieces;
        ArrayList<Piece> opponentPieces = lastMove.piece.isWhite() ? blackPieces : whitePieces;

        // special handling for pawn promotion, remove additional piece and move
        if(lastMove.getCurrRow() == lastMove.getTargetRow() && lastMove.getCurrCol() == lastMove.getTargetCol()) {
            Piece promotion = lastMove.getPiece();
            allPieces.remove(promotion);
            playerPieces.remove(promotion);
            lastMove = lastMoves.pop();
        }

        int prevRow = lastMove.currRow;
        int prevCol = lastMove.currCol;
        int currRow = lastMove.getTargetRow();
        int currCol = lastMove.getTargetCol();
        Piece movedPiece = lastMove.getPiece();

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


        // revert castling
        if (movedPiece instanceof King && Math.abs(currCol-prevCol) == 2) {
            boolean kingside = prevCol > 4;
            int rookCurrCol = kingside ? currCol + 1 : currCol - 1;
            int rookTargetCol = kingside ? 7 : 0;
            // find rook
            Piece rook = board[currRow][rookCurrCol];
            rook.setCol(rookTargetCol);
            board[currRow][rookTargetCol] = rook;
            board[currRow][currCol] = null;
            // reset rook and king's first move as true
            rook.setFirstMove(true);
            movedPiece.setFirstMove(true);
        } else if(movedPiece instanceof Pawn) {
            if((prevRow == 1 && movedPiece.isWhite()) || (prevRow == 6 && !movedPiece.isWhite())) {
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

    public ArrayList<Piece> getCapturedPieces() {
        return capturedPieces;
    }

    public Stack<Move> getLastMove() {
        return lastMoves;
    }
}
