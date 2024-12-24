package player;

import board.Board;
import main.ChessGUI;

public class ComputerPlayer implements Player {

    ChessGUI gui;
    Board board;
    int	playerColor;

    public ComputerPlayer() {
        this.playerColor = 1;
    }

    public void update(Board board)
    {
        gui.updateBoard(board, playerColor);
    }

    public int getColor() {
        return playerColor;
    }
}
