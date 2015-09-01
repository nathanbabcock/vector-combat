package model;

import model.geometry.Point2D;
import model.geometry.Vector2D;

import java.awt.*;

/**
 * Created by Nathan on 8/30/2015.
 */
public class Particle {
    public Game game;

    public Point2D position;
    public Vector2D velocity, acceleration;
    public float size, angle, age;
    public float growth, rotation; // change in size and angle over time (per second)
    public Color color;

    public Particle(Game game) {
        this.game = game;
    }

    public void update(float deltaTime) {
        // Remove if necessary
        if (position.x > game.map.WIDTH || position.y > game.map.HEIGHT || position.x < 0 || position.y < 0) {
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
}
