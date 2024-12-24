package main;

import board.Board;
import board.Piece;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class ChessGUI {

    JFrame window;
    JPanel gamePanel;
    JPanel boardPanel;
    JPanel sidePanel;
    JPanel playerPanel;
    JPanel piecePanel;

    PieceButton[][] buttons = new PieceButton[8][8];


    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    final int MAX_ROW = 8;
    final int MAX_COL = 8;

    //Color
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;
    boolean firstUpdate = true;
    ArrayList<Piece> pieces;


    public ChessGUI() {
        window = new JFrame("Chess Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Shut down program on window close or else it'll keep running
        window.setResizable(false);

        // set game panel dimensions
        gamePanel = new JPanel();
        gamePanel.setLayout(new BorderLayout());
        gamePanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));
        gamePanel.setBackground(Color.BLACK);

        // create board panel
        configureBoardPanel();
        gamePanel.add(boardPanel, BorderLayout.WEST);

        //configure side panel
        configureSidePanel();
        gamePanel.add(sidePanel, BorderLayout.EAST);
        System.out.println(Arrays.toString(buttons));

        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null); // window will show up at center of monitor
        window.setVisible(true);
    }

    public void configureBoardPanel() {
        boardPanel = new JPanel(new GridLayout(8,8,0,0));
        boardPanel.setPreferredSize(new Dimension(800,800));

        // Draw dress board
        boolean isWhite = true;
        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++) {
                Color color = isWhite ? new Color(210, 165, 125) : new Color(175, 115, 70);
                buttons[row][col] = new PieceButton(row, col, color);
                buttons[row][col].addActionListener(e -> handlePieceClick(e));
                boardPanel.add(buttons[row][col]);
                isWhite = !isWhite; // Alternate color
            }
            isWhite = !isWhite; // Alternate color for next row
        }
    }

    public void configureSidePanel() {
        sidePanel = new JPanel();
        sidePanel.setPreferredSize(new Dimension(300,800));
        sidePanel.setBackground(Color.GRAY);
    }

    public void updateSidePanel(ArrayList<Piece> capturedPieces) {
        // should be updated after each kill
    }


    // called after player updates model
    public void updateBoard(Board board, int playerColor) {
        Piece[][] modelBoard = board.getBoard();
        // iterate through game board and update to match model board
        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++) {
                Piece modelPiece = modelBoard[row][col];
                if(buttons[row][col].getPiece()!=modelPiece) {
                    buttons[row][col].setPiece(modelPiece);
                }
            }
        }

        // once update is complete that means player has made their move so update current player
        // if current player is human (white), switch to computer and deactivate click functionality
        if (playerColor == WHITE && !firstUpdate) {
            currentColor = BLACK;
            disableUserClicks();
        } else {
            currentColor = WHITE;
            enableUserClicks();
        }

        if(firstUpdate) {
            firstUpdate = false;
        }
    }

    // enable user clicks on white pieces
    public void enableUserClicks() {
        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++) {
                Piece piece = buttons[row][col].getPiece();
                if (piece != null && piece.isWhite()) {
                    buttons[row][col].setEnabled(true); // Enable buttons for white pieces
                } else {
                    buttons[row][col].setEnabled(false); // Disable buttons for other squares
                }
            }
        }
    }

    // Disable all clicks during the computer's turn
    public void disableUserClicks() {
        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++) {
                buttons[row][col].setEnabled(false); // Disable all buttons
            }
        }
    }

    // Handle user clicks on the board
    private void handlePieceClick(ActionEvent e) {
        PieceButton clickedButton = (PieceButton) e.getSource();
        int row = clickedButton.row;
        int col = clickedButton.col;

        System.out.println("Clicked row: " + row + ", col: " + col);
    }
}
