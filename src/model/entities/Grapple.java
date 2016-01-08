package model.entities;

import model.Collision;
import model.Game;
import model.Player;
import model.characters.Character;
import model.characters.Ninja;
import model.geometry.AABB;
import model.geometry.Circle2D;
import model.geometry.Vector2D;
import view.Canvas;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Nathan on 8/25/2015.
 */
public class Grapple extends Entity<Circle2D> {
    public int ownerID;

    public transient static final float RADIUS = 6;
    public transient static final float VELOCITY = 500;

    public Grapple(Game game, float x, float y, float radius) {
        super(game, new Circle2D(x, y, radius));
    }

    public void update(float deltaTime) {
        // Remove if necessary
        if (getCenter().x > game.map.width || getCenter().y > game.map.height || getCenter().x < 0 || getCenter().y < 0) {
            game.garbage.add(this);
            return;
        }

        // Move rocket
        hitbox.position.displace(acceleration, velocity, deltaTime);

        // Check collisions
        checkCollisions();
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
            for (Player player : game.players) {
                if (player.clientName.equals(ownerID))
                    continue;
                collision = player.character.hitbox.collision(hitbox);
                if (collision != null)
                    break;
            }
        }
        if (collision != null)
            handleCollision(collision);
    }

    public void handleCollision(Collision collision) {
//        game.garbage.add(this);
        velocity = new Vector2D(0, 0);
        Ninja owner = (Ninja) game.players.get(ownerID).character;
        owner.grapplePoints = new ArrayList();
        owner.grapplePoints.add(getCenter());

//        for (Player player : game.characters) {
//            float distance = player.position.distance(position);
//            if (distance <= Grapple.EXPLOSION_RADIUS) {
//                // Knockback
//                Vector2D explosion = new Vector2D(player.getCenter().x - getCenter().x, player.getCenter().y - getCenter().y);
//                // TODO scale damage and knockback with distance
//                explosion.setMagnitude(300f);
//                player.velocity.add(explosion);
//
//                // Damage
//                if (ownerID != player)
//                    player.damage(Grapple.DAMAGE);
//            }
//        }
//
//        generateExplosionParticles();
    }

    public void draw(Canvas canvas, Graphics2D g2) {
        g2.setColor(Color.black);
        int x = (int) (getBottomLeft().x + canvas.cameraOffsetX);
        int y = (int) (canvas.HEIGHT - canvas.cameraOffsetY - getBottomLeft().y - 2 * hitbox.radius);
        int size = (int) (2 * hitbox.radius);
        g2.fillOval(x, y, size, size);
        Character character = game.players.get(ownerID).character;
        g2.drawLine((int) character.getCenter().x + canvas.cameraOffsetX, (int) (canvas.getHeight() - canvas.cameraOffsetY - character.getCenter().y), (int) getCenter().x + canvas.cameraOffsetX, (int) (canvas.getHeight() - canvas.cameraOffsetY - getCenter().y));
    }

}
