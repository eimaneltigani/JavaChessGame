package player;

import board.Board;
import main.ChessGUI;

public class HumanPlayer implements Player {
    ChessGUI gui;
    Board board;
    int	playerColor;

    public HumanPlayer() {
        this.playerColor = 0;
        gui = new ChessGUI();
    }

    public void update(Board board)
    {
        gui.updateBoard(board, playerColor);
    }

    public int getColor() {
        return playerColor;
    }
}
