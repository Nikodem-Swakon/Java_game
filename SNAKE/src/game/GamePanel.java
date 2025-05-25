package game;

import game.AI.SimpleSnakeAI;
import game.utils.Direction;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private final int TILE_SIZE = 25;
    private final int WIDTH = 20;
    private final int HEIGHT = 20;
    private final int DELAY = 150;
    private SimpleSnakeAI ai;

    private Snake playerSnake;
    private Snake aiSnake;
    private Apple apple;
    private Timer timer;
    private boolean running = true;
    private boolean gameOver = false;
    private int score = 0;

    private Random random = new Random();

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        ai = new SimpleSnakeAI();
        startGame(); 
        timer = new Timer(DELAY, this);
        timer.start();
    }

    private void startGame() {
        playerSnake = new Snake(new Point(5,5), Direction.RIGHT);
        aiSnake = new Snake(new Point(WIDTH - 6, HEIGHT - 6), Direction.LEFT);
        
        spawnApple();
        running = true;
        gameOver = false;
        score = 0;
    }

    private void spawnApple() {
        Point p;
        do {
            p = new Point(random.nextInt(WIDTH), random.nextInt(HEIGHT));
        } while (playerSnake.contains(p) || aiSnake.contains(p));
        apple = new Apple(p.x, p.y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!running) return;

        movePlayer();
        moveAI();

        checkCollisions();

        // Resetujemy flagę zmiany kierunku - pozwalamy na nową zmianę w następnym ticku
        playerSnake.resetDirectionChangeFlag();
        aiSnake.resetDirectionChangeFlag();

        repaint();
    }

    public Point nextPosition(Snake s, Direction dir) {
        Point head = s.getHead();
        Point next = new Point(head);
        switch (dir) {
            case UP -> next.y--;
            case DOWN -> next.y++;
            case LEFT -> next.x--;
            case RIGHT -> next.x++;
        }
        return next;
    }


    private void movePlayer() {
        Point nextPos = nextPosition(playerSnake);
        if (collision(nextPos, playerSnake)) {
            running = false;
            gameOver = true;
            timer.stop();
            return;
        }
        boolean grow = nextPos.equals(apple.getPosition());
        playerSnake.move(grow);
        if (grow) {
            score++;
            spawnApple();
        }
    }

    private void moveAI() {
        Point aiHead = aiSnake.getHead();
        Point applePos = apple.getPosition();

        // Prosta AI - idzie w osi X lub Y w stronę jabłka
        Direction newDir = aiSnake.getDirection();
        if (applePos.x < aiHead.x && newDir != Direction.RIGHT) newDir = Direction.LEFT;
        else if (applePos.x > aiHead.x && newDir != Direction.LEFT) newDir = Direction.RIGHT;
        else if (applePos.y < aiHead.y && newDir != Direction.DOWN) newDir = Direction.UP;
        else if (applePos.y > aiHead.y && newDir != Direction.UP) newDir = Direction.DOWN;

        aiSnake.setDirection(newDir);

        aiSnake.setDirection(newDir);
        Point nextPos = nextPosition(aiSnake);
        // Jeśli kolizja, zmien kierunek na przeciwny
        if (collision(nextPos, aiSnake)) {
            switch (newDir) {
                case UP -> newDir = Direction.LEFT;
                case DOWN -> newDir = Direction.RIGHT;
                case LEFT -> newDir = Direction.DOWN;
                case RIGHT -> newDir = Direction.UP;
            }       

            aiSnake.setDirection(newDir);
            nextPos = nextPosition(aiSnake);
            if (collision(nextPos, aiSnake)) {
                // AI stoi w miejscu jeśli dalej kolizja
                return;
            }
        }

        boolean grow = nextPos.equals(apple.getPosition());
        aiSnake.move(grow);
        if (grow) spawnApple();
    }

    private Point nextPosition(Snake s) {
        Point head = s.getHead();
        Point next = new Point(head);
        switch (s.getDirection()) {
            case UP ->  next.y--; 
            case DOWN ->  next.y++; 
            case LEFT ->  next.x--; 
            case RIGHT ->  next.x++;
        }
        return next;
    }

    private boolean collision(Point p, Snake s) {
        // kolizja ze ścianami
        if (p.x < 0 || p.y < 0 || p.x >= WIDTH || p.y >= HEIGHT) return true;
        // kolizja z własnym ciałem i ciałem drugiego węża
        if (s.getBody().contains(p)) return true;
        Snake other = (s == playerSnake) ? aiSnake : playerSnake;
        if (other.getBody().contains(p)) return true;
        return false;
    }

    private void checkCollisions() {
        // Dodatkowo, możesz tu dodać kolizję głowa głowa lub inne warunki
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Siatka
        g.setColor(Color.DARK_GRAY);
        for (int i = 0; i <= WIDTH; i++)
            g.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, HEIGHT * TILE_SIZE);
        for (int i = 0; i <= HEIGHT; i++)
            g.drawLine(0, i * TILE_SIZE, WIDTH * TILE_SIZE, i * TILE_SIZE);

        // Wąż gracza - zielony
        g.setColor(Color.GREEN);
        for (Point p : playerSnake.getBody()) {
            g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        // Wąż AI - niebieski
        g.setColor(Color.BLUE);
        for (Point p : aiSnake.getBody()) {
            g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        // Jabłko
        g.setColor(Color.RED);
        Point ap = apple.getPosition();
        g.fillOval(ap.x * TILE_SIZE, ap.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        // Wynik
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Score: " + score, 10, 20);

         if (gameOver) {
            g.setColor(new Color(0, 0, 0, 180)); // półprzezroczyste tło
            g.fillRect(0, 0, getWidth(), getHeight());

            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 30));
            g.drawString("GAME OVER", WIDTH * TILE_SIZE / 4, HEIGHT * TILE_SIZE / 2 - 20);

            g.setFont(new Font("Arial", Font.BOLD, 20));
            g.drawString("Final Score: " + score, WIDTH * TILE_SIZE / 4, HEIGHT * TILE_SIZE / 2 + 20);
        }
    }
@Override
public void keyPressed(KeyEvent e) {
    int key = e.getKeyCode();

    if (!gameOver) {
        switch (key) {
            case KeyEvent.VK_UP    -> playerSnake.setDirection(Direction.UP);
            case KeyEvent.VK_DOWN  -> playerSnake.setDirection(Direction.DOWN);
            case KeyEvent.VK_LEFT  -> playerSnake.setDirection(Direction.LEFT);
            case KeyEvent.VK_RIGHT -> playerSnake.setDirection(Direction.RIGHT);
        }

    } else {
        // Jeśli gra skończona i wciśniemy ENTER, restart gry
        if (key == KeyEvent.VK_ENTER) {
            startGame();
            timer.start();
        }
    }
}

@Override
public void keyReleased(KeyEvent e) {
    // tutaj nic nie robimy, ale metoda musi być obecna
}

@Override
public void keyTyped(KeyEvent e) {
    // można pominąć lub zostawić pusto
}



}

