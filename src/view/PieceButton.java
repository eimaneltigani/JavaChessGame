package view;

import model.Piece;

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
    Color tileColor;
    Color backgroundColor;

    public PieceButton(int row, int col, Color color) {
        this.row = row;
        this.col = col;
        this.tileColor = color;
        this.backgroundColor = tileColor;
        setBackground(backgroundColor);
        setFocusPainted(false);  // Prevent focus outline
        setContentAreaFilled(false); // Let paintComponent handle the background
    }

    public void setPiece(Piece p) {
        this.piece = p;
        if (p==null) {
            isEmpty = true;
            image = null; // clear image
        } else {
            isEmpty = false;
            image = p.image;
        }

        repaint(); // Request re-rendering to display the piece
    }

    public Piece getPiece() {
        return this.piece;
    }

    public void changeBackground(Color color) {
        this.backgroundColor = color;
        repaint(); // Request re-rendering to apply the new background
    }

    public void removeHighlight() {
        this.backgroundColor = tileColor;
        repaint(); // Request re-rendering to apply the new background
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
