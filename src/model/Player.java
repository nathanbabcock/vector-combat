package model;

import model.geometry.AABB;
import model.geometry.Point2D;
import model.geometry.Vector2D;
import view.Canvas;

import java.awt.*;

/**
 * Created by Nathan on 8/19/2015.
 */
abstract public class Player extends AABB implements Entity {
    // Constants
    public Float moveSpeed = 200f;
    public Float jumpSpeed = 100f;

    public Game game;

    public Vector2D velocity, acceleration;
    public int health;

    // States, written to by controls and read from for sprites
    public boolean walkingLeft;
    public boolean walkingRight;
    public boolean jumping;
    public boolean dead;

    public Sprite sprite;
    public float spriteTime;

    public Player(Game game) {
        this.game = game;
        velocity = new Vector2D(0, 0);
        acceleration = new Vector2D(0, 0);
        position = new Point2D(0, 0);
        width = 24;
        height = 80;
        health = 200;
    }

    public void update(float deltaTime) {
        applyDynamics(deltaTime);
        updateSprite(deltaTime);
        checkCollisions();
        checkHealth();
    }

    public void applyDynamics(float deltaTime) {
        // Apply gravity
        velocity.add(acceleration.copy().scale(deltaTime));
        acceleration.y = game.gravity;

        // Calculate actual velocity by applying controls
        Vector2D velocity = this.velocity.copy();
        if (walkingRight)
            velocity.add(new Vector2D(moveSpeed, 0));
        if (walkingLeft)
            velocity.add(new Vector2D(-moveSpeed, 0));

        // Move player
        position.displace(acceleration, velocity, deltaTime);
    }

    private void checkCollisions() {
        // Check collisions
        for (AABB box : game.map.statics) {
            Collision collision = collision(box);
            if (collision != null)
                handleCollision(collision);
        }
    }

    private void handleCollision(Collision collision) {
        if (Math.abs(collision.delta.x) > Math.abs(collision.delta.y)) {
            position.x += collision.delta.x;
            velocity.x = 0f;
            acceleration.x = 0f;
        } else {
            position.y += collision.delta.y;
            velocity.y = 0f;

            if (collision.delta.y > 0) {
                acceleration.y = 0f;
                velocity.x = 0f;
            }
        }
    }

    public void damage(int damage) {
        health -= damage;
        checkHealth();
    }

    private void checkHealth() {
        if (health <= 0) {
            game.garbage.add(this);
            dead = true;
        }
    }

    abstract public void updateSprite(float deltaTime);

    abstract public void shoot(Point2D xhair);

    abstract public void draw(Canvas canvas, Graphics2D g2);

}
