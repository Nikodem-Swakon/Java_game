package game;

import java.awt.*;
import java.util.LinkedList;

public class Snake {
    private LinkedList<Point> body = new LinkedList<>();
    private char direction = 'R';
    private boolean directionChanged = false;

    public Snake(Point start) {
        body.add(start);
    }

    public LinkedList<Point> getBody() {
        return body;
    }

    public Point getHead() {
        return body.getFirst();
    }

    public void setDirection(char newDir) {
        if (!directionChanged && isValidDirectionChange(newDir)) {
            direction = newDir;
            directionChanged = true;
        }
    }

    public char getDirection() {
        return direction;
    }

    public void resetDirectionChangeFlag() {
        directionChanged = false;
    }

    public Point getNextPosition() {
        Point head = new Point(getHead());
        switch (direction) {
            case 'U': head.y--; break;
            case 'D': head.y++; break;
            case 'L': head.x--; break;
            case 'R': head.x++; break;
        }
        return head;
    }

    public void move(boolean grow) {
        Point next = getNextPosition();
        body.addFirst(next);
        if (!grow) body.removeLast();
    }

    public boolean contains(Point p) {
        return body.contains(p);
    }

    private boolean isValidDirectionChange(char newDir) {
        return (direction == 'U' && newDir != 'D') ||
               (direction == 'D' && newDir != 'U') ||
               (direction == 'L' && newDir != 'R') ||
               (direction == 'R' && newDir != 'L');
    }

    public void reset(Point start) {
        body.clear();
        body.add(start);
        direction = 'R';
        directionChanged = false;
    }
}
