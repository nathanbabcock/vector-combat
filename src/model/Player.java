package model;

import model.geometry.AABB;
import model.geometry.Point2D;
import model.geometry.Vector2D;
import view.Canvas;

import java.awt.*;
import java.util.Random;

/**
 * Created by Nathan on 8/19/2015.
 */
abstract public class Player extends AABB implements Entity {
    // Constants
    public Float moveSpeed = 200f;
    public Float jumpSpeed = 100f;
    public Float attackInterval = 1.0f;

    public Game game;

    public Vector2D velocity, acceleration;
    public Point2D xhair;
    public int health;
    public float currentAttackDelay;

    // States, written to by controls and read from for sprites
    public boolean walkingLeft;
    public boolean walkingRight;
    public boolean attacking;
    public boolean jumping;
    public boolean dead;
    public boolean onGround;

    public Sprite sprite;
    public float spriteTime;

    public Player(Game game) {
        this.game = game;
        xhair = new Point2D(0, 0);
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
        attack(deltaTime);
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

    public void generateBloodParticles() {
        // Particle effects
        final int AVG_PARTICLES = 2;
        final int AVG_SIZE = 5;
        final int MAX_DEVIATION = 3;
        final int AVG_VELOCITY = 100;

        Random r = new Random();
        for (int i = 0; i < AVG_PARTICLES; i++) {
            Particle particle = new Particle(game);
            particle.position = getCenter().copy();
            int sign;
            if (r.nextBoolean())
                sign = -1;
            else
                sign = 1;
            particle.size = AVG_SIZE + (r.nextInt(MAX_DEVIATION + 1) * sign);
            particle.color = new Color(255, 0, 0);
            particle.angle = (float) Math.toRadians(r.nextInt(360));
            particle.growth = 0;// -15; // - (r.nextInt(5) + 10);
            particle.rotation = (float) Math.toRadians(r.nextInt(361));
            particle.velocity = new Vector2D(r.nextInt(AVG_VELOCITY * 2) - AVG_VELOCITY, r.nextInt(AVG_VELOCITY * 2) - AVG_VELOCITY);
            particle.acceleration = new Vector2D(0, game.gravity);
            game.particles.add(particle);
        }
    }

    abstract public void updateSprite(float deltaTime);

    abstract public void attack(float deltaTime);

    abstract public void draw(Canvas canvas, Graphics2D g2);

}
