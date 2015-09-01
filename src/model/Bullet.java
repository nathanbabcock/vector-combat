package model;

import model.geometry.AABB;
import model.geometry.Vector2D;
import view.Canvas;

import java.awt.*;
import java.util.Random;

/**
 * Created by Nathan on 8/25/2015.
 */
public class Bullet extends AABB implements Entity {
    public Game game;
    public Player owner;
    public Vector2D velocity, acceleration;

    public static final float SIZE = 8;
    public static final float VELOCITY = 1000;
    public static final int DAMAGE = 10;

    public Bullet(Game game, float x, float y, float size) {
//        super(x, y, radius);
        super(x, y, size, size);
        this.game = game;
        velocity = new Vector2D(0, 0);
        acceleration = new Vector2D(0, 0);
    }

    public void update(float deltaTime) {
        // Remove if necessary
        if (position.x > game.map.WIDTH || position.y > game.map.HEIGHT || position.x < 0 || position.y < 0) {
            game.garbage.add(this);
            return;
        }

        // Move
        position.displace(acceleration, velocity, deltaTime);

//        generateParticleTrail(deltaTime);

        // Check collisions
        checkCollisions();
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
                if (player == owner) continue;
                collision = player.collision(this);
                if (collision != null)
                    break;
            }
        }
        if (collision != null)
            handleCollision(collision);
    }

    private void handleCollision(Collision collision) {
        game.garbage.add(this);


        if (collision.collider instanceof Player) {
            // Damage
            Player player = (Player) collision.collider;
            player.damage(Bullet.DAMAGE);

            // Knockback
            Vector2D knockback = new Vector2D(player.getCenter().x - getCenter().x, player.getCenter().y - getCenter().y);
            knockback.setMagnitude(40f);
            player.velocity.add(knockback);

            // Particles
            player.generateBloodParticles();
        } else {
            // Particles
            generateImpactParticles();
        }
//        for (Player player : game.players) {
//            float distance = player.position.distance(position);
//            if (distance <= Bullet.EXPLOSION_RADIUS) {
//                // Knockback
//                Vector2D explosion = new Vector2D(player.getCenter().x - getCenter().x, player.getCenter().y - getCenter().y);
//                // TODO scale damage and knockback with distance
//                explosion.setMagnitude(300f);
//                player.velocity.add(explosion);
//
//                // Damage
//                if (owner != player)
//                    player.damage(Bullet.DAMAGE);
//            }
//        }
//
//        generateExplosionParticles();
    }

    private void generateImpactParticles() {
        // Particle effects
        final int AVG_PARTICLES = 3;
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
        g2.setColor(Color.black);
        int x = (int) (getBottomLeft().x + canvas.cameraOffsetX);
        int y = (int) (canvas.HEIGHT - canvas.cameraOffsetY - getBottomLeft().y - width);
        int size = (int) (width);
        g2.fillRect(x, y, size, size);
    }

}
