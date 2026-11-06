package org.madast;

import java.awt.*;

public class Guard {
    private int x;
    private final int y;
    private final int size = 32;

    // Pola do prostego ruchu patrolującego (w lewo i w prawo)
    private final int startX;
    private boolean movingRight = true;

    public Guard(int startX, int startY) {
        this.startX = startX;
        this.x = startX;
        this.y = startY;
    }

    public void update() {
        // Prosty ruch patrolujący tam i z powrotem
        int speed = 1;
        if (movingRight) {
            x += speed;
            // Zasięg ruchu w pikselach
            int patrolDistance = 128;
            if (x >= startX + patrolDistance) {
                movingRight = false;
            }
        } else {
            x -= speed;
            if (x <= startX) {
                movingRight = true;
            }
        }
    }

    public void draw(Graphics2D g2d) {
        // Strażnik w kolorze czerwonym
        g2d.setColor(Color.RED);
        g2d.fillRect(x, y, size, size);

        // Obramowanie strażnika
        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRect(x, y, size, size);
    }

    // Metoda sprawdzająca kolizję z graczem (AABB-Box Collision)
    public boolean checkCollision(Player player) {
        Rectangle guardRect = new Rectangle(x, y, size, size);
        Rectangle playerRect = new Rectangle(player.getX(), player.getY(), player.getSize(), player.getSize());
        return guardRect.intersects(playerRect);
    }
}