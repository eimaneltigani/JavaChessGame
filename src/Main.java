import model.Board;
import controller.ComputerPlayer;
import controller.HumanPlayer;
import controller.Player;
import model.Move;
import view.ChessGUI;


public class Main {
    public static void startGame(Player p1, Player p2) {
        Board board = new Board();
        p1.initializeBoard(board);
        p2.initializeBoard(board);
        Player currPlayer = p1;

        while (true) {
            Move move = currPlayer.decideMove(board);
            currPlayer.update(board, move);

            // currPlayer = currPlayer.isHuman() ? p2 : p1;
        }

    }

    public static void main(String[] args) {
        Player p1 = new HumanPlayer();
        Player p2 = new ComputerPlayer();

        startGame(p1, p2);

    }
}