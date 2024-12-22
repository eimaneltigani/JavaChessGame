import javax.swing.*;
import java.awt.*;

/**
 * Use this class as game screen
 */
public class GamePanel extends JPanel {

    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
    }

    private void update() {

    }

    // Method in JComponent that JPanel inherits and is used to draw objects on the panel
    // aka hands all the drawing stuff (board, pieces, on-screen messages, etc.)
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}
