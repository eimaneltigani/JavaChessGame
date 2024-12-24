package main;

import board.Board;
import player.HumanPlayer;
import player.Player;

import javax.swing.*;



public class Main {
    public static void startGame(Player p1) {
        Board board = new Board();
        p1.update(board);
    }

    public static void main(String[] args) {

        Player p1 = new HumanPlayer();
        startGame(p1);

    }
}