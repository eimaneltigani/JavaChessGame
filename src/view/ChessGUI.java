package view;

import model.Board;
import model.Move;
import model.Piece;
import controller.Player;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

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
    JPanel promotionalPanel;
    JLayeredPane layeredPane;
    PieceButton[][] buttons = new PieceButton[8][8];
    boolean promotionIsVisible = false;
    //
    ClickListener clickListener;

    public interface ClickListener {
        void onClick(int row, int col, boolean captured);

        void handlePromotionSelection(String piece);
    }

    // actionPerformed method triggered on button click
    // delegates onClick action to class that implements ClickListener
    @Override
    public void actionPerformed(ActionEvent e) {

        if (promotionIsVisible) {
            JButton clickedButton = (JButton) e.getSource();
            String buttonName = clickedButton.getText();
            System.out.println("You clicked promotional button for: " + buttonName);
            if (clickListener != null) {
                clickListener.handlePromotionSelection(buttonName);
            }
            promotionalPanel.setVisible(false); // Hide after selection
            promotionIsVisible = false;
        } else {
            PieceButton clickedButton = (PieceButton) e.getSource();
            int row = clickedButton.row;
            int col = clickedButton.col;
            boolean captured = false;
            if (clickedButton.getPiece()!=null && clickedButton.getPiece().isWhite()==false) {
                captured = true;
            }

            if (clickListener != null) {
                clickListener.onClick(row, col, captured);
            }
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
        window.setSize(WIDTH, HEIGHT);
        window.setLayout(new BorderLayout());
        window.setResizable(false);

        // Main components
        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(800,800));
        layeredPane.setBounds(0,0,800,800);

        boardPanel = new JPanel(new GridLayout(8, 8, 0, 0));
        boardPanel.setPreferredSize(new Dimension(800,800));
        configureBoardPanel();

        promotionalPanel = new JPanel();
        promotionalPanel.setPreferredSize(new Dimension(800,300));
        promotionalPanel.setBounds(0,250,800,300);
        promotionalPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 50)); // Center the buttons
        configurePromotionPanel();
        promotionalPanel.setVisible(false); // hide initially

        sidePanel = new JPanel(new BorderLayout());
        sidePanel.setPreferredSize(new Dimension(300,800));
        configureSidePanel();

        // add components to layered pane
        layeredPane.add(boardPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(promotionalPanel, JLayeredPane.PALETTE_LAYER);

        window.add(layeredPane, BorderLayout.WEST);
        window.add(sidePanel, BorderLayout.EAST);
        window.pack();
        window.setLocationRelativeTo(null); // window will show up at center of monitor
        window.setVisible(true);
    }

    private void configurePromotionPanel() {
        String[] pieces = {"queen", "rook", "bishop", "knight"};

        for (String piece : pieces) {
            JButton button = new JButton();
            button.setText(piece);
            button.setPreferredSize(new Dimension(100,50));
            button.setBackground(Color.DARK_GRAY);
            button.addActionListener(this);
            promotionalPanel.add(button);
        }
    }

    public void showPromotion() {
        System.out.println("user just asked to display promotion panel");
        promotionalPanel.setVisible(true);
        promotionIsVisible = true;
    }


    // set buttons and background color for board tiles
    public void configureBoardPanel() {
        boardPanel.setBounds(0,0,800,800);

        boolean isWhite = true;
        for (int row = 0; row < MAX_ROW; row++) {
            for (int col = 0; col < MAX_COL; col++) {
                Color color = isWhite ? new Color(210, 165, 125) : new Color(175, 115, 70);
                PieceButton b = new PieceButton(row, col, color);
                buttons[row][col] = b;
                b.addActionListener(this);
                b.setEnabled(false);
                boardPanel.add(b);

                isWhite = !isWhite; // Alternate color
            }
            isWhite = !isWhite; // Alternate color for next row
        }
    }

    public void configureSidePanel() {

        // Top side panel displays current users turn
        playerPanel = new JPanel();
        playerPanel.setPreferredSize(new Dimension(300,200));
        playerPanel.setBackground(Color.GRAY);

        // Lower side panel displays captured pieces
        piecePanel = new JPanel();
        piecePanel.setPreferredSize(new Dimension(300,600));
        piecePanel.setLayout(new GridLayout(1,2));

        sidePanel.add(playerPanel, BorderLayout.NORTH);
        sidePanel.add(piecePanel, BorderLayout.SOUTH);

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
    public void enableUserClicks(HashMap<Piece, ArrayList<int[]>> availablePieces) {
        for (Piece p : availablePieces.keySet()) {
            ArrayList<int[]> movesForPiece = availablePieces.get(p);
            int row = p.getRow();
            int col = p.getCol();
            if(!movesForPiece.isEmpty()) {
                buttons[row][col].setEnabled(true);
            }
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

    // display promotion panel
    public void displayPromotionPanel() {
    }
}
