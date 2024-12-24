package main;

import board.Piece;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.MessageFormat;

public class PieceButton extends JButton {
    Piece piece;
    boolean isEmpty = true;
    public BufferedImage image;
    int row;
    int col;
    Color backgroundColor;

    public PieceButton(int row, int col, Color color) {
        this.row = row;
        this.col = col;
        this.backgroundColor = color;
        setBackground(backgroundColor);
        setBorderPainted(false); // Remove border for a cleaner look
        setFocusPainted(false);  // Prevent focus outline
        setContentAreaFilled(false); // Let paintComponent handle the background
    }

    public void setPiece(Piece p) {
        piece = p;
        isEmpty = false;
        char colorChar = p.isWhite() ? 'w' : 'b';
        String imagePath = MessageFormat.format("/piece/{0}-{1}", colorChar, p.getType());
        this.image = getImage(imagePath);
        repaint(); // Request re-rendering to display the piece
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

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Let JButton paint itself

        // Draw background color
        g.setColor(backgroundColor);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw the piece image, if any
        if (!isEmpty && image != null) {
            int x = (getWidth() - image.getWidth()) / 2;
            int y = (getHeight() - image.getHeight()) / 2;
            g.drawImage(image, x, y, null);
        }
    }
}
