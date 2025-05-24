package game;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Snake Game");
        GamePanel game = new GamePanel();
        frame.add(game);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
