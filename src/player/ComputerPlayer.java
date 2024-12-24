package player;

import board.Board;
import board.Piece;
import main.ChessGUI;

public class ComputerPlayer implements Player {

    ChessGUI gui;
    Board board;
    int	playerColor;
    Player opponent;

    public ComputerPlayer() {
        this.playerColor = 1;
    }

    public void makeMove(Piece piece, int targetRow, int targetCol) {

    };

    @Override
    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    public Player getOpponent() {
        return this.opponent;
    }

    public void update(Board board, Player player)
    {
        gui.updateBoard(board, player);
    }
    public int getColor() {
        return playerColor;
    }
}
