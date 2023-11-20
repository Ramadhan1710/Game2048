import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;

public class Game2048 extends JFrame implements KeyListener {
    private static final int GRID_SIZE = 4;
    private static final int TILE_SIZE = 100;

    private int[][] board;
    private Random random = new Random();

    public Game2048() {
        setTitle("2048 Game");
        setSize(GRID_SIZE * TILE_SIZE, GRID_SIZE * TILE_SIZE);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        board = new int[GRID_SIZE][GRID_SIZE];
        addRandomTile();
        addRandomTile();

        addKeyListener(this);
        setFocusable(true);
    }

    private void addRandomTile() {
        int emptyCount = 0;
        for (int[] row : board) {
            for (int value : row) {
                if (value == 0) {
                    emptyCount++;
                }
            }
        }

        if (emptyCount > 0) {
            int randomIndex = random.nextInt(emptyCount) + 1;
            emptyCount = 0;

            for (int i = 0; i < GRID_SIZE; i++) {
                for (int j = 0; j < GRID_SIZE; j++) {
                    if (board[i][j] == 0) {
                        emptyCount++;
                        if (emptyCount == randomIndex) {
                            board[i][j] = (random.nextInt(2) + 1) * 2; // 2 or 4
                            return;
                        }
                    }
                }
            }
        }
    }

    private void mergeTiles(int[] line) {
        for (int i = 0; i < GRID_SIZE - 1; i++) {
            if (line[i] == line[i + 1] && line[i] != 0) {
                line[i] *= 2;
                line[i + 1] = 0;
            }
        }
    }

    private void moveLeft() {
        for (int i = 0; i < GRID_SIZE; i++) {
            mergeTiles(board[i]);

            int[] line = new int[GRID_SIZE];
            int index = 0;
            for (int j = 0; j < GRID_SIZE; j++) {
                if (board[i][j] != 0) {
                    line[index++] = board[i][j];
                }
            }

            for (int j = 0; j < GRID_SIZE; j++) {
                board[i][j] = line[j];
            }
        }
    }

    private void moveRight() {
        for (int i = 0; i < GRID_SIZE; i++) {
            mergeTiles(board[i]);

            int[] line = new int[GRID_SIZE];
            int index = GRID_SIZE - 1;
            for (int j = GRID_SIZE - 1; j >= 0; j--) {
                if (board[i][j] != 0) {
                    line[index--] = board[i][j];
                }
            }

            for (int j = 0; j < GRID_SIZE; j++) {
                board[i][j] = line[j];
            }
        }
    }

    private void moveUp() {
        for (int j = 0; j < GRID_SIZE; j++) {
            int[] line = new int[GRID_SIZE];
            int index = 0;

            for (int i = 0; i < GRID_SIZE; i++) {
                if (board[i][j] != 0) {
                    line[index++] = board[i][j];
                }
            }

            mergeTiles(line);

            for (int i = 0; i < GRID_SIZE; i++) {
                board[i][j] = line[i];
            }
        }
    }

    private void moveDown() {
        for (int j = 0; j < GRID_SIZE; j++) {
            int[] line = new int[GRID_SIZE];
            int index = GRID_SIZE - 1;

            for (int i = GRID_SIZE - 1; i >= 0; i--) {
                if (board[i][j] != 0) {
                    line[index--] = board[i][j];
                }
            }

            mergeTiles(line);

            for (int i = 0; i < GRID_SIZE; i++) {
                board[i][j] = line[i];
            }
        }
    }

    private void checkGameOver() {
        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                if (board[i][j] == 0) {
                    return;
                }
                if (j < GRID_SIZE - 1 && board[i][j] == board[i][j + 1]) {
                    return;
                }
                if (i < GRID_SIZE - 1 && board[i][j] == board[i + 1][j]) {
                    return;
                }
            }
        }
        JOptionPane.showMessageDialog(this, "Game Over!");
        System.exit(0);
    }

    private void printBoard() {
        for (int[] row : board) {
            for (int value : row) {
                System.out.print(value + "\t");
            }
            System.out.println();
        }
        System.out.println();
    }

    private void updateBoard() {
        printBoard();
        addRandomTile();
        repaint();
        checkGameOver();
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        for (int i = 0; i < GRID_SIZE; i++) {
            for (int j = 0; j < GRID_SIZE; j++) {
                int value = board[i][j];
                g.setColor(getTileColor(value));
                g.fillRect(j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                g.setColor(Color.BLACK);
                g.drawRect(j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                if (value != 0) {
                    drawCenteredString(g, String.valueOf(value), j * TILE_SIZE, i * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }
    }

    private Color getTileColor(int value) {
        switch (value) {
            case 2:    return new Color(0xFFEBCD);
            case 4:    return new Color(0xFFDAB9);
            case 8:    return new Color(0xFFD08A);
            case 16:   return new Color(0xFFC77C);
            case 32:   return new Color(0xFFB86B);
            case 64:   return new Color(0xFFAE5E);
            case 128:  return new Color(0xFFA147);
            case 256:  return new Color(0xFF8D42);
            case 512:  return new Color(0xFF7C37);
            case 1024: return new Color(0xFF6B32);
            case 2048: return new Color(0xFF621E);
            default:   return Color.LIGHT_GRAY;
        }
    }

    private void drawCenteredString(Graphics g, String text, int x, int y, int width, int height) {
        FontMetrics fm = g.getFontMetrics();
        int textX = x + (width - fm.stringWidth(text)) / 2;
        int textY = y + (height - fm.getHeight()) / 2 + fm.getAscent();
        g.drawString(text, textX, textY);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                moveLeft();
                break;
            case KeyEvent.VK_RIGHT:
                moveRight();
                break;
            case KeyEvent.VK_UP:
                moveUp();
                break;
            case KeyEvent.VK_DOWN:
                moveDown();
                break;
        }
        updateBoard();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    @Override
    public void keyReleased(KeyEvent e) {
        // Not used
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Game2048 game = new Game2048();
            game.setVisible(true);
        });
    }
}
