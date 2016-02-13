package model.entities;

import model.Collision;
import model.Game;
import model.Player;
import model.characters.Character;
import model.geometry.AABB;
import model.geometry.Circle;
import view.Canvas;

import java.awt.*;

/**
 * Created by Nathan on 8/25/2015.
 */
public class Grapple extends Circle {
    public byte owner;

    public Character grappleChar;

    public transient static final float RADIUS = 6;
    public transient static final float VELOCITY = 1000;

    public Grapple() {
    }

    public Grapple(Game game, float x, float y, float radius) {
        super(game, x, y, radius);
    }

    public void update(float deltaTime) {
        // Remove if necessary
        if (getCenter().x > game.map.width || getCenter().y > game.map.height || getCenter().x < 0 || getCenter().y < 0) {
            game.garbage.add(this);
            return;
        }

        if (grappleChar != null) {
            position = grappleChar.getCenter();
            return;
        }

        // Move rocket
        position.displace(acceleration, velocity, deltaTime);

        // Check collisions
        checkCollisions();
    }

    public void checkCollisions() {
        // Check collisions
        Collision collision = null;
        boolean isPlayer = false;
        for (AABB box : game.map.statics) { // Walls
            collision = box.collision(this);
            if (collision != null)
                break;
        }
        if (collision == null) { // characters
            for (Player player : game.players) {
                if (player.clientID == owner || player.character == null)
                    continue;
                collision = player.character.collision(this);
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
        velocity.zero();
//        Player player = game.getPlayer(owner);
//        if (player == null)
//            return;
//        Ninja ownerNinja = (Ninja) player.character;

        if (collision.collider != null && collision.collider instanceof Character) {
            grappleChar = (Character) collision.collider;
            //position = ((Character) collision.collider).getCenter();
        } else ;
//            ownerNinja.grapplePoint = getCenter();

    }

    public void draw(Canvas canvas, Graphics2D g2) {
        g2.setColor(Color.black);
        int x = (int) (getCenter().x + canvas.cameraOffsetX - Grapple.RADIUS);
        int y = (int) (canvas.getHeight() - canvas.cameraOffsetY - getCenter().y - Grapple.RADIUS);
        int size = (int) (2 * radius);
        g2.fillOval(x, y, size, size);
        Character character = game.getPlayer(owner).character;
        g2.drawLine((int) character.getCenter().x + canvas.cameraOffsetX, (int) (canvas.getHeight() - canvas.cameraOffsetY - character.getCenter().y), (int) (getCenter().x + canvas.cameraOffsetX), (int) (canvas.getHeight() - canvas.cameraOffsetY - getCenter().y));
    }

}
