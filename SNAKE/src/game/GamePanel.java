package game;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener, MouseListener {

    private final int TILE_SIZE = 25;
    private final int WIDTH = 20;
    private final int HEIGHT = 20;
    private final int DELAY = 150;

    private Snake snake;
    private Apple apple;
    private boolean running = true;
    private boolean gameOver = false;
    private int score = 0;

    private Timer timer;
    private Rectangle restartButton;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);

        snake = new Snake(new Point(5, 5));
        apple = new Apple(WIDTH, HEIGHT, snake);

        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void startGame() {
        snake.reset(new Point(5, 5));
        apple.spawn(WIDTH, HEIGHT, snake);
        running = true;
        gameOver = false;
        score = 0;
        timer.start();
        repaint();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!running) return;

        Point nextPos = snake.getNextPosition();

        if (nextPos.x < 0 || nextPos.y < 0 || nextPos.x >= WIDTH || nextPos.y >= HEIGHT || snake.contains(nextPos)) {
            running = false;
            gameOver = true;
            timer.stop();
            repaint();
            return;
        }

        boolean ateApple = nextPos.equals(apple.getPosition());
        snake.move(ateApple);

        if (ateApple) {
            score++;
            apple.spawn(WIDTH, HEIGHT, snake);
        }

        repaint();
        snake.resetDirectionChangeFlag();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Grid
        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i <= WIDTH; i++) g.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, HEIGHT * TILE_SIZE);
        for (int i = 0; i <= HEIGHT; i++) g.drawLine(0, i * TILE_SIZE, WIDTH * TILE_SIZE, i * TILE_SIZE);

        // Snake
        g.setColor(Color.GREEN);
        for (Point p : snake.getBody()) {
            g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        // Apple
        g.setColor(Color.RED);
        Point food = apple.getPosition();
        g.fillOval(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // Score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Score: " + score, 10, 20);

        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("GAME OVER", WIDTH * TILE_SIZE / 4, HEIGHT * TILE_SIZE / 2 - 20);
            g.drawString("Score: " + score, WIDTH * TILE_SIZE / 3, HEIGHT * TILE_SIZE / 2 + 10);

            // Restart button
            String restartText = "Click to Restart";
            g.setFont(new Font("Arial", Font.PLAIN, 20));
            int textWidth = g.getFontMetrics().stringWidth(restartText);
            int textHeight = g.getFontMetrics().getHeight();
            int x = (WIDTH * TILE_SIZE - textWidth) / 2;
            int y = HEIGHT * TILE_SIZE / 2 + 50;
            restartButton = new Rectangle(x - 10, y - textHeight, textWidth + 20, textHeight + 10);

            g.setColor(Color.GRAY);
            g.fillRect(restartButton.x, restartButton.y, restartButton.width, restartButton.height);
            g.setColor(Color.WHITE);
            g.drawRect(restartButton.x, restartButton.y, restartButton.width, restartButton.height);
            g.drawString(restartText, x, y);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (!running) return;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:    snake.setDirection('U'); break;
            case KeyEvent.VK_DOWN:  snake.setDirection('D'); break;
            case KeyEvent.VK_LEFT:  snake.setDirection('L'); break;
            case KeyEvent.VK_RIGHT: snake.setDirection('R'); break;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameOver && restartButton != null && restartButton.contains(e.getPoint())) {
            startGame();
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
}
