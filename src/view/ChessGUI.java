package view;

import controller.Player;
import model.Board;
import model.Move;
import model.Piece;

import javax.swing.*;
import javax.swing.border.LineBorder;
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

    JFrame window;
    JPanel boardPanel;
    JPanel sidePanel;
    JPanel playerPanel;
    JPanel humanPanel;
    JPanel computerPanel;
    JPanel piecePanel;
    JLabel turnLabel;
    JPanel promotionalPanel;
    JLayeredPane layeredPane;
    PieceButton[][] buttons = new PieceButton[8][8];
    boolean promotionIsVisible = false;
    ClickListener clickListener;


    private static final int WIDTH = 1100;
    private static final int HEIGHT = 800;
    private static final int MAX_ROW = 8;
    private static final int MAX_COL = 8;

    // Create separate interface to handle click actions
    public interface ClickListener {

        void onClick(int row, int col, boolean captured);
        void handlePromotionSelection(String piece);

    }

    /**
     * Constructor - takes in click listener (in this case, it will be the user player) to process user input
     * in separate class to allow controller to handle game logic
      * @param clicklistener
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
        configureBoardPanel();

        promotionalPanel = new JPanel();
        configurePromotionPanel();

        sidePanel = new JPanel(new BorderLayout());
        configureSidePanel();

        // Add components to layered pane
        layeredPane.add(boardPanel, JLayeredPane.DEFAULT_LAYER);
        layeredPane.add(promotionalPanel, JLayeredPane.PALETTE_LAYER);

        // Add everything to frame
        window.add(layeredPane, BorderLayout.WEST);
        window.add(sidePanel, BorderLayout.EAST);
        window.pack();
        window.setLocationRelativeTo(null); // window will show up at center of monitor
        window.setVisible(true);
    }

    // Draw 8x8 board with alternating colors for each tile
    private void configureBoardPanel() {
        boardPanel.setPreferredSize(new Dimension(800,800));
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

    // Create layered pane to displayed on top of board if user reaches pawn promotion
    private void configurePromotionPanel() {
        promotionalPanel.setPreferredSize(new Dimension(800,300));
        promotionalPanel.setBounds(0,250,800,300);
        promotionalPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 50, 50)); // Center the buttons

        String[] pieces = {"queen", "rook", "bishop", "knight"};
        for (String piece : pieces) {
            JButton button = new JButton();
            button.setText(piece);
            button.setPreferredSize(new Dimension(100,50));
            button.setBackground(Color.DARK_GRAY);
            button.addActionListener(this);
            promotionalPanel.add(button);
        }

        promotionalPanel.setVisible(false); // hide initially
    }

    // Create side panel to display captured pieces and current players turn
    private void configureSidePanel() {
        sidePanel.setPreferredSize(new Dimension(300,800));

        // Top side panel displays current users turn
        playerPanel = new JPanel();
        playerPanel.setPreferredSize(new Dimension(300,200));
        playerPanel.setBackground(Color.GRAY);
        playerPanel.setLayout(new GridLayout(1,2));
        // create panels for each player
        humanPanel = createPlayerPanel("Human Player",  "res/piece/w-king.png");
        computerPanel = createPlayerPanel("Computer Player",  "res/piece/b-king.png");
        playerPanel.add(humanPanel);
        playerPanel.add(computerPanel);

        // Lower side panel displays captured pieces
        piecePanel = new JPanel();
        piecePanel.setPreferredSize(new Dimension(300,600));
        piecePanel.setLayout(new GridLayout(1,2));

        sidePanel.add(playerPanel, BorderLayout.NORTH);
        sidePanel.add(piecePanel, BorderLayout.SOUTH);
    }

    private JPanel createPlayerPanel(String playerName, String iconPath) {
        JPanel playerPanel = new JPanel(new GridBagLayout());
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));

        // Add vertical padding at the top
        playerPanel.add(Box.createVerticalGlue());

        JLabel iconLabel = new JLabel(new ImageIcon(iconPath));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerPanel.add(iconLabel);

        // Create and add the text label
        JLabel textLabel = new JLabel(playerName);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerPanel.add(textLabel);

        // Add vertical padding at the bottom
        playerPanel.add(Box.createVerticalGlue());

        // Add padding and set initial border
        playerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return playerPanel;
    }


    // GUI board initialized after model creation (called within Main)
    public void initializeBoard(Board board) {
        ArrayList<Piece> allPieces = board.getAllPieces();
        for (Piece p : allPieces) {
            int row = p.getRow();
            int col = p.getCol();
            buttons[row][col].setPiece(p);
        }
    }

    // Updates GUI based of last move
    public void update(Move move) {
        Piece pieceMoved = move.getPiece();

        int prevRow = move.getCurrRow();
        int prevCol = move.getCurrCol();
        buttons[prevRow][prevCol].setPiece(null);

        int endRow = move.getTargetRow();
        int endCol = move.getTargetCol();
        buttons[endRow][endCol].setPiece(pieceMoved);
    }

    // Updates captured piece panel after each kill
    public void updateCapturedPiecePanel(ArrayList<Piece> capturedPieces) {
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

    public void setTurn(boolean player) {
        if (player) {
            humanPanel.setBorder(new LineBorder(Color.GREEN, 3));
            computerPanel.setBorder(new LineBorder(Color.BLACK, 1));
        } else {
            humanPanel.setBorder(new LineBorder(Color.GREEN, 3));
            computerPanel.setBorder(new LineBorder(Color.BLACK, 1));
        }
    }


    // A function that highlights the possible moves after user click specified piece
    public void highlightLegalMoves(ArrayList<int[]> legalMoves) {
        if (legalMoves == null) {
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

    // Remove previous highlights after user is finished picking their move
    public void removeHighlight(ArrayList<int[]> legalMoves) {
        for (int[] move : legalMoves) {
            int x = move[0];
            int y = move[1];
            PieceButton button = buttons[x][y];
            button.removeHighlight();
            button.setEnabled(false);
        }
    }

    // Display promotion box for user
    public void showPromotionalPanel() {
        System.out.println("user just asked to display promotion panel");
        promotionalPanel.setVisible(true);
        promotionIsVisible = true;
    }

    // Enable clicks during user's turn
    public void enableUserClicks(HashMap<Piece, ArrayList<int[]>> availablePieces) {
        for (Piece p : availablePieces.keySet()) {
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
}
