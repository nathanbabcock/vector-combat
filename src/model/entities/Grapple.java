package model.entities;

import model.Collision;
import model.Game;
import model.geometry.AABB;
import model.geometry.Circle2D;
import model.geometry.Vector2D;
import model.players.Ninja;
import model.players.Player;
import view.Canvas;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Nathan on 8/25/2015.
 */
public class Grapple extends Circle2D implements Entity {
    public Game game;

    public Vector2D velocity, acceleration;
    public Player owner;

    public static final float RADIUS = 6;
    public static final float VELOCITY = 500;

    public Grapple(Game game, float x, float y, float radius) {
        super(x, y, radius);
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

        // Move rocket
        position.displace(acceleration, velocity, deltaTime);

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
                if (player == owner)
                    continue;
                collision = player.collision(this);
                if (collision != null)
                    break;
            }
        }
        if (collision != null)
            handleCollision(collision);
    }

    private void handleCollision(Collision collision) {
//        game.garbage.add(this);
        velocity = new Vector2D(0, 0);
        Ninja owner = (Ninja) this.owner;
        owner.grapplePoints = new ArrayList();
        owner.grapplePoints.add(position);

//        for (Player player : game.players) {
//            float distance = player.position.distance(position);
//            if (distance <= Grapple.EXPLOSION_RADIUS) {
//                // Knockback
//                Vector2D explosion = new Vector2D(player.getCenter().x - getCenter().x, player.getCenter().y - getCenter().y);
//                // TODO scale damage and knockback with distance
//                explosion.setMagnitude(300f);
//                player.velocity.add(explosion);
//
//                // Damage
//                if (owner != player)
//                    player.damage(Grapple.DAMAGE);
//            }
//        }
//
//        generateExplosionParticles();
    }

    public void draw(Canvas canvas, Graphics2D g2) {
        g2.setColor(Color.black);
        int x = (int) (getBottomLeft().x + canvas.cameraOffsetX);
        int y = (int) (canvas.HEIGHT - canvas.cameraOffsetY - getBottomLeft().y - 2 * radius);
        int size = (int) (2 * radius);
        g2.fillOval(x, y, size, size);
        g2.drawLine((int) owner.getCenter().x + canvas.cameraOffsetX, (int) (canvas.HEIGHT - canvas.cameraOffsetY - owner.getCenter().y), (int) getCenter().x + canvas.cameraOffsetX, (int) (canvas.HEIGHT - canvas.cameraOffsetY - getCenter().y));
    }

}
