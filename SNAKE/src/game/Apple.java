package game;

import java.awt.*;
import java.util.Random;

public class Apple {
    private Point position;
    private final Random random = new Random();

    public Apple(int width, int height, Snake snake) {
        spawn(width, height, snake);
    }

    public void spawn(int width, int height, Snake snake) {
        do {
            position = new Point(random.nextInt(width), random.nextInt(height));
        } while (snake.contains(position));
    }

    public Point getPosition() {
        return position;
    }
}
