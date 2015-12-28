package model.entities;

import model.Collision;
import model.Game;
import model.Player;
import model.characters.Character;
import model.geometry.AABB;
import model.geometry.Vector2D;
import model.particles.Particle;
import view.Canvas;

import java.awt.*;
import java.io.Serializable;
import java.util.Random;

/**
 * Created by Nathan on 8/25/2015.
 */
public class Bullet extends Entity<AABB> implements Serializable {
    public String owner;

    public transient static final float SIZE = 8;
    public transient static final float VELOCITY = 1000;
    public transient static final int DAMAGE = 10;

    public Bullet(Game game, float x, float y, float size) {
        super(game, new AABB(x, y, size, size));
    }

    public void checkCollisions() {
        // Check collisions
        Collision collision = null;
        for (AABB box : game.map.statics) { // Walls
            collision = box.collision(hitbox);
            if (collision != null)
                break;
        }
        if (collision == null) { // characters
            for (Player player : game.players.values()) {
                if (owner == null || player.clientName.equals(owner) || player.character == null) continue;
                collision = player.character.hitbox.collision(hitbox);
                if (collision != null) {
                    collision.collider = player.character;
                    break;
                }
            }
        }
        if (collision != null)
            handleCollision(collision);
    }

    public void handleCollision(Collision collision) {
        game.garbage.add(this);

        if (collision.collider instanceof Character) {
            // Damage
            Character player = (Character) collision.collider;
            player.damage(Bullet.DAMAGE);

            // Knockback
            Vector2D knockback = new Vector2D(player.getCenter().x - getCenter().x, player.getCenter().y - getCenter().y);
            knockback.setMagnitude(40f);
            player.velocity.add(knockback);
        } else {
            // Particles
            generateImpactParticles();
        }
//        for (Player player : game.characters) {
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
        int y = (int) (canvas.getHeight() - canvas.cameraOffsetY - getBottomLeft().y - hitbox.width);
        int size = (int) (hitbox.width);
        g2.fillRect(x, y, size, size);
    }

}
