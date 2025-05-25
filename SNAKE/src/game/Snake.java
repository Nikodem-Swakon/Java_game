package game;

import game.utils.Direction;
import java.awt.*;
import java.util.LinkedList;

public class Snake {
    private LinkedList<Point> body = new LinkedList<>();
    private Direction direction = Direction.RIGHT;
    private boolean directionChanged = false;

    public Snake(Point start) {
        body.add(start);
    }

    public Snake(Point start, Direction direction) {
        this.body = new LinkedList<>();
        this.body.add(start);
        this.direction = direction;
    }


    public LinkedList<Point> getBody() {
        return body;
    }

    public Point getHead() {
        return body.getFirst();
    }

    public void setDirection(Direction newDir) {
        if (!directionChanged && isValidDirectionChange(newDir)) {
            direction = newDir;
            directionChanged = true;
        }
    }

    public Direction getDirection() {
        return direction;
    }

    public void resetDirectionChangeFlag() {
        directionChanged = false;
    }

    public Point getNextPosition() {
        Point head = new Point(getHead());
        switch (direction) {
            case UP -> head.y--;
            case DOWN -> head.y++;
            case LEFT -> head.x--;
            case RIGHT -> head.x++;
        }
        return head;
    }

    public void move(boolean grow) {
        Point next = getNextPosition();
        body.addFirst(next);
        if (!grow) {
            body.removeLast();
        }
    }

    public boolean contains(Point p) {
        return body.contains(p);
    }

    private boolean isValidDirectionChange(Direction newDir) {
        return (direction == Direction.UP && newDir != Direction.DOWN) ||
               (direction == Direction.DOWN && newDir != Direction.UP) ||
               (direction == Direction.LEFT && newDir != Direction.RIGHT) ||
               (direction == Direction.RIGHT && newDir != Direction.LEFT);
    }

    public void reset(Point start) {
        body.clear();
        body.add(start);
        direction = Direction.RIGHT;
        directionChanged = false;
    }
}
