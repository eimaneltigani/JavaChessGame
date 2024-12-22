import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        JFrame window = new JFrame("Simple Chess"); // Game title displayed on windows top bar
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Shut down program on window close or else it'll keep running
        window.setResizable(false);

        // Add GamePanel to window
        GamePanel gp = new GamePanel();
        window.add(gp);
        window.pack();

        window.setLocationRelativeTo(null); // window will show up at center of monitor
        window.setVisible(true);

        // call method after window is created
        gp.launchGame();
    }
}