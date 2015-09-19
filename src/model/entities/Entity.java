package model.entities;

import model.Collision;
import model.Game;
import model.geometry.Point2D;
import model.geometry.Shape2D;
import model.geometry.Vector2D;
import view.Canvas;

import java.awt.*;
import java.io.Serializable;

/**
 * Created by Nathan on 8/31/2015.
 */
public abstract class Entity<E extends Shape2D> implements Serializable {
    public transient Game game;
    public E hitbox;

    public Vector2D velocity, acceleration;

    public Entity(Game game, E hitbox) {
        this.game = game;
        this.hitbox = hitbox;

        velocity = new Vector2D(0, 0);
        acceleration = new Vector2D(0, 0);
    }

    public Point2D getCenter() {
        return hitbox.getCenter();
    }

    public Point2D getBottomLeft() {
        return hitbox.getBottomLeft();
    }

    public void update(float deltaTime) {
        // Remove if necessary
        if (getCenter().x > game.map.WIDTH || getCenter().y > game.map.HEIGHT || getCenter().x < 0 || getCenter().y < 0) {
            game.garbage.add(this);
            return;
        }

        // Move
        hitbox.position.displace(acceleration, velocity, deltaTime);

        // Check collisions
        checkCollisions();
    }

    abstract public void draw(Canvas canvas, Graphics2D g2);

    abstract public void checkCollisions();

    abstract public void handleCollision(Collision collision);
}
