package game.AI;

import game.Snake;
import game.utils.Direction;
import java.awt.Point;

public class SimpleSnakeAI implements SnakeAI {
    @Override
    public Direction getNextDirection(Snake aiSnake, Snake playerSnake, Point apple) {
        Point aiHead = aiSnake.getHead();
        
        Direction newDir = aiSnake.getDirection();

        if (apple.x < aiHead.x && newDir != Direction.RIGHT) newDir = Direction.LEFT;
        else if (apple.x > aiHead.x && newDir != Direction.LEFT) newDir = Direction.RIGHT;
        else if (apple.y < aiHead.y && newDir != Direction.DOWN) newDir = Direction.UP;
        else if (apple.y > aiHead.y && newDir != Direction.UP) newDir = Direction.DOWN;

       

        return newDir;
    }
}
