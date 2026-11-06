package org.madast;

import java.awt.*;

public class Player {
    private int x;
    private int y;
    private final int size = 32;

    public Player(int startX, int startY) {
        this.x = startX;
        this.y = startY;
    }

    public void draw(Graphics2D g2d) {
        g2d.setColor(Color.GREEN);
        g2d.fillRect(x, y, size, size);

        // Opcjonalnie: obramowanie gracza, żeby lepiej wyglądał
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRect(x, y, size, size);
    }

    // Gettery i settery potrzebne do kolizji i zmiany pokoi
    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getSize() {
        return size;
    }

    public int getSpeed() {
        return 10;
    }
}