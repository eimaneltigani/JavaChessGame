package model;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;

/**
 * Abstract class to represent common functionality of all chess model.pieces
 */
public abstract class Piece {
    String type;
    boolean isWhite;
    public int row, col;
    protected boolean firstMove;
    public BufferedImage image;

    /**
     * Constructor
     * @param typeIn
     * @param isWhiteIn
     * @param col
     * @param row
     */
    public Piece(String typeIn, boolean isWhiteIn, int row, int col) {
        this.type = typeIn;
        this.isWhite = isWhiteIn;
        this.col = col;
        this.row = row;
        this.firstMove = true;
        char colorChar = isWhite ? 'w' : 'b';
        String imagePath = MessageFormat.format("/piece/{0}-{1}", colorChar, typeIn);
        this.image = getImage(imagePath);
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

    public abstract boolean canMove(int targetCol, int targetRow);

    public abstract ArrayList<int[]> availableMoves(Board board);

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

    public void setRow(int row) {
        this.row = row;
    }

    public int getRow() {
        return row;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getCol() {
        return col;
    }

    public boolean getOppositeColor() {
        return !this.isWhite;
    }

    public void setFirstMove(boolean b) {
        firstMove = b;
    }

    public boolean isFirstMove() {
        return firstMove;
    }

    public boolean isWithinBounds(int row, int col) {
        return row >= 0 && row <8 && col >= 0 && col < 8;
    }
}
