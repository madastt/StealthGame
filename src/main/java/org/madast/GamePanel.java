package org.madast;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    // Zastępujemy osobne współrzędne obiektem gracza
    private final Player player = new Player(64, 64);

    private boolean upPressed, downPressed, leftPressed, rightPressed;
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
        Timer timer = new Timer(16, this);
        timer.start();
    }

    private boolean isCollision(int x, int y) {
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
                int tileX = col * TILE_SIZE;
                int tileY = row * TILE_SIZE;

                if(currentMap[row][col] == 1){
                    g2d.setColor(Color.GRAY);
                    g2d.fillRect(tileX, tileY, TILE_SIZE, TILE_SIZE);
                    g2d.setColor(Color.DARK_GRAY);
                    g2d.drawRect(tileX, tileY, TILE_SIZE, TILE_SIZE);
                } else {
                    g2d.setColor(Color.DARK_GRAY);
                    g2d.fillRect(tileX, tileY, TILE_SIZE, TILE_SIZE);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(tileX, tileY, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        // Rysowanie gracza przez jego własną metodę
        player.draw(g2d);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        int nextX = player.getX();
        int nextY = player.getY();

        if (upPressed) nextY -= player.getSpeed();
        if (downPressed) nextY += player.getSpeed();

        if (isCollision(player.getX(), nextY)) {
            player.setY(nextY);
        }

        if (leftPressed) nextX -= player.getSpeed();
        if (rightPressed) nextX += player.getSpeed();

        if (isCollision(nextX, player.getY())) {
            player.setX(nextX);
        }

        int mapPixelWidth = currentMap[0].length * TILE_SIZE;
        int mapPixelHeight = currentMap.length * TILE_SIZE;

        // ZMIANA EKRANÓW (Prawo / Lewo)
        if (player.getX() > mapPixelWidth - player.getSize()) {
            if (roomX < worldMaps[0].length - 1) {
                roomX++;
                currentMap = worldMaps[roomY][roomX];
                player.setX(0);
            } else {
                player.setX(mapPixelWidth - player.getSize());
            }
        } else if (player.getX() < 0) {
            if (roomX > 0) {
                roomX--;
                currentMap = worldMaps[roomY][roomX];
                player.setX(mapPixelWidth - player.getSize());
            } else {
                player.setX(0);
            }
        }

        // ZMIANA EKRANÓW (Dół / Góra)
        if (player.getY() > mapPixelHeight - player.getSize()) {
            if (roomY < worldMaps.length - 1) {
                roomY++;
                currentMap = worldMaps[roomY][roomX];
                player.setY(0);
            } else {
                player.setY(mapPixelHeight - player.getSize());
            }
        } else if (player.getY() < 0) {
            if (roomY > 0) {
                roomY--;
                currentMap = worldMaps[roomY][roomX];
                player.setY(mapPixelHeight - player.getSize());
            } else {
                player.setY(0);
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
}