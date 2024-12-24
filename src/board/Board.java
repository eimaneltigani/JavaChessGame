package board;

import pieces.*;

import java.util.ArrayList;


/**
 * Game board data structure.
 */
public class Board {
    Piece[][] board;
    ArrayList<Piece> allPieces;
    ArrayList<Piece> whitePieces;
    ArrayList<Piece> blackPieces;

    ArrayList<Piece> capturedPieces;


    /**
     * Constructor. Initializes board to class chess start position
     */
    public Board() {
        board = new Piece[8][8]; // Define chess board as 2D array
        initializeBoard();
        populateLists();
    }

    /**
     * Initializes board to classic chess start position
     */
    public void initializeBoard() {

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

        // Set white piece row
        board[7][0] = new Rook(white,7,0);
        board[7][1] = new Knight(white,7,1);
        board[7][2] = new Bishop(white,7,2);
        board[7][3] = new Queen(white,7,3);
        board[7][4] = new King(white,7,4);
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
        board [0][4] = new King(black,0,4);
        board [0][5] = new Bishop(black,0,5);
        board [0][6] = new Knight(black,0,6);
        board [0][7] = new Rook(black,0,7);

    }

    /**
     * Adds all of each team's pieces to their respective list
     */
    public void populateLists() {
        allPieces = new ArrayList<>();
        whitePieces = new ArrayList<>();
        blackPieces = new ArrayList<>();

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if(board[row][col] != null) {
                    if(board[row][col].isWhite()) {
                        whitePieces.add(board[row][col]);
                    } else {
                        blackPieces.add(board[row][col]);
                    }
                    allPieces.add(board[row][col]);
                }

            }
        }
    }

    public static void movePiece() {}

    public void copyBoard(ArrayList<Piece> source, ArrayList<Piece> target) {
        target.clear();
        for (int i = 0; i < source.size(); i++) {
            target.add(source.get(i));
        }
    }

    public Piece[][] getBoard() {
        return board;
    }

    public void setBoard(Piece[][] board) {
        this.board = board;
    }



    public void setAllPieces(ArrayList<Piece> allPieces) {
        this.allPieces = allPieces;
    }

    public ArrayList<Piece> getAllPieces() {
        return allPieces;
    }

}
