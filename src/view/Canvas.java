package view;

import model.Entity;
import model.Game;
import model.Particle;
import model.Player;
import model.geometry.AABB;
import model.geometry.Point2D;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Canvas extends JPanel {
    private Game game;

    public AffineTransform backup;

    public final int WIDTH = 800;
    public final int HEIGHT = 600;
    public final int cameraMarginX = 250;
    public final int cameraMarginY = 250;
    public int cameraOffsetX;
    public int cameraOffsetY;

    public Point2D xhair_relative, xhair_absolute;

/*    ArrayList<Float> positionGraph = new ArrayList();
    ArrayList<Float> velocityGraph = new ArrayList();
    ArrayList<Float> accelerationGraph = new ArrayList();*/

    public Canvas(Game game) {
        this.game = game;
        xhair_relative = xhair_absolute = new Point2D(0, 0);

        cameraOffsetX = cameraOffsetY = 0;

        setSize(new Dimension(WIDTH, HEIGHT));
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        backup = g2.getTransform();
        super.paintComponent(g);

        calculateCameraOffset();

        // Particles
        for (Particle particle : game.particles)
            particle.draw(this, g2);

        // Background
//        g2.drawImage(game.map.background, 0, 0, null);

        // Boundaries
        g2.setColor(Color.black);
        for (AABB b : game.map.statics)
            g2.fillRect((int) b.getBottomLeft().x + cameraOffsetX, (int) (HEIGHT - cameraOffsetY - b.getBottomLeft().y - b.height), (int) (b.width), (int) (b.height));

        // Players
        for (Player player : game.players)
            player.draw(this, g2);

        // Entities
        for (Entity entity : game.entities)
            entity.draw(this, g2);

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

    private void calculateCameraOffset() {
        // Horizontal
        Point2D pos = game.player.getBottomLeft();
        if (pos.x < cameraMarginX) {
            cameraOffsetX = 0;
            return;
        }
        if (pos.x + game.player.width > game.map.WIDTH - cameraMarginX) {
            cameraOffsetX = WIDTH - game.map.WIDTH;
            return;
        }

        int left = (int) pos.x + cameraOffsetX;
        if (left < cameraMarginX) {
            cameraOffsetX += cameraMarginX - left;
            return;
        } else if (left + game.player.width > WIDTH - cameraMarginX) {
            cameraOffsetX -= (left + game.player.width) - (WIDTH - cameraMarginX);
            return;
        }

        // Vertical

        if (pos.y < cameraMarginY) { // Bottom of map
            cameraOffsetY = 0;
            return;
        }
        if (pos.y + game.player.height > game.map.HEIGHT - cameraMarginX) { // Top of map
            cameraOffsetY = HEIGHT - game.map.HEIGHT;
            return;
        }

        int bottom = (int) pos.y + cameraOffsetY;
        System.out.println(bottom);
        if (bottom < cameraMarginY) { // In bottom margin
            cameraOffsetY += cameraMarginY - bottom;
            return;
        } else if (bottom + game.player.height > HEIGHT - cameraMarginY) { // In top margin
            cameraOffsetY -= (bottom + game.player.height) - (HEIGHT - cameraMarginY);
            return;
        }
    }
}
