
package org.madast;

import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

public class Guard {

    private int x;
    private final int y;
    private final int size = 32;

    private final int startX;
    private boolean movingRight = true;

    // Parametry pola widzenia
    private static final int VISION_RANGE = 220;
    private static final double VISION_ANGLE = 60.0;

    // Parametry patrolowania
    private static final int PATROL_DISTANCE = 128;
    private static final int SPEED = 1;

    private double visionDirection = 0.0;
    private static final double ROTATION_SPEED = 3.0;
    private boolean turning = false;

    public Guard(int startX, int startY) {
        this.startX = startX;
        this.x = startX;
        this.y = startY;
    }

    public void update() {

        // Jeśli strażnik się obraca, nie porusza się
        if (turning) {

            double targetAngle = movingRight ? 0.0 : 180.0;
            double difference = targetAngle - visionDirection;

            if (Math.abs(difference) <= ROTATION_SPEED) {
                visionDirection = targetAngle;
                turning = false;
            } else {
                visionDirection += Math.signum(difference) * ROTATION_SPEED;
            }

            return;
        }

        // Ruch patrolujący
        if (movingRight) {

            x += SPEED;

            if (x >= startX + PATROL_DISTANCE) {
                x = startX + PATROL_DISTANCE;

                movingRight = false;
                turning = true;
            }

        } else {

            x -= SPEED;

            if (x <= startX) {
                x = startX;

                movingRight = true;
                turning = true;
            }
        }
    }

    // Sprawdzanie, czy gracz znajduje się w polu widzenia
    public boolean canSeePlayer(Player player, int[][] map, int tileSize) {

        double guardX = x + size / 2.0;
        double guardY = y + size / 2.0;

        double playerX = player.getX() + player.getSize() / 2.0;
        double playerY = player.getY() + player.getSize() / 2.0;

        double dx = playerX - guardX;
        double dy = playerY - guardY;

        double distance = Math.sqrt(dx * dx + dy * dy);

        // Sprawdzenie zasięgu
        if (distance > VISION_RANGE) {
            return false;
        }

        if (distance == 0) {
            return true;
        }

        // Kierunek patrzenia strażnika
        double radians = Math.toRadians(visionDirection);

        double directionX = Math.cos(radians);
        double directionY = Math.sin(radians);

        // Iloczyn skalarny wektorów
        double dotProduct =
                (dx * directionX + dy * directionY) / distance;

        double minDotProduct =
                Math.cos(Math.toRadians(VISION_ANGLE / 2.0));

        // Sprawdzenie kąta widzenia
        if (dotProduct < minDotProduct) {
            return false;
        }

        // Sprawdzenie, czy ściana nie blokuje widoczności
        return hasLineOfSight(guardX, guardY, playerX, playerY, map, tileSize);
    }

    // Sprawdzanie przeszkód pomiędzy strażnikiem a graczem
    private boolean hasLineOfSight(
            double x1,
            double y1,
            double x2,
            double y2,
            int[][] map,
            int tileSize
    ) {

        int minCol = Math.max(0,
                (int) Math.floor(Math.min(x1, x2) / tileSize) - 1);

        int maxCol = Math.min(map[0].length - 1,
                (int) Math.floor(Math.max(x1, x2) / tileSize) + 1);

        int minRow = Math.max(0,
                (int) Math.floor(Math.min(y1, y2) / tileSize) - 1);

        int maxRow = Math.min(map.length - 1,
                (int) Math.floor(Math.max(y1, y2) / tileSize) + 1);

        for (int row = minRow; row <= maxRow; row++) {
            for (int col = minCol; col <= maxCol; col++) {

                // 1 oznacza ścianę
                if (map[row][col] == 1) {

                    Rectangle wall = new Rectangle(
                            col * tileSize,
                            row * tileSize,
                            tileSize,
                            tileSize
                    );

                    // Sprawdzenie przecięcia odcinka ze ścianą
                    if (wall.intersectsLine(x1, y1, x2, y2)) {
                        return false;
                    }
                }
            }
        }

        return true;
    }


    // Rysowanie pola widzenia z uwzględnieniem ścian
    public void drawVision(Graphics2D g2d, int[][] map, int tileSize) {

        double centerX = x + size / 2.0;
        double centerY = y + size / 2.0;

        double directionAngle = visionDirection;

        double startAngle = directionAngle - VISION_ANGLE / 2.0;
        double endAngle = directionAngle + VISION_ANGLE / 2.0;

        Path2D.Double visionCone = new Path2D.Double();

        // Początek stożka znajduje się w środku strażnika
        visionCone.moveTo(centerX, centerY);

        // Wysyłamy promienie co 0.5 stopnia
        for (double angle = startAngle; angle <= endAngle + 0.001; angle += 0.5) {

            Point2D.Double endpoint = castRay(
                    centerX,
                    centerY,
                    angle,
                    map,
                    tileSize
            );

            visionCone.lineTo(endpoint.x, endpoint.y);
        }

        visionCone.closePath();

        // Wypełnienie pola widzenia
        g2d.setColor(new Color(255, 0, 0, 55));
        g2d.fill(visionCone);

        // Kontur pola widzenia
        g2d.setColor(new Color(255, 0, 0, 130));
        g2d.draw(visionCone);
    }

    // Wyznaczanie końca pojedynczego promienia
    private Point2D.Double castRay(
            double startX,
            double startY,
            double angle,
            int[][] map,
            int tileSize
    ) {

        double radians = Math.toRadians(angle);

        double directionX = Math.cos(radians);
        double directionY = Math.sin(radians);

        double lastX = startX;
        double lastY = startY;

        // Przesuwamy promień co 1 piksel
        for (double distance = 0; distance <= VISION_RANGE; distance += 1.0) {

            double rayX = startX + directionX * distance;
            double rayY = startY + directionY * distance;

            int col = (int) Math.floor(rayX / tileSize);
            int row = (int) Math.floor(rayY / tileSize);

            // Wyjście poza mapę również blokuje widoczność
            if (row < 0 || row >= map.length ||
                    col < 0 || col >= map[0].length) {

                return new Point2D.Double(lastX, lastY);
            }

            // 1 oznacza ścianę
            if (map[row][col] == 1) {
                return new Point2D.Double(lastX, lastY);
            }

            lastX = rayX;
            lastY = rayY;
        }

        // Nie znaleziono przeszkody w zasięgu widzenia
        return new Point2D.Double(
                startX + directionX * VISION_RANGE,
                startY + directionY * VISION_RANGE
        );
    }


    public void draw(Graphics2D g2d) {

        g2d.setColor(Color.RED);
        g2d.fillRect(x, y, size, size);

        g2d.setColor(Color.DARK_GRAY);
        g2d.drawRect(x, y, size, size);
    }

    // Kolizja fizyczna strażnika z graczem
    public boolean checkCollision(Player player) {

        Rectangle guardRect = new Rectangle(x, y, size, size);

        Rectangle playerRect = new Rectangle(
                player.getX(),
                player.getY(),
                player.getSize(),
                player.getSize()
        );

        return guardRect.intersects(playerRect);
    }
}
