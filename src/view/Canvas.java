package view;

import model.Game;
import model.Player;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Canvas extends JPanel {
    Game game;
    int WIDTH, HEIGHT;

    public final Color randColor = new Color((int) (Math.random() * 0x1000000));

    ArrayList<Float> positionGraph = new ArrayList();
    ArrayList<Float> velocityGraph = new ArrayList();
    ArrayList<Float> accelerationGraph = new ArrayList();

    public Canvas(Game game) {
        this.game = game;
        WIDTH = game.map.WIDTH;
        HEIGHT = game.map.HEIGHT;
        setSize(new Dimension(WIDTH, HEIGHT));
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g);

        // Background
        g2.drawImage(game.map.background, 0, 0, null);

        // Player
        Player player = game.player;
        g2.setColor(randColor);
        g2.fillRect(player.x.intValue(), HEIGHT - player.y.intValue() - player.height.intValue(), player.width.intValue(), player.height.intValue());

        // Physics graphs
        positionGraph.add(HEIGHT - player.y);
        velocityGraph.add(HEIGHT - player.velocity.magnitude());
        accelerationGraph.add(HEIGHT + game.gravity);
        g2.setColor(Color.GREEN);
        for (int i = 0; i < positionGraph.size(); i++)
            g2.fillRect(i, positionGraph.get(i).intValue(), 2, 2); // height
        g2.setColor(Color.BLUE);
        for (int i = 0; i < velocityGraph.size(); i++)
            g2.fillRect(i, velocityGraph.get(i).intValue(), 2, 2); // velocity
        g2.setColor(Color.ORANGE);
        for (int i = 0; i < accelerationGraph.size(); i++)
            g2.fillRect(i, accelerationGraph.get(i).intValue(), 2, 2); // acceleration

        /*// Besenham test
        ArrayList<Point2D> test = Game.besenham(0, 0, WIDTH, HEIGHT);
        for(Point2D p : test)
            g2.fillRect(p.x.intValue(), p.y.intValue(), 1, 1);*/
    }
}
