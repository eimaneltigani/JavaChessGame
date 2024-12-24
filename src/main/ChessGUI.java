package main;

import board.Board;
import board.Piece;

import javax.swing.*;
import java.awt.*;
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
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col <8; col++) {
                Color color = isWhite ? new Color(210, 165, 125) : new Color(175, 115, 70);
                buttons[row][col] = new PieceButton(row, col, color);
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


    // handle game updates (like updating positions)
    public void updateBoard(Board board) {
        for(Piece piece : board.getAllPieces()) {
            PieceButton button = buttons[piece.row][piece.col];
            button.setPiece(piece);
        }
    }
}
