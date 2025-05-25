package game;

import java.awt.Point;
// do dodadania
// gruszka - odwaraca sterowanie kierunku
// złote jabłko - mała szansa, pojawia się tylko na chwilę - daje 10 pkt 
// piwo - przyspiesza węża i daje możliwość zabicia innego weża - ściany nie zabijaja
public class Apple {
    private Point position;

    public Apple(int x, int y) {
        position = new Point(x, y);
    }

    public Point getPosition() {
        return position;
    }

    public void setPosition(Point p) {
        position = p;
    }
}
