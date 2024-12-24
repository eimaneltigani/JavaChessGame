package main;

import board.Board;
import board.Piece;
import player.Player;

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
    Player currPlayer;

    Piece selectedPiece;
    ArrayList<int[]> legalMoves;


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
    public void updateBoard(Board board, Player player) {
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

        switchPlayer(player);
    }

    public void switchPlayer(Player player) {
        // if first update
        if(currPlayer == null) {
            currPlayer = player;
        } else {
            currPlayer = player.getOpponent();
        }

        // if current player is human, enable clicks
        if(currPlayer.getColor() == WHITE) {
            enableUserClicks();
        } else {
            disableUserClicks();
        }

        // update players turn panel
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

        Piece piece = clickedButton.getPiece();

        // users first click for turn
        if (selectedPiece == null || piece.isWhite() == selectedPiece.isWhite()) {
            if (selectedPiece != null) {
                removeHighlight();
            }

            selectedPiece = piece;
            legalMoves = selectedPiece.legalMoves();
            highlightLegalMoves();
        } else {
            // they're moving pieces
            currPlayer.makeMove(selectedPiece, row, col);
            selectedPiece = null;
            removeHighlight();
        }


        System.out.println("Clicked row: " + row + ", col: " + col);
    }

    public void highlightLegalMoves() {

        if(legalMoves == null) {
            System.out.println("no legal moves for selected piece, pick another one!");
        } else {
            for (int[] move : legalMoves) {
                int x = move[0];
                int y = move[1];
                PieceButton button = buttons[x][y];
                button.highlightBackground();
            }
        }

    }

    public void removeHighlight() {
        for (int[] move : legalMoves) {
            int x = move[0];
            int y = move[1];
            PieceButton button = buttons[x][y];
            button.removeHighlight();
        }

        legalMoves = null;
    }
}
