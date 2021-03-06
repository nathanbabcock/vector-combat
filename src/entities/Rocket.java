package entities;

import core.Game;
import core.Player;
import geometry.Collision;
import geometry.Polygon;
import geometry.Vector2f;
import particles.Fire;
import particles.Particle;
import view.Canvas;

import java.awt.*;
import java.util.Random;

/**
 * Created by Nathan on 8/25/2015.
 */
public class Rocket extends geometry.Polygon {
    public byte owner;
    public boolean exploded;

    public transient static final float RADIUS = 8;
    public transient static final float SPEED = 500;
    public transient static final float EXPLOSION_RADIUS = 100;
    public transient static final float EXPLOSION_MAGNITUDE = 300;
    public transient static final int DAMAGE = 0;//70;

    public Rocket() {
    }

    public Rocket(Game game, float x, float y, float radius) {
        super(game);
        makeAABB(x, y, radius * 2, radius * 2);
        exploded = false;
    }

    public void update(float deltaTime) {
        super.update(deltaTime);
        generateParticleTrail(deltaTime);
    }

    private void generateParticleTrail(float deltaTime) {
        final int AVG_PARTICLES = 50;
        final int AVG_SIZE = 15;
        final int MAX_DEVIATION = 5;

        float numParticles = AVG_PARTICLES * deltaTime;
        Random r = new Random();
        if (r.nextFloat() < numParticles) {
            Particle particle = new Fire(game);
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

    public void checkCollisions() {
        // Check collisions
        Collision collision = null;
        for (Polygon box : game.map.statics) { // Walls
            collision = box.collision(this);
            if (collision != null)
                break;
        }
        if (collision == null) { // characters
            for (Player player : game.players) {
                if (player.clientID == owner || player.character == null)
                    continue;
                collision = player.character.collision(this);
                if (collision != null)
                    break;
            }
        }
        if (collision != null)
            handleCollision(collision);
    }

    public void handleCollision(Collision collision) {
        game.garbage.add(this);

        for (Player player : game.players) {
            if (player.character == null) continue;
            float distance = player.character.getPosition().distance(getCenter());
            if (distance <= Rocket.EXPLOSION_RADIUS) {
                // Knockback
                Vector2f explosion = new Vector2f(player.character.getCenter().x - getCenter().x, player.character.getCenter().y - getCenter().y);
                // TODO scale damage and knockback with distance
                explosion.setMagnitude(EXPLOSION_MAGNITUDE);
                player.character.velocity.add(explosion);

                // Damage
                if (player.clientID != owner)
                    player.character.damage(Rocket.DAMAGE, collision.position, game.getPlayer(owner));
            }
        }

        //generateExplosionParticles();
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
            particle.velocity = new Vector2f(r.nextInt(AVG_VELOCITY * 2) - AVG_VELOCITY, r.nextInt(AVG_VELOCITY * 2) - AVG_VELOCITY);
            particle.acceleration = new Vector2f(0, game.GRAVITY);
            game.particles.add(particle);
        }
    }

    public void draw(Canvas canvas, Graphics2D g2) {
        if (exploded) return;
        g2.setColor(Color.red);
        int x = (int) (getBottomLeft().x + canvas.cameraOffsetX);
        int y = (int) (canvas.getHeight() - canvas.cameraOffsetY - getBottomLeft().y - getHeight());
        int size = (int) getWidth();
        g2.fillOval(x, y, size, size);
    }

}
