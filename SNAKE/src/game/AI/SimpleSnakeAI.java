package game.AI;

import game.Snake;
import game.utils.Direction;
import java.awt.Point;


public class SimpleSnakeAI {
    public Direction decideDirection(Snake aiSnake, Point applePos, java.util.function.Predicate<Point> collisionCheck) {
        Point aiHead = aiSnake.getHead();
        Direction newDir = aiSnake.getDirection();

        // Prosta AI - idzie w osi X lub Y w stronę jabłka
        if (applePos.x < aiHead.x && newDir != Direction.RIGHT) newDir = Direction.LEFT;
        else if (applePos.x > aiHead.x && newDir != Direction.LEFT) newDir = Direction.RIGHT;
        else if (applePos.y < aiHead.y && newDir != Direction.DOWN) newDir = Direction.UP;
        else if (applePos.y > aiHead.y && newDir != Direction.UP) newDir = Direction.DOWN;

        // Sprawdzamy kolizję z nowym kierunkiem
        Point nextPos = nextPosition(aiHead, newDir);
        if (collisionCheck.test(nextPos)) {
            // Zmieniamy kierunek na przeciwny w stylu switch
            switch (newDir) {
                case UP -> newDir = Direction.LEFT;
                case DOWN -> newDir = Direction.RIGHT;
                case LEFT -> newDir = Direction.DOWN;
                case RIGHT -> newDir = Direction.UP;
            }
            nextPos = nextPosition(aiHead, newDir);
            if (collisionCheck.test(nextPos)) {
                // Nie da się ruszyć - zostajemy na miejscu
                return aiSnake.getDirection();
            }
        }
        return newDir;
    }

    private Point nextPosition(Point head, Direction dir) {
        Point next = new Point(head);
        switch (dir) {
            case UP -> next.y--;
            case DOWN -> next.y++;
            case LEFT -> next.x--;
            case RIGHT -> next.x++;
        }
        return next;
    }
}
