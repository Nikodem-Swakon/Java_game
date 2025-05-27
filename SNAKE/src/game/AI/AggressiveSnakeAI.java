package game.AI;

import game.Snake;
import game.utils.Direction;
import java.awt.Point;

public class AggressiveSnakeAI implements SnakeAI {

    @Override
    public Direction getNextDirection(Snake aiSnake, Snake playerSnake, Point apple) {
        // Przykład: jeśli gracz jest blisko, atakuj go, inaczej idź do jabłka

        Point aiHead = aiSnake.getHead();
        Point playerHead = playerSnake.getHead();

        // Odległość Manhattan
        int distX = Math.abs(playerHead.x - aiHead.x);
        int distY = Math.abs(playerHead.y - aiHead.y);

        if (distX + distY < 5) {
            // Atakuj gracza
            if (playerHead.x < aiHead.x) return Direction.LEFT;
            if (playerHead.x > aiHead.x) return Direction.RIGHT;
            if (playerHead.y < aiHead.y) return Direction.UP;
            return Direction.DOWN;
        } else {
            // Idź do jabłka (jak w SimpleSnakeAI)
            if (apple.x < aiHead.x) return Direction.LEFT;
            if (apple.x > aiHead.x) return Direction.RIGHT;
            if (apple.y < aiHead.y) return Direction.UP;
            return Direction.DOWN;
        }
    }
}
