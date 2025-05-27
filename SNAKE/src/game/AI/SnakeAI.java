package game.AI;

import game.utils.Direction;
import game.Snake;
import java.awt.Point;

public interface SnakeAI {
    Direction getNextDirection(Snake aiSnake, Snake playerSnake, Point apple);
}
