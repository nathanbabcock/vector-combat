package model;

import model.geometry.AABB;
import model.geometry.Circle2D;
import model.geometry.Vector2D;
import view.Canvas;

import java.awt.*;
import java.util.Random;

/**
 * Created by Nathan on 8/25/2015.
 */
public class Rocket extends Circle2D implements Entity {
    public Game game;

    public Vector2D velocity, acceleration;
    public Player owner;
    public boolean exploded;

    public static final float RADIUS = 8;
    public static final float VELOCITY = 500;
    public static final float EXPLOSION_RADIUS = 100;
    public static final int DAMAGE = 60;

    public Rocket(Game game, float x, float y, float radius) {
        super(x, y, radius);
        this.game = game;
        exploded = false;
        velocity = new Vector2D(0, 0);
        acceleration = new Vector2D(0, 0);
    }

    public void update(float deltaTime) {
        // Remove if necessary
        if (position.x > game.map.WIDTH || position.y > game.map.HEIGHT || position.x < 0 || position.y < 0) {
            game.garbage.add(this);
            return;
        }

        // Move rocket
        position.displace(acceleration, velocity, deltaTime);

        generateParticleTrail(deltaTime);

        // Check collisions
        checkCollisions();
    }

    private void generateParticleTrail(float deltaTime) {
        final int AVG_PARTICLES = 50;
        final int AVG_SIZE = 15;
        final int MAX_DEVIATION = 5;

        float numParticles = AVG_PARTICLES * deltaTime;
        Random r = new Random();
        if (r.nextFloat() < numParticles) {
            Particle particle = new Smoke(game);
            particle.position = getCenter().copy();
            int sign;
            if (r.nextBoolean())
                sign = -1;
            else
                sign = 1;
            particle.size = AVG_SIZE + (r.nextInt(MAX_DEVIATION + 1) * sign);
            particle.color = new Color(255, 255, 0);
            particle.angle = (float) Math.toRadians(r.nextInt(360));
            particle.growth = -15; // - (r.nextInt(5) + 10);
            particle.rotation = (float) Math.toRadians(r.nextInt(361));
            game.particles.add(particle);
        }
    }

    private void checkCollisions() {
        // Check collisions
        Collision collision = null;
        for (AABB box : game.map.statics) { // Walls
            collision = box.collision(this);
            if (collision != null)
                break;
        }
        if (collision == null) { // players
            for (Player player : game.players) {
                if (player == owner)
                    continue;
                collision = player.collision(this);
                if (collision != null)
                    break;
            }
        }
        if (collision != null) {
            game.garbage.add(this);
            handleCollision(collision);
        }
    }

    private void handleCollision(Collision collision) {
        for (Player player : game.players) {
            float distance = player.position.distance(position);
            if (distance <= Rocket.EXPLOSION_RADIUS) {
                // Knockback
                Vector2D explosion = new Vector2D(player.getCenter().x - getCenter().x, player.getCenter().y - getCenter().y);
                // TODO scale damage and knockback with distance
                explosion.setMagnitude(300f);
                player.velocity.add(explosion);

                // Damage
                if (owner != player)
                    player.damage(Rocket.DAMAGE);
            }
        }

        generateExplosionParticles();
    }

    private void generateExplosionParticles() {
        // Particle effects
        final int AVG_PARTICLES = 20;
        final int AVG_SIZE = 10;
        final int MAX_DEVIATION = 5;
        final int AVG_VELOCITY = 200;

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
            particle.color = new Color(0, 0, 0);
            particle.angle = (float) Math.toRadians(r.nextInt(360));
            particle.growth = 0;// -15; // - (r.nextInt(5) + 10);
            particle.rotation = (float) Math.toRadians(r.nextInt(361));
            particle.velocity = new Vector2D(r.nextInt(AVG_VELOCITY * 2) - AVG_VELOCITY, r.nextInt(AVG_VELOCITY * 2) - AVG_VELOCITY);
            particle.acceleration = new Vector2D(0, game.gravity);
            game.particles.add(particle);
        }
    }

    public void draw(Canvas canvas, Graphics2D g2) {
        if (exploded) return;
        g2.setColor(Color.red);
        int x = (int) (getBottomLeft().x + canvas.cameraOffsetX);
        int y = (int) (canvas.HEIGHT - canvas.cameraOffsetY - getBottomLeft().y - 2 * radius);
        int size = (int) (2 * radius);
        g2.fillOval(x, y, size, size);
    }

}
