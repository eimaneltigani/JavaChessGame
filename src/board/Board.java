package board;

import pieces.*;

public class Board {
    Tile[][] board;

    /**
     * Constructor. Initializes board to class chess start position
     */
    public Board() {
        this.board = new Tile[8][8]; // Define chess board as 2D array
        initializeBoard();
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
        board[7][0] = new Tile(new Rook(white),7,0);
        board[7][1] = new Tile(new Knight(white),7,1);
        board[7][2] = new Tile(new Bishop(white),7,2);
        board[7][3] = new Tile(new Queen(white),7,3);
        board[7][4] = new Tile(new King(white),7,4);
        board[7][5] = new Tile(new Bishop(white),7,5);
        board[7][6] = new Tile(new Knight(white),7,6);
        board[7][7] = new Tile(new Rook(white),7,7);

        // Set white pawns
        for (int i=0;i<8;i++) {
            board[6][i] = new Tile(new Pawn(white),6,i);
        }

        // Set empty rows
        for (int row = 2; row < 6; row++) {
            for (int col = 0; col < 8; col ++) {
                board[row][col] = new Tile(null, row, col);
            }
        }

        // Set black pawns
        for (int i=0;i<8;i++) {
            board[1][i] = new Tile(new Pawn(black),1,i);
        }

        // Set black piece row
        board [0][0] = new Tile(new Rook(black),0,0);
        board [0][1] = new Tile(new Knight(black),0,1);
        board [0][2] = new Tile(new Bishop(black),0,2);
        board [0][3] = new Tile(new Queen(black),0,3);
        board [0][4] = new Tile(new King(black),0,4);
        board [0][5] = new Tile(new Bishop(black),0,5);
        board [0][6] = new Tile(new Knight(black),0,6);
        board [0][7] = new Tile(new Rook(black),0,7);

    }

    public Tile[][] getBoard() {
        return board;
    }

    public void setBoard(Tile[][] board) {
        this.board = board;
    }

}
