package main;

import board.Board;
import board.Piece;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Use this class as game screen
 */
public class GamePanel extends JPanel implements Runnable {

    public static final int WIDTH = 1100;
    public static final int HEIGHT = 800;
    // we call the two methods update() and paintComponent() 60 times per second so we can refresh the screen
    // so first we set FPS
    final int FPS = 60;
    // use thread class to run game loop
    Thread gameThread;
    ChessBoard view = new ChessBoard();

    // PIECES
    public static ArrayList<Piece> pieces;
    public static ArrayList<Piece> simPieces = new ArrayList<>();

    // COLOR
    public static final int WHITE = 0;
    public static final int BLACK = 1;
    int currentColor = WHITE;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);

        // set board
        Board board = new Board();
        pieces = board.getAllPieces();
        board.copyBoard(pieces, simPieces);
    }

    //instantiate thread
    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start(); // calls run method
    }


    @Override
    public void run() {
        // GAME LOOP - use System.nanoTime() to measure elapsed time and call update
        // and repaint methods once every 1/60 of a second
        double drawInterval = (double) 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while(gameThread != null) {
            currentTime = System.nanoTime();

            delta += (currentTime - lastTime)/drawInterval;
            lastTime = currentTime;
            if(delta >= 1) {
                update();
                repaint();
                delta--;
            }
        }
    }

    // going to handle all the update information like x,y positions
    // or the number of pieces left on the board, etc.
    private void update() {

    }

    // Method in JComponent that JPanel inherits and is used to draw objects on the panel
    // aka hands all the drawing stuff (board, pieces, on-screen messages, etc.)
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D)g;

        // BOARD
        view.draw(g2);

        // PIECES
        for (Piece p : simPieces) {
            p.draw(g2);
        }
    }
}
