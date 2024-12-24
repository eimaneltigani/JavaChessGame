package player;

import board.Board;
import main.ChessGUI;

public class ComputerPlayer implements Player {

    ChessGUI gui;
    Board board;
    int	playerColor;

    public void update(Board board)
    {
        gui.updateBoard(board);
    }

    public int getColor() {
        return playerColor;
    }
}
