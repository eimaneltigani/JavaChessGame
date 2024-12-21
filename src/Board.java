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
        board[7][0] = new Tile(null,7,0);
        board[7][1] = new Tile(null,7,1);
        board[7][2] = new Tile(null,7,2);
        board[7][3] = new Tile(null,7,3);
        board[7][4] = new Tile(null,7,4);
        board[7][5] = new Tile(null,7,5);
        board[7][6] = new Tile(null,7,6);
        board[7][7] = new Tile(null,7,7);

        // Set white pawns
        for (int i=0;i<8;i++) {
            board[6][i] = new Tile(null,6,i);
        }

        // Set empty rows
        for (int row = 2; row < 6; row++) {
            for (int col = 0; col < 8; col ++) {
                board[row][col] = new Tile(null, row, col);
            }
        }

        // Set black pawns
        for (int i=0;i<8;i++) {
            board[1][i] = new Tile(null,1,i);
        }

        // Set black piece row
        board [0][0] = new Tile(null,0,0);
        board [0][1] = new Tile(null,0,1);
        board [0][2] = new Tile(null,0,2);
        board [0][3] = new Tile(null,0,3);
        board [0][4] = new Tile(null,0,4);
        board [0][5] = new Tile(null,0,5);
        board [0][6] = new Tile(null,0,6);
        board [0][7] = new Tile(null,0,7);

    }

    public Tile[][] getBoard() {
        return board;
    }

    public void setBoard(Tile[][] board) {
        this.board = board;
    }
}
