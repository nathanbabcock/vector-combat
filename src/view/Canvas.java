package view;

import model.*;
import model.geometry.AABB;
import model.geometry.Point2D;
import model.geometry.Vector2D;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Canvas extends JPanel {
    Game game;
    public final int WIDTH = 800;
    public final int HEIGHT = 600;

    public final int cameraMarginX = 250;
    public final int cameraMarginY = 250;

    public int cameraOffsetX;
    public int cameraOffsetY;

    public Point2D xhair;

    public final Color randColor = new Color((int) (Math.random() * 0x1000000));

/*    ArrayList<Float> positionGraph = new ArrayList();
    ArrayList<Float> velocityGraph = new ArrayList();
    ArrayList<Float> accelerationGraph = new ArrayList();*/

    public Canvas(Game game) {
        this.game = game;
        xhair = new Point2D(0, 0);

        cameraOffsetX = cameraOffsetY = 0;

        setSize(new Dimension(WIDTH, HEIGHT));
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

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        AffineTransform backup = g2.getTransform();
        super.paintComponent(g);

        calculateCameraOffset();

        // Particles
        for (Particle particle : game.particles) {
            g2.setColor(particle.color);

//            AffineTransform trans = new AffineTransform();
//            trans.rotate(particle.angle, particle.position.x, particle.position.y);

            int x = (int) (cameraOffsetX + particle.position.x - particle.size / 2);
            int y = (int) (HEIGHT - cameraOffsetY - (particle.position.y + particle.size / 2));
            int size = (int) particle.size;
            Rectangle2D rect = new Rectangle2D.Float(x, y, size, size);
            AffineTransform at = AffineTransform.getRotateInstance(particle.angle, cameraOffsetX + particle.position.x, HEIGHT - cameraOffsetY - particle.position.y);
            g2.fill(at.createTransformedShape(rect));
        }

        // Background
//        g2.drawImage(game.map.background, 0, 0, null);

        // Boundaries
        g2.setColor(Color.black);
        for (AABB b : game.map.statics)
            g2.fillRect((int) b.getBottomLeft().x + cameraOffsetX, (int) (HEIGHT - cameraOffsetY - b.getBottomLeft().y - b.height), (int) (b.width), (int) (b.height));

        // Players
        for (Player player : game.players) {
            // Draw hitbox
//            g2.setColor(randColor);
//            g2.fillRect((int) player.getBottomLeft().x + cameraOffsetX, (int) (HEIGHT - cameraOffsetY - player.getBottomLeft().y - player.height), (int) player.width, (int) player.height);

            // Player
            int playerX = (int) player.getBottomLeft().x + cameraOffsetX + player.sprite.offsetX;
            int playerY = (int) (HEIGHT - cameraOffsetY - player.getBottomLeft().y - player.height - player.sprite.offsetY);
            int playerWidth = player.sprite.width;
            int playerHeight = player.sprite.height;

            // Rocket launcher
            // Draw rocket
            Sprite rl = game.sprites.get("rocket_launcher");
            int rlWidth = rl.width;
            int rlX = playerX - 8;
            Point2D rlOrigin = new Point2D(playerX + 12, playerY + 36);
            Vector2D rlVector = new Vector2D(xhair.x - (player.getBottomLeft().x + 12), -xhair.y + (player.getBottomLeft().y + 36));

            if (xhair != null && xhair.x < player.getCenter().x) {
                playerWidth *= -1;
                playerX += player.sprite.width;

                rlWidth *= -1;
                rlX += 40;
            }

            g2.drawImage(player.sprite.image, playerX, playerY, playerWidth, playerHeight, null);

            AffineTransform trans = new AffineTransform();
            trans.rotate(rlVector.getDirection(), rlOrigin.x, rlOrigin.y); // the points to rotate around (the center in my example, your left side for your problem)
            g2.transform(trans);
//            g2d.drawImage( image, sprite.x, sprite.y );  // the actual location of the sprite

            g2.drawImage(rl.image, rlX, playerY + 16, rlWidth, rl.height, null);
            g2.setTransform(backup);

        }


        // Entities
        for (Object entity : game.entities) {
            if (entity instanceof Rocket) {
                Rocket rocket = (Rocket) entity;
                if (rocket.exploded) continue;
                g2.setColor(Color.red);
                g2.fillOval((int) (rocket.getBottomLeft().x + cameraOffsetX), (int) (HEIGHT - cameraOffsetY - rocket.getBottomLeft().y - 2 * rocket.radius), (int) (2 * rocket.radius), (int) (2 * rocket.radius));
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
