import java.awt.*;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener, MouseListener {
    private final int TILE_SIZE = 25;
    private final int WIDTH = 20;
    private final int HEIGHT = 20;
    private final int DELAY = 150;

    private LinkedList<Point> snake = new LinkedList<>();
    private Point food;
    private char direction = 'R';
    private boolean running = true;
    private boolean gameOver = false;
    private int score = 0;
    private boolean directionChanged = false;

    private Timer timer;
    private Random random = new Random();
    private Rectangle restartButton;

    public SnakeGame() {
        setPreferredSize(new Dimension(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);

        startGame();

        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void startGame() {
        snake.clear();
        snake.add(new Point(5, 5));
        spawnFood();
        direction = 'R';
        running = true;
        gameOver = false;
        score = 0;
    }

    private void spawnFood() {
        while (true) {
            food = new Point(random.nextInt(WIDTH), random.nextInt(HEIGHT));
            if (!snake.contains(food)) break;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!running) return;

        Point head = new Point(snake.getFirst());
        switch (direction) {
            case 'U': head.y--; break;
            case 'D': head.y++; break;
            case 'L': head.x--; break;
            case 'R': head.x++; break;
        }

        if (head.x < 0 || head.y < 0 || head.x >= WIDTH || head.y >= HEIGHT || snake.contains(head)) {
            running = false;
            gameOver = true;
            timer.stop();
            repaint();
            return;
        }

        snake.addFirst(head);
        if (head.equals(food)) {
            score++;
            spawnFood();
        } else {
            snake.removeLast();
        }

        repaint();
        directionChanged = false;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw grid
        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i <= WIDTH; i++) g.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, HEIGHT * TILE_SIZE);
        for (int i = 0; i <= HEIGHT; i++) g.drawLine(0, i * TILE_SIZE, WIDTH * TILE_SIZE, i * TILE_SIZE);

        // Draw snake
        g.setColor(Color.GREEN);
        for (Point p : snake) {
            g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        // Draw food
        g.setColor(Color.RED);
        g.fillOval(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // Draw score
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Score: " + score, 10, 20);

        // Game over screen
        if (gameOver) {
            g.setColor(new Color(0, 0, 0, 180));
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("GAME OVER", WIDTH * TILE_SIZE / 4, HEIGHT * TILE_SIZE / 2 - 20);
            g.drawString("Score: " + score, WIDTH * TILE_SIZE / 3, HEIGHT * TILE_SIZE / 2 + 10);

            // Draw restart button
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
        if (!running || directionChanged) return;
        char newDir = direction;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:    if (direction != 'D') newDir = 'U'; break;
            case KeyEvent.VK_DOWN:  if (direction != 'U') newDir = 'D'; break;
            case KeyEvent.VK_LEFT:  if (direction != 'R') newDir = 'L'; break;
            case KeyEvent.VK_RIGHT: if (direction != 'L') newDir = 'R'; break;
        }
        if (newDir != direction) {
            direction = newDir;
            directionChanged = true;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    @Override
    public void mouseClicked(MouseEvent e) {
        if (gameOver && restartButton != null && restartButton.contains(e.getPoint())) {
            startGame();
            timer.start();
            repaint();
        }
    }

    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        SnakeGame game = new SnakeGame();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
