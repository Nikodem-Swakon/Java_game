package game;

import game.AI.AggressiveSnakeAI;
import game.AI.SimpleSnakeAI;
import game.AI.SnakeAI;
import game.level.LevelType;
import game.utils.Direction;
import game.utils.HighScoreManager;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class GamePanel extends JPanel implements ActionListener, KeyListener {
    private  int TILE_SIZE;
    private  int WIDTH;
    private  int HEIGHT;
    private  int DELAY;
    private SnakeAI ai;
    private SnakeAI ai2;
    private LevelType level = LevelType.NORMAL;

    private java.util.List<Point> obstacles = new ArrayList<>();

    private final Object lock = new Object();
    private Thread aiThread1;
    private Thread aiThread2;

    private Snake playerSnake;
    private Snake aiSnake;
    private Snake aiSnake2;
    private Apple apple;
    private Timer timer;
    private boolean running = true;
    private boolean gameOver = false;
    private int score = 0;

    private Random random = new Random();

    public GamePanel() {
        loadConfig("level_config.txt");
        setPreferredSize(new Dimension(WIDTH * TILE_SIZE, HEIGHT * TILE_SIZE));
        setBackground(Color.BLACK);
        setFocusable(true);
        addKeyListener(this);

        StringBuilder message = new StringBuilder("TOP WYNIKI:\n");
        for (String score : HighScoreManager.getTopScores(3)) {
            message.append(score).append("\n");
        }
        JOptionPane.showMessageDialog(this, message.toString(), "Highscores", JOptionPane.INFORMATION_MESSAGE);

        startGame();
        timer = new Timer(DELAY, this);
        timer.start();

        startAIThreads();

    }


    private void startAIThreads() {
        aiThread1 = new Thread(() -> {
            while (running && !gameOver) {
                moveAI();
                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        aiThread2 = new Thread(() -> {
            while (running && !gameOver) {
                moveAI2();
                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });

        aiThread1.start();
        aiThread2.start();
    }

    private void loadConfig(String filename) {
        try {
            List<String> lines = Files.readAllLines(Paths.get(filename));
            for (String line : lines) {
                if (line.startsWith("WIDTH=")) WIDTH = Integer.parseInt(line.substring(6));
                else if (line.startsWith("HEIGHT=")) HEIGHT = Integer.parseInt(line.substring(7));
                else if (line.startsWith("TILE_SIZE=")) TILE_SIZE = Integer.parseInt(line.substring(10));
                else if (line.startsWith("DELAY=")) DELAY = Integer.parseInt(line.substring(6));
            }
        } catch (Exception e) {
            System.out.println("error" + e.getMessage());
        }
    }
    private void startGame() {
        playerSnake = new Snake(new Point(5,5), Direction.RIGHT);
        
            if (level == LevelType.NORMAL) {
        aiSnake = new Snake(new Point(WIDTH - 6, HEIGHT - 6), Direction.LEFT);
        ai = new SimpleSnakeAI();
        aiSnake2 = new Snake(new Point(WIDTH - 6, 5), Direction.LEFT);
        ai2 = new SimpleSnakeAI();
                setBackground(Color.BLACK);

    } else if (level == LevelType.HARD) {
        aiSnake = new Snake(new Point(WIDTH - 6, HEIGHT - 6), Direction.LEFT);
        ai = new AggressiveSnakeAI(); // <- będziesz musiał stworzyć nową klasę AI
                 aiSnake2 = new Snake(new Point(WIDTH - 6, 5), Direction.LEFT);
                 ai2 = new AggressiveSnakeAI();
                 setBackground(Color.ORANGE);
    }
        obstacles.clear();
        for (int i = 0; i < 10; i++) {
            Point p;
            do {
                p = new Point(random.nextInt(WIDTH), random.nextInt(HEIGHT));
            } while (playerSnake.contains(p) || aiSnake.contains(p) || obstacles.contains(p));
        obstacles.add(p);
        }

        spawnApple();
        running = true;
        gameOver = false;
        score = 0;
        

    }

    private void spawnApple() {
        Point p;
        do {
            p = new Point(random.nextInt(WIDTH), random.nextInt(HEIGHT));
        } while (playerSnake.contains(p) || aiSnake.contains(p) || obstacles.contains(p));
        apple = new Apple(p.x, p.y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!running) return;

        movePlayer();

        checkCollisions();

        // Resetujemy flagę zmiany kierunku - pozwalamy na nową zmianę w następnym ticku
        playerSnake.resetDirectionChangeFlag();
        aiSnake.resetDirectionChangeFlag();
        aiSnake2.resetDirectionChangeFlag();
        if (score == 5) {
            gameWon();
        }

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
        synchronized (lock) {
    Direction newDir = ai.getNextDirection(aiSnake, playerSnake, apple.getPosition());

    aiSnake.setDirection(newDir);
    Point nextPos = nextPosition(aiSnake);

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
    }

    private void moveAI2() {
        synchronized (lock) {
        Direction newDir = ai2.getNextDirection(aiSnake2, playerSnake, apple.getPosition());

        aiSnake2.setDirection(newDir);
        Point nextPos = nextPosition(aiSnake2);

        if (collision(nextPos, aiSnake2)) {
            switch (newDir) {
                case UP -> newDir = Direction.LEFT;
                case DOWN -> newDir = Direction.RIGHT;
                case LEFT -> newDir = Direction.DOWN;
                case RIGHT -> newDir = Direction.UP;
            }

            aiSnake2.setDirection(newDir);
            nextPos = nextPosition(aiSnake2);
            if (collision(nextPos, aiSnake2)) return;
        }

        boolean grow = nextPos.equals(apple.getPosition());
        aiSnake2.move(grow);
        if (grow) spawnApple();
    }
    }


    private void stopAIThreads() {
        if (aiThread1 != null) aiThread1.interrupt();
        if (aiThread2 != null) aiThread2.interrupt();
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
        //ai1+ai2
        if (aiSnake2 != null && aiSnake2.getBody().contains(p)) return true;
        // kolizja ze ścianami
        if (p.x < 0 || p.y < 0 || p.x >= WIDTH || p.y >= HEIGHT) return true;
        // kolizja z własnym ciałem i ciałem drugiego węża
        if (s.getBody().contains(p)) return true;
        Snake other = (s == playerSnake) ? aiSnake : playerSnake;
        if (other.getBody().contains(p)) return true;
        if (obstacles.contains(p)) return true;
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

        // Przeszkody - żółte
        g.setColor(Color.YELLOW);
        for (Point p : obstacles) {
        g.fillRect(p.x * TILE_SIZE, p.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }


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

        // Wąż AI 2 – fioletowy
        g.setColor(Color.MAGENTA);
        for (Point p : aiSnake2.getBody()) {
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
            stopAIThreads();
            startGame();
            timer.start();
            startAIThreads();
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

private void gameWon() {

    stopAIThreads();
    timer.stop(); // zatrzymujemy grę



          String name = JOptionPane.showInputDialog(this, "Gratulacje! Podaj swój nick:", "Zapisz wynik", JOptionPane.PLAIN_MESSAGE);

    if (name != null && !name.trim().isEmpty()) {
        HighScoreManager.saveScore(name.trim(), score); // Zapisujemy wynik
        JOptionPane.showMessageDialog(this, "Wynik zapisany!", "Sukces", JOptionPane.INFORMATION_MESSAGE);
    } else {
        JOptionPane.showMessageDialog(this, "Nie podano nicku. Wynik nie został zapisany.", "Uwaga", JOptionPane.WARNING_MESSAGE);
    }

        String[] options = {"Normal", "Hard"};
        int choice = JOptionPane.showOptionDialog(
            this,
            "Gratulacje! Wygrałeś! Wybierz poziom trudności dla następnego poziomu:",
            "Wygrana",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.INFORMATION_MESSAGE,
            null,
            options,
            options[0]
        );

        if (choice == 0) {
            level = LevelType.NORMAL;
        } else if (choice == 1) {
            level = LevelType.HARD;
        } else {
            level = LevelType.NORMAL;
        }

        resetGame();
    }

    private void resetGame() {
        score = 0;
        // zresetuj pozycję węża, jabłka, kierunek, prędkość zależnie od levelType
        stopAIThreads();
        startGame();
        timer.start();
        startAIThreads();
    }
}




