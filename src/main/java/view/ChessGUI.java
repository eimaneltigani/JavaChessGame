package view;

import model.Board;
import model.Move;
import model.Piece;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

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

        void onClick(int row, int col);
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

    /** Private methods to configure GUI components **/

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
                b.setFocusable(false);
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
        humanPanel = createPlayerPanel("Human Player",  "w-king.png");
        computerPanel = createPlayerPanel("Computer Player",  "b-king.png");
        playerPanel.add(humanPanel);
        playerPanel.add(computerPanel);

        // Lower side panel displays captured pieces
        piecePanel = new JPanel();
        piecePanel.setPreferredSize(new Dimension(300,600));
        piecePanel.setLayout(new BorderLayout());
        configureCapturedPanel();

        sidePanel.add(playerPanel, BorderLayout.NORTH);
        sidePanel.add(piecePanel, BorderLayout.SOUTH);
    }

    // Top right panel will display both players
    private JPanel createPlayerPanel(String playerName, String iconPath) {
        JPanel playerPanel = new JPanel(new GridBagLayout());
        playerPanel.setLayout(new BoxLayout(playerPanel, BoxLayout.Y_AXIS));

        playerPanel.add(Box.createVerticalGlue());

        URL resource = getClass().getClassLoader().getResource(iconPath);
        JLabel iconLabel = new JLabel(new ImageIcon(resource));
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerPanel.add(iconLabel);

        JLabel textLabel = new JLabel(playerName);
        textLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        playerPanel.add(textLabel);

        playerPanel.add(Box.createVerticalGlue());

        playerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        return playerPanel;
    }

    // Bottom left panel will display both teams captured pieces
    private void configureCapturedPanel() {
        JPanel titlePanel = new JPanel();
        titlePanel.setPreferredSize(new Dimension(300, 100));

        JLabel titleLabel = new JLabel("Captured:");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setVerticalAlignment(SwingConstants.CENTER);
        titlePanel.setBackground(Color.GRAY);
        titlePanel.add(titleLabel);

        piecePanel.add(titlePanel, BorderLayout.NORTH);

        JPanel columnsPanel = new JPanel(new GridLayout(1,2));
        JPanel whitePanel = createCapturedColumn('w', 0);
        JPanel blackPanel = createCapturedColumn('b', 1);
        columnsPanel.add(whitePanel);
        columnsPanel.add(blackPanel);

        piecePanel.add(columnsPanel, BorderLayout.CENTER);
    }

    JLabel[][] capturedCountLabels = new JLabel[2][5];
    String[] pieceNames = {"pawn", "knight", "bishop", "rook", "queen"};

    private JPanel createCapturedColumn(char color, int row) {
        JPanel column = new JPanel(new GridLayout(5, 1));

        for (int i = 0; i <pieceNames.length; i++) {
            JPanel piecePanel = new JPanel();
            piecePanel.setLayout(new BorderLayout());

            // Add piece image
            JLabel pieceIcon = new JLabel();
            pieceIcon.setHorizontalAlignment(SwingConstants.CENTER);
            String imagePath = color + "-" + pieceNames[i] + ".png";
            URL resource = getClass().getClassLoader().getResource(imagePath);
            ImageIcon originalImage = new ImageIcon(resource);
            Image scaledImage = originalImage.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            pieceIcon.setIcon(new ImageIcon(scaledImage)); // Resize icon
            piecePanel.add(pieceIcon, BorderLayout.CENTER);

            JLabel capturedCount = new JLabel("0");
            capturedCount.setFont(new Font("Arial", Font.BOLD, 20));
            capturedCount.setForeground(Color.DARK_GRAY);
//            capturedCount.setHorizontalAlignment(SwingConstants.RIGHT);
            capturedCount.setBorder(BorderFactory.createEmptyBorder(0, 30, 0, 0));
            piecePanel.add(capturedCount, BorderLayout.NORTH);

            piecePanel.setBackground(Color.GRAY);

            column.add(piecePanel);

            capturedCountLabels[row][i] = capturedCount;
        }

        return column;
    }


    /* Public methods to modify view **/

    // Draw pieces on board initialized after model creation (called within Main)
    public void initializeBoard(Board board) {
        ArrayList<Piece> allPieces = board.getAllPieces();
        for (Piece p : allPieces) {
            int row = p.getRow();
            int col = p.getCol();
            buttons[row][col].setPiece(p);
        }
        setTurn(true);
    }

    /* Methods to be called by both Human and AI Player classes after updating model **/

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
    public void updateCapturedPanel(ArrayList<Piece> capturedPieces) {
        int[][] capturedCount = new int[2][5];

        for(Piece p : capturedPieces) {
            if(Objects.equals(p.getType(), "king")) {
                continue;
            }
            int row = p.isWhite() ? 0 : 1;
            int col = switch (p.getType()) {
                case "pawn" -> 0;
                case "knight" -> 1;
                case "bishop" -> 2;
                case "rook" -> 3;
                case "queen" -> 4;
                default -> throw new IllegalStateException("Unexpected value: " + p.getType());
            };

            capturedCount[row][col]++;
        }

        for(int i = 0; i < 2; i++) {
            for(int j = 0; j < 5; j++) {
                int count = capturedCount[i][j];
                capturedCountLabels[i][j].setText(String.valueOf(count));
            }
        }
    }

    public void setCheck(Piece king) {
        int row = king.getRow();
        int col = king.getCol();

        // find coordinating piece button
        PieceButton button = buttons[row][col];
        button.changeBackground(Color.RED);
    }

    public void setTurn(boolean player) {
        if (player) {
            humanPanel.setBorder(new LineBorder(Color.GREEN, 3));
            computerPanel.setBorder(new LineBorder(Color.BLACK, 1));
        } else {
            humanPanel.setBorder(new LineBorder(Color.BLACK, 1));
            computerPanel.setBorder(new LineBorder(Color.GREEN, 3));
        }
    }

    /* Below functions are only called in user class to enable user interaction **/

    // A function that highlights the possible moves after user selects piece to move
    public void highlightLegalMoves(ArrayList<int[]> legalMoves) {
        if (legalMoves == null) {
            System.out.println("no legal moves for selected piece, pick another one!");
        } else {
            for (int[] move : legalMoves) {
                int x = move[0];
                int y = move[1];
                PieceButton button = buttons[x][y];
                button.changeBackground(Color.GREEN);
                button.setEnabled(true);
            }
        }
    }

    // Remove previous highlights after user is finished picking their move or no longer in check
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

        if (promotionIsVisible) { // handle pawn promotion
            JButton clickedButton = (JButton) e.getSource();
            String buttonName = clickedButton.getText();
            System.out.println("You clicked promotional button for: " + buttonName);
            if (clickListener != null) {
                clickListener.handlePromotionSelection(buttonName);
            }
            promotionalPanel.setVisible(false); // Hide after selection
            promotionIsVisible = false;
        } else { // handle rest of game selections
            PieceButton clickedButton = (PieceButton) e.getSource();
            int row = clickedButton.row;
            int col = clickedButton.col;

            if (clickListener != null) {
                clickListener.onClick(row, col);
            }
        }

    }
}
