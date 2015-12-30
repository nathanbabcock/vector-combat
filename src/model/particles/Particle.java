package model.particles;

import model.Game;
import model.geometry.Point2D;
import model.geometry.Vector2D;
import view.Canvas;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;

/**
 * Created by Nathan on 8/30/2015.
 */
public class Particle {
    public Game game;

    public Point2D position;
    public Vector2D velocity, acceleration;
    public float size, angle;//, age;
    public float growth, rotation; // change in size and angle over time (per second)
    public Color color;

    public Particle(Game game) {
        this.game = game;
    }

    public void update(float deltaTime) {
        // Remove if necessary
        if (position.x > game.map.width || position.y > game.map.height || position.x < 0 || position.y < 0) {
            game.garbage.add(this);
            return;
        }

        // Apply dynamics if necessary
        if (velocity != null && acceleration != null) {
            velocity.add(acceleration.copy().scale(deltaTime));
            position.displace(acceleration, velocity, deltaTime);
        }

        // Apply changes over time
        size += deltaTime * growth;
        angle += deltaTime * rotation;

        // Check again for removal
        if (size <= 0)
            game.garbage.add(this);
    }

    public void draw(Canvas canvas, Graphics2D g2) {
        g2.setColor(color);

//            AffineTransform trans = new AffineTransform();
//            trans.rotate(particle.angle, particle.position.x, particle.position.y);

        int x = (int) (canvas.cameraOffsetX + position.x - size / 2);
        int y = (int) (canvas.getHeight() - canvas.cameraOffsetY - (position.y + size / 2));
        Rectangle2D rect = new Rectangle2D.Float(x, y, size, size);
        AffineTransform at = AffineTransform.getRotateInstance(angle, canvas.cameraOffsetX + position.x, canvas.getHeight() - canvas.cameraOffsetY - position.y);
        g2.fill(at.createTransformedShape(rect));
    }
}
