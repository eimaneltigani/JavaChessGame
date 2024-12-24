package main;

import board.Board;
import player.ComputerPlayer;
import player.HumanPlayer;
import player.Player;

import javax.swing.*;



public class Main {
    public static void startGame(Player p1) {
        Board board = new Board();
        p1.update(board, p1);
    }

    public static void main(String[] args) {

        Player p1 = new HumanPlayer();
        Player p2 = new ComputerPlayer();
        p1.setOpponent(p2);
        p2.setOpponent(p1);
        startGame(p1);

    }
}