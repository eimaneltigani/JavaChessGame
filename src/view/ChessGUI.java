package view;

import model.Board;
import model.Move;
import model.Piece;
import controller.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * The main view of the chess game.
 * All GUI Elements are declared, initialized and used in this class itself.
 */
public class ChessGUI implements ActionListener {

    // GUI components and specifications
    private static final int WIDTH = 1100;
    private static final int HEIGHT = 800;
    private static final int MAX_ROW = 8;
    private static final int MAX_COL = 8;
    JFrame window;
    JPanel gamePanel;
    JPanel boardPanel;
    JPanel sidePanel;
    JPanel playerPanel;
    JPanel piecePanel;
    PieceButton[][] buttons = new PieceButton[8][8];

    //
    ClickListener clickListener;

    public interface ClickListener {
        void onClick(int row, int col, boolean captured);
    }

    // actionPerformed method triggered on button click
    // delegates onClick action to class that implements ClickListener
    @Override
    public void actionPerformed(ActionEvent e) {
        PieceButton clickedButton = (PieceButton) e.getSource();
        int row = clickedButton.row;
        int col = clickedButton.col;
        boolean captured = false;
        if(clickedButton.getPiece()!=null && clickedButton.getPiece().isWhite()==false) {
            captured = true;
        }

        if (clickListener != null) {
            clickListener.onClick(row, col, captured);
        }
    }


    /**
     * Contructor for GUI
     * @param clicklistener - takes in class that extends clickListener (in this case, HumanPlayer)
     */
    public ChessGUI(ClickListener clicklistener) {
        this.clickListener = clicklistener;
        window = new JFrame("Chess Game");
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Shut down program on window close or else it'll keep running
        window.setResizable(false);

        // set up panels
        gamePanel = new JPanel(new BorderLayout());
        gamePanel.setPreferredSize(new Dimension(WIDTH, HEIGHT));

        boardPanel = new JPanel(new GridLayout(8, 8));
        configureBoardPanel();

        sidePanel = new JPanel(new GridLayout(2,1));
        configureSidePanel();

        gamePanel.add(boardPanel, BorderLayout.WEST);
        gamePanel.add(sidePanel, BorderLayout.EAST);

        window.add(gamePanel);
        window.pack();
        window.setLocationRelativeTo(null); // window will show up at center of monitor
        window.setVisible(true);
    }


    // set buttons and background color for board tiles
    public void configureBoardPanel() {
        boardPanel.setPreferredSize(new Dimension(800,800));

        boolean isWhite = true;
        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++) {
                Color color = isWhite ? new Color(210, 165, 125) : new Color(175, 115, 70);
                PieceButton b = new PieceButton(row, col, color);
                buttons[row][col] = b;
                b.addActionListener(this);
                boardPanel.add(b);

                isWhite = !isWhite; // Alternate color
            }
            isWhite = !isWhite; // Alternate color for next row
        }
    }

    public void configureSidePanel() {
        sidePanel.setPreferredSize(new Dimension(300,800));
        sidePanel.setBackground(Color.GRAY);

        // Top side panel displays current users turn
        playerPanel = new JPanel();
        playerPanel.setPreferredSize(new Dimension(300,200));

        // Lower side panel displays captured pieces
        piecePanel = new JPanel();
        piecePanel.setPreferredSize(new Dimension(300,600));
        piecePanel.setLayout(new GridLayout(2,1));

        sidePanel.add(playerPanel);
        sidePanel.add(piecePanel);

    }

    public void updatePiecePanel(ArrayList<Piece> capturedPieces) {
        // should be updated after each kill
        JPanel whitePanel1 = new JPanel();
        whitePanel1.setLayout(new BoxLayout(whitePanel1, BoxLayout.Y_AXIS));
        JPanel blackPanel1 = new JPanel();
        blackPanel1.setLayout(new BoxLayout(blackPanel1, BoxLayout.Y_AXIS));

        ArrayList<Piece> capturedWhite = new ArrayList<>();
        ArrayList<Piece> capturedBlack = new ArrayList<>();

        for (Piece p : capturedPieces) {
            if(p.isWhite()) {
                capturedWhite.add(p);
            } else {
                capturedBlack.add(p);
            }
        }

        for(Piece p : capturedWhite) {
            ImageIcon imageIcon = new ImageIcon(p.image);
            JLabel label = new JLabel(imageIcon);
            whitePanel1.add(label);
        }

        for(Piece p : capturedBlack) {
            ImageIcon imageIcon = new ImageIcon(p.image);
            JLabel label = new JLabel(imageIcon);
            blackPanel1.add(label);
        }

        piecePanel.removeAll();
        piecePanel.add(whitePanel1);
        piecePanel.add(blackPanel1);

        piecePanel.repaint();
        piecePanel.revalidate();
    }

    // called as first update when model is created
    public void initializeBoard(Board board) {
        ArrayList<Piece> allPieces = board.getAllPieces();
        for (Piece p : allPieces) {
            int row = p.getRow();
            int col = p.getCol();
            buttons[row][col].setPiece(p);
        }
    }


    // only update board based of last move
    public void update(Move move) {
        Piece pieceMoved = move.getPiece();

        int prevRow = move.getCurrRow();
        int prevCol = move.getCurrCol();
        buttons[prevRow][prevCol].setPiece(null);

        int endRow = move.getTargetRow();
        int endCol = move.getTargetCol();
        buttons[endRow][endCol].setPiece(pieceMoved);
    }


    // called when users turn, enable clicks on user pieces
    public void enableUserClicks(ArrayList<Piece> availablePieces) {
        for (Piece p : availablePieces) {
            int row = p.getRow();
            int col = p.getCol();
            buttons[row][col].setEnabled(true);
        }
    }

    // Disable clicks after users turn is complete
    public void disableUserClicks() {
        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++) {
                buttons[row][col].setEnabled(false);
            }
        }

    }

    // A function that highlights the possible moves of specific piece and allows user to click it
    public void highlightLegalMoves(ArrayList<int[]> legalMoves) {
        if(legalMoves == null) {
            System.out.println("no legal moves for selected piece, pick another one!");
        } else {
            for (int[] move : legalMoves) {
                int x = move[0];
                int y = move[1];
                PieceButton button = buttons[x][y];
                button.highlightBackground();
                button.setEnabled(true);
            }
        }

    }

    // remove highlights and clicks is user clicks another piece or completes move
    public void removeHighlight(ArrayList<int[]> legalMoves) {
        for (int[] move : legalMoves) {
            int x = move[0];
            int y = move[1];
            PieceButton button = buttons[x][y];
            button.removeHighlight();
            button.setEnabled(false);
        }
    }


}
