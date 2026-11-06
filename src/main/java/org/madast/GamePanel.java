package org.madast;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private final Player player = new Player(64, 64);

    // Lista przeciwników na planszy
    private final List<Guard> guards = new ArrayList<>();

    private boolean upPressed, downPressed, leftPressed, rightPressed, gameOver;
    private final int TILE_SIZE = 64;

    private final int[][][][] worldMaps = {
            // Wiersz 0 (górne pokoje)
            {
                    {
                            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                            {1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0},
                            {1, 0, 0, 0, 0, 1, 0, 0, 1, 1, 1, 0, 0},
                            {1, 0, 1, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0},
                            {1, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0},
                            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                            {1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1}
                    },
                    {
                            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
                            {0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1},
                            {0, 0, 1, 1, 1, 1, 0, 1, 0, 0, 0, 0, 1},
                            {0, 0, 0, 0, 0, 1, 0, 1, 0, 1, 1, 0, 1},
                            {0, 0, 1, 1, 0, 1, 0, 0, 0, 1, 0, 0, 1},
                            {1, 0, 0, 1, 0, 0, 0, 1, 1, 1, 0, 0, 1},
                            {1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1}
                    }
            },
            // Wiersz 1 (dolne pokoje)
            {
                    {
                            {1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1},
                            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {1, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0},
                            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
                            {1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1},
                            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
                    },
                    {
                            {1, 1, 1, 1, 1, 1, 0, 0, 1, 1, 1, 1, 1},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                            {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0, 1},
                            {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                            {1, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 1},
                            {1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1},
                            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1}
                    }
            }
    };

    private int roomX = 0;
    private int roomY = 0;
    private int[][] currentMap = worldMaps[roomY][roomX];

    public GamePanel() {
        this.setPreferredSize(new Dimension(currentMap[0].length * TILE_SIZE, currentMap.length * TILE_SIZE));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);

        // Dodajemy przykładowego strażnika na startowej mapie
        spawnGuards();

        Timer timer = new Timer(16, this);
        timer.start();
    }

    private void spawnGuards() {
        guards.clear();
        // Przykładowo w pokoju [0][0] dodajemy strażnika
        if (roomX == 0 && roomY == 0) {
            guards.add(new Guard(256, 192));
        } else if (roomX == 1 && roomY == 0) {
            guards.add(new Guard(384, 256));
        }
    }

    private boolean canMoveTo(int x, int y) {
        int leftCol = x / TILE_SIZE;
        int rightCol = (x + player.getSize() - 1) / TILE_SIZE;
        int topRow = y / TILE_SIZE;
        int bottomRow = (y + player.getSize() - 1) / TILE_SIZE;

        if (topRow < 0 || bottomRow >= currentMap.length || leftCol < 0 || rightCol >= currentMap[0].length) {
            return true;
        }

        return currentMap[topRow][leftCol] != 1 &&
                currentMap[topRow][rightCol] != 1 &&
                currentMap[bottomRow][leftCol] != 1 &&
                currentMap[bottomRow][rightCol] != 1;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);

        int mapWidth = currentMap[0].length * TILE_SIZE;
        int mapHeight = currentMap.length * TILE_SIZE;

        double scale = Math.min((double) getWidth() / mapWidth, (double) getHeight() / mapHeight);

        int offsetX = (int) ((getWidth() - (mapWidth * scale)) / 2);
        int offsetY = (int) ((getHeight() - (mapHeight * scale)) / 2);

        g2d.translate(offsetX, offsetY);
        g2d.scale(scale, scale);

        // Rysowanie mapy
        for (int row = 0; row < currentMap.length; row++) {
            for (int col = 0; col < currentMap[0].length; col++) {

                if (currentMap[row][col] == 0) {

                    g2d.setColor(Color.DARK_GRAY);

                    g2d.fillRect(
                            col * TILE_SIZE,
                            row * TILE_SIZE,
                            TILE_SIZE,
                            TILE_SIZE
                    );
                }
            }
        }

// Następnie rysujemy pola widzenia strażników
        for (Guard guard : guards) {
            guard.drawVision(g2d, currentMap, TILE_SIZE);
        }

// Ściany rysujemy ponownie na wierzchu stożków widzenia
        for (int row = 0; row < currentMap.length; row++) {
            for (int col = 0; col < currentMap[0].length; col++) {

                int tileX = col * TILE_SIZE;
                int tileY = row * TILE_SIZE;

                if (currentMap[row][col] == 1) {

                    g2d.setColor(Color.GRAY);
                    g2d.fillRect(tileX, tileY, TILE_SIZE, TILE_SIZE);

                    g2d.setColor(Color.DARK_GRAY);
                    g2d.drawRect(tileX, tileY, TILE_SIZE, TILE_SIZE);

                } else {

                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(tileX, tileY, TILE_SIZE, TILE_SIZE);
                }
            }
        }

// Rysowanie strażników
        for (Guard guard : guards) {
            guard.draw(g2d);
        }

// Rysowanie gracza
        player.draw(g2d);

// Ekran przegranej
        if (gameOver) {
            drawGameOver(g2d, mapWidth, mapHeight);
        }
    }


    @Override
    public void actionPerformed(ActionEvent e) {

        // Po przegranej zatrzymujemy aktualizację gry
        if (gameOver) {
            return;
        }

        // Ruch gracza
        int nextX = player.getX();
        int nextY = player.getY();

        if (upPressed) nextY -= player.getSpeed();
        if (downPressed) nextY += player.getSpeed();

        if (canMoveTo(player.getX(), nextY)) {
            player.setY(nextY);
        }

        if (leftPressed) nextX -= player.getSpeed();
        if (rightPressed) nextX += player.getSpeed();

        if (canMoveTo(nextX, player.getY())) {
            player.setX(nextX);
        }

        int mapPixelWidth = currentMap[0].length * TILE_SIZE;
        int mapPixelHeight = currentMap.length * TILE_SIZE;

        // ZMIANA POKOJÓW (Prawo / Lewo)
        if (player.getX() > mapPixelWidth - player.getSize()) {

            if (roomX < worldMaps[0].length - 1) {
                roomX++;
                currentMap = worldMaps[roomY][roomX];

                player.setX(0);
                spawnGuards();

            } else {
                player.setX(mapPixelWidth - player.getSize());
            }

        } else if (player.getX() < 0) {

            if (roomX > 0) {
                roomX--;
                currentMap = worldMaps[roomY][roomX];

                player.setX(mapPixelWidth - player.getSize());
                spawnGuards();

            } else {
                player.setX(0);
            }
        }

        // ZMIANA POKOJÓW (Dół / Góra)
        if (player.getY() > mapPixelHeight - player.getSize()) {

            if (roomY < worldMaps.length - 1) {
                roomY++;
                currentMap = worldMaps[roomY][roomX];

                player.setY(0);
                spawnGuards();

            } else {
                player.setY(mapPixelHeight - player.getSize());
            }

        } else if (player.getY() < 0) {

            if (roomY > 0) {
                roomY--;
                currentMap = worldMaps[roomY][roomX];

                player.setY(mapPixelHeight - player.getSize());
                spawnGuards();

            } else {
                player.setY(0);
            }
        }

        // Aktualizacja strażników i wykrywanie gracza
        for (Guard guard : guards) {

            guard.update();

            if (guard.checkCollision(player)
                    || guard.canSeePlayer(player, currentMap, TILE_SIZE)) {

                gameOver = true;
                break;
            }
        }

        repaint();
    }


    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) upPressed = true;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) downPressed = true;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) leftPressed = true;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = true;
        if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
            resetGame();
            return;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W || code == KeyEvent.VK_UP) upPressed = false;
        if (code == KeyEvent.VK_S || code == KeyEvent.VK_DOWN) downPressed = false;
        if (code == KeyEvent.VK_A || code == KeyEvent.VK_LEFT) leftPressed = false;
        if (code == KeyEvent.VK_D || code == KeyEvent.VK_RIGHT) rightPressed = false;
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    private void resetGame() {
        gameOver = false;
        player.setX(64);
        player.setY(64);

        roomX = 0;
        roomY = 0;

        currentMap = worldMaps[roomY][roomX];
        spawnGuards();
    }

    private void drawGameOver(Graphics2D g2d, int mapWidth, int mapHeight) {

        // Przyciemnienie planszy
        g2d.setColor(new Color(0, 0, 0, 190));
        g2d.fillRect(0, 0, mapWidth, mapHeight);

        g2d.setColor(Color.WHITE);

        // Główny napis
        g2d.setFont(new Font("Arial", Font.BOLD, 40));

        String title = "PRZEGRANA";

        FontMetrics fm = g2d.getFontMetrics();

        int titleX = (mapWidth - fm.stringWidth(title)) / 2;
        int titleY = mapHeight / 2;

        g2d.drawString(title, titleX, titleY);

        // Informacja o ponownej grze
        g2d.setFont(new Font("Arial", Font.PLAIN, 20));

        String message = "Nacisnij R, aby zagrac ponownie";

        fm = g2d.getFontMetrics();

        int messageX = (mapWidth - fm.stringWidth(message)) / 2;

        g2d.drawString(message, messageX, titleY + 40);
    }
}