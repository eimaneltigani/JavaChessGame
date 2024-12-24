package player;

import board.Board;
import board.Piece;
import main.ChessGUI;

public class HumanPlayer implements Player {
    ChessGUI gui;
    Board board;
    int	playerColor;
    Player opponent;

    public HumanPlayer() {
        this.playerColor = 0;
        gui = new ChessGUI();
    }

    public void makeMove(Piece piece, int targetRow, int targetCol) {

    };

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
