package model.entities;

import model.Collision;
import model.Game;
import model.Player;
import model.characters.Character;
import model.characters.Ninja;
import model.geometry.Point2f;
import model.geometry.Polygon;
import view.Canvas;

import java.awt.*;


/**
 * Created by Nathan on 8/25/2015.
 */
public class Grapple extends model.geometry.Polygon {
    public byte owner;

    public Character grappleChar;

    public transient static final float RADIUS = 6;
    public transient static final float VELOCITY = 1000;

    public Grapple() {
    }

    public Grapple(Game game, float x, float y, float radius) {
        super(game);
        makeAABB(x, y, radius * 2, radius * 2);
    }

    public void update(float deltaTime) {
        // Remove if necessary
        if (getCenter().x > game.map.width || getCenter().y > game.map.height || getCenter().x < 0 || getCenter().y < 0) {
            game.garbage.add(this);
            return;
        }

        if (grappleChar != null) {
            setPosition(grappleChar.getCenter());
            return;
        }

        // Move rocket
        displace(acceleration, velocity, deltaTime);

        // Check collisions
        checkCollisions();
    }

    public void checkCollisions() {
        // Check collisions
        Collision collision = null;
        boolean isPlayer = false;
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
        int size = (int) getWidth();
        g2.fillOval(x, y, size, size);
        Ninja character = (Ninja) game.getPlayer(owner).character;
        Point2f origin = character.getProjectileOrigin();
        g2.drawLine((int) origin.x + canvas.cameraOffsetX, (int) (canvas.getHeight() - canvas.cameraOffsetY - origin.y), (int) (getCenter().x + canvas.cameraOffsetX), (int) (canvas.getHeight() - canvas.cameraOffsetY - getCenter().y));
    }

}
