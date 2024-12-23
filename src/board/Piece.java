package board;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.MessageFormat;

import main.ChessBoard;
import main.GamePanel;

/**
 * Abstract class to represent common functionality of all chess pieces
 */
public abstract class Piece {
    String type;
    boolean isWhite;
    public BufferedImage image;
    public int x, y;
    public int col, row, preCol, preRow;
    public int color;

    /**
     * Constructor
     * @param typeIn
     * @param isWhiteIn
     * @param col
     * @param row
     */
    public Piece(String typeIn, boolean isWhiteIn, int row, int col) {
        this.type = typeIn;
        char colorChar = isWhiteIn ? 'w' : 'b';
        String imagePath = MessageFormat.format("/piece/{0}-{1}", colorChar, typeIn);
        this.image = getImage(imagePath);
        this.color = isWhiteIn ? GamePanel.WHITE : GamePanel.BLACK;
        this.col = col;
        this.row = row;
        x = getX(col);
        y = getY(row);
        preCol = col;
        preRow = row;
    }

    public BufferedImage getImage(String imagePath) {

        BufferedImage image = null;

        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return image;
    }

    // create methods to find x and y coordinates of square
    public int getX(int col) {
        return col * ChessBoard.SQUARE_SIZE;
    }
    public int getY(int row) {
        return row * ChessBoard.SQUARE_SIZE;
    }

    public void draw(Graphics2D g2) {
        g2.drawImage(image, x, y, ChessBoard.SQUARE_SIZE, ChessBoard.SQUARE_SIZE, null);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isWhite() {
        return isWhite;
    }

    public void setWhite(boolean isWhite) {
        this.isWhite = isWhite;
    }

    public abstract boolean canMove(int targetCol, int targetRow);

}
