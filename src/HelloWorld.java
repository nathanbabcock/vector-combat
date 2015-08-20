import javax.swing.*;
import java.awt.*;

/**
 * Created by Nathan on 8/19/2015.
 */
public class HelloWorld extends JFrame {
    Game game;

    public HelloWorld() {
        game = new Game();

        setSize(new Dimension(800, 600));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);

        add(new Canvas(game));
        repaint();
    }

    public static void main(String[] args) {
        new HelloWorld();
    }


}
