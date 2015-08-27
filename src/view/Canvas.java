package view;

import model.Game;
import model.Player;
import model.Rocket;
import model.geometry.AABB;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Canvas extends JPanel {
    Game game;
    int WIDTH, HEIGHT;

    public final Color randColor = new Color((int) (Math.random() * 0x1000000));

/*    ArrayList<Float> positionGraph = new ArrayList();
    ArrayList<Float> velocityGraph = new ArrayList();
    ArrayList<Float> accelerationGraph = new ArrayList();*/

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
//        g2.drawImage(game.map.background, 0, 0, null);

        // Boundaries
        g2.setColor(Color.magenta);
        for (AABB b : game.map.statics)
            g2.drawRect((int) b.getBottomLeft().x, (int) (HEIGHT - b.getBottomLeft().y - b.height), (int) (b.width), (int) (b.height));

        // Player
        Player player = game.player;
        g2.setColor(randColor);
        g2.fillRect((int) player.getBottomLeft().x, (int) (HEIGHT - player.getBottomLeft().y - player.height), (int) player.width, (int) player.height);

        // Entities
        for (Object entity : game.entities) {
            if (entity instanceof Rocket) {
                Rocket rocket = (Rocket) entity;
                if (rocket.exploded) continue;
                g2.setColor(Color.red);
                g2.fillOval((int) (rocket.getBottomLeft().x), (int) (HEIGHT - rocket.getBottomLeft().y - 2 * rocket.radius), (int) (2 * rocket.radius), (int) (2 * rocket.radius));
            }
        }

       /* // Physics graphs
        positionGraph.add(HEIGHT - player.y);
        velocityGraph.add(HEIGHT - player.velocity.magnitude());
        accelerationGraph.add(HEIGHT - player.acceleration.magnitude());
        g2.setColor(Color.GREEN);
        for (int i = 0; i < positionGraph.size(); i++)
            g2.fillRect(i, positionGraph.get(i).intValue(), 2, 2); // height
        g2.setColor(Color.BLUE);
        for (int i = 0; i < velocityGraph.size(); i++)
            g2.fillRect(i, velocityGraph.get(i).intValue(), 2, 2); // velocity
        g2.setColor(Color.ORANGE);
        for (int i = 0; i < accelerationGraph.size(); i++)
            g2.fillRect(i, accelerationGraph.get(i).intValue(), 2, 2); // acceleration*/


    /* // Draw origin
        g2.setColor(Color.RED);
        g2.drawRect(0, HEIGHT - 1, 1, 1);*/
    }
}
