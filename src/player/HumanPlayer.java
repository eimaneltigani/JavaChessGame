package player;

import board.Board;
import main.ChessGUI;

public class HumanPlayer implements Player {
    ChessGUI gui;
    Board board;
    int	playerColor;

    public HumanPlayer() {
        gui = new ChessGUI();
        this.playerColor = 0;
    }
    public void update(Board board)
    {
        gui.updateBoard(board);
    }

    public int getColor() {
        return playerColor;
    }
}
