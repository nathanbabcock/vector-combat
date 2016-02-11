package model.entities;

import model.Collision;
import model.Game;
import model.geometry.Point2f;
import model.geometry.Vector2f;
import view.Canvas;

import java.awt.*;

/**
 * Created by Nathan on 8/31/2015.
 */
public abstract class Entity {
    public transient Game game;

    public Point2f position;
    public Vector2f velocity, acceleration;

    public Entity() {
    }

    public Entity(Game game, Point2f position) {
        this.game = game;
        this.position = position;
        velocity = new Vector2f(0, 0);
        acceleration = new Vector2f(0, 0);
    }

    abstract public Point2f getCenter();

    abstract public Point2f getBottomLeft();

    public void update(float deltaTime) {
        // Remove if necessary
        if (getCenter().x > game.map.width || getCenter().y > game.map.height || getCenter().x < 0 || getCenter().y < 0) {
            game.garbage.add(this);
            return;
        }

        // Move
        position.displace(acceleration, velocity, deltaTime);

        // Check collisions
        checkCollisions();
    }

    public void draw(Canvas canvas, Graphics2D g2) {
    }

    public void checkCollisions() {
    }

    public void handleCollision(Collision collision) {
    }
}
