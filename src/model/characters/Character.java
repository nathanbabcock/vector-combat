package model.characters;

import model.Collision;
import model.Game;
import model.Player;
import model.Sprite;
import model.entities.Entity;
import model.geometry.AABB;
import model.geometry.Point2D;
import model.geometry.Vector2D;
import model.particles.Particle;
import network.InputState;
import view.Canvas;

import java.awt.*;
import java.io.Serializable;
import java.util.Random;

/**
 * Created by Nathan on 8/19/2015.
 */
abstract public class Character extends Entity<AABB> implements Serializable {
    // Constants
    public transient Float moveSpeed = 200f;
    public transient Float jumpVelocity = 300f;
    public transient Float attackInterval = 1.0f;

    public transient Player player;
    public Point2D xhair;
    public int health;
    public float currentAttackDelay;

    // States, written to by controls and read from for sprites
    public boolean movingLeft, movingRight, movingUp, movingDown, attacking, altAttacking, dead, onGround, wallLeft, wallRight;

    public transient Sprite sprite;
    public transient float spriteTime;

    // Final variables from Shape2D
    public final Point2D position;
    public final float width, height;

    public Character(Game game) {
        super(game, new AABB(0, 0, 24, 80));
        position = hitbox.position;// = new Point2D(400, 850);
        width = hitbox.width;
        height = hitbox.height;
        xhair = new Point2D(0, 0);
        health = 200;
        updateSprite(0);
    }

    @Override
    public void update(float deltaTime) {
        jump(deltaTime);
        move(deltaTime);
        attack(deltaTime);
        altAttack(deltaTime);
        checkHealth();
        applyDynamics(deltaTime);
        checkCollisions();
        updateSprite(deltaTime);
    }

    public void applyDynamics(float deltaTime) {
        // Apply gravity
        velocity.add(acceleration.copy().scale(deltaTime));
        acceleration.y = game.gravity;

        // Move player
        hitbox.position.displace(acceleration, velocity, deltaTime);
    }

    public void jump(float deltaTime) {
        if (onGround && movingUp)
            velocity.add(new Vector2D(0, jumpVelocity));
    }

    public void move(float deltaTime) {
        // Calculate actual velocity by applying controls
        if (onGround) {
            if (movingRight)
                velocity.x = moveSpeed;
            if (movingLeft)
                velocity.x = -moveSpeed;
        } else {
            if (movingRight) {
                if (velocity.x >= moveSpeed)
                    return;
                velocity.x += moveSpeed * deltaTime * 2.5f;
            }
            if (movingLeft) {
                if (velocity.x <= -moveSpeed)
                    return;
                velocity.x -= moveSpeed * deltaTime * 2.5f;
            }
        }
    }

    public void checkCollisions() {
        // Reset states
        onGround = wallLeft = wallRight = false;

        // Check collisions
        for (AABB box : game.map.statics) {
            Collision collision = hitbox.collision(box);
            if (collision != null)
                handleCollision(collision);
        }
    }

    public void handleCollision(Collision collision) {
        if (Math.abs(collision.delta.x) > Math.abs(collision.delta.y)) {
            hitbox.position.x += collision.delta.x;
            velocity.x = 0f;
            acceleration.x = 0f;

            if (collision.delta.x > 0)
                wallLeft = true;
            else
                wallRight = true;

        } else {
            hitbox.position.y += collision.delta.y;
            velocity.y = 0f;

            if (collision.delta.y > 0) {
                onGround = true;
                acceleration.y = 0f;
                velocity.x = 0f;
            }
        }
    }

    public void damage(int damage) {
        health -= damage;
        generateBloodParticles();
        checkHealth();
    }

    private void checkHealth() {
        if (health <= 0)
            player.kill();
    }

    public void generateBloodParticles() {
        // Particle effects
        final int AVG_PARTICLES = 4;
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

    public void importState(InputState state) {
        movingLeft = state.movingLeft;
        movingRight = state.movingRight;
        movingDown = state.movingDown;
        movingUp = state.movingUp;
        attacking = state.attacking;
        altAttacking = state.altAttacking;
        xhair = state.xhair;
    }

    abstract public void updateSprite(float deltaTime);

    abstract public void attack(float deltaTime);

    public void altAttack(float deltaTime) {
    } // Unused by default(?)

    abstract public void draw(Canvas canvas, Graphics2D g2);

    public void draw(Canvas canvas, Graphics2D g2, String clientName) {
        // Draw client name
        int playerX = (int) getBottomLeft().x + canvas.cameraOffsetX;
        int playerY = (int) (canvas.HEIGHT - canvas.cameraOffsetY - getBottomLeft().y - height);
        int playerWidth = sprite.width;
        int playerHeight = sprite.height;
        final int fontSize = 12;
        g2.setFont(new Font("Lucida Sans", Font.PLAIN, fontSize));
        if (player.team == Team.BLUE)
            g2.setColor(Color.BLUE);
        else if (player.team == Team.RED)
            g2.setColor(Color.RED);
        else
            g2.setColor(Color.BLACK);
        int estWidth = clientName.length() * fontSize;
        g2.drawString(clientName, playerX + playerWidth - (estWidth / 2), playerY - fontSize);
        draw(canvas, g2);
    }

}
