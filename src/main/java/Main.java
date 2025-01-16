import model.Board;
import controller.AIPlayer;
import controller.HumanPlayer;
import controller.Player;
import model.Move;
import view.ChessGUI;


public class Main {
    public static void startGame(Player p1, Player p2) {
        ChessGUI gui = new ChessGUI((ChessGUI.ClickListener) p1);
        Board board = new Board();
        gui.initializeBoard(board);

        p1.initializeBoard(gui);
        p2.initializeBoard(gui);

        Player currPlayer = p1;

        while (true) {
            Move move = currPlayer.decideMove(board);
            if(move==null) {
                System.out.println("~~~~~~~~~~Game over: " + currPlayer.getColor() + " player defeated~~~~~~~~~~~");
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            currPlayer.update(board, move);

            currPlayer = currPlayer.getColor() ? p2 : p1;
        }

    }

    public static void main(String[] args) {
        Player p1 = new HumanPlayer();
        Player p2 = new AIPlayer();

        startGame(p1, p2);
    }
}