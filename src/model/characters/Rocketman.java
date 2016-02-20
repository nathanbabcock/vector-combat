package model.characters;

import model.Player;
import model.Sprite;
import model.entities.Rocket;
import model.geometry.Point2f;
import model.geometry.Vector2f;
import view.Canvas;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Created by Nathan on 8/31/2015.
 */
public class Rocketman extends Character {
    public transient static final float ATTACK_INTERVAL = 0.8f;

    public Rocketman() {
    }

    public Rocketman(Player player) {
        super(player);
        attackInterval = ATTACK_INTERVAL;
    }

    @Override
    public void updateSprite(float deltaTime) {
        if (sprite == null)
            sprite = game.getSprite("scout_standing");

        if (movingLeft || movingRight) {
            if (!sprite.name.startsWith("rocket_walking")) {
                sprite = game.getSprite("rocket_walking_1");
                spriteTime = 0;
            } else if (spriteTime >= sprite.time) {
                sprite = game.getSprite(sprite.next);
                spriteTime = 0;
            }
        } else {
            sprite = game.getSprite("rocket_standing");
        }
        spriteTime += deltaTime;
    }

    @Override
    public void attack(float deltaTime) {
        if (currentAttackDelay > 0)
            currentAttackDelay -= deltaTime;

        if (!attacking || currentAttackDelay > 0)
            return;

        Rocket rocket = new Rocket(game, getCenter().x, getCenter().y, Rocket.RADIUS);
        rocket.owner = player.clientID;
        Point2f origin = getCenter();
        rocket.velocity = new Vector2f(xhair.x - origin.x, xhair.y - origin.y);
        rocket.velocity.setMagnitude(Rocket.VELOCITY);
        rocket.acceleration = new Vector2f(0, 0);
        game.entities.add(rocket);
        currentAttackDelay = attackInterval;
    }

    @Override
    public void draw(Canvas canvas, Graphics2D g2) {
        // Draw hitbox
//            g2.setColor(randColor);
//            g2.fillRect((int) player.getBottomLeft().x + cameraOffsetX, (int) (height - cameraOffsetY - player.getBottomLeft().y - player.height), (int) player.width, (int) player.height);

        // Player
        int playerX = (int) getBottomLeft().x + canvas.cameraOffsetX + sprite.offsetX;
        int playerY = (int) (canvas.getHeight() - canvas.cameraOffsetY - getBottomLeft().y - height - sprite.offsetY);
        int playerWidth = sprite.width;
        int playerHeight = sprite.height;

        // Rocket launcher
        // Draw rocket
        Sprite rl = game.getSprite("rocket_launcher");
        int rlWidth = rl.width;
        int rlHeight = rl.height;
        int rlX = playerX - 8;
        int rlY = playerY + 16;
        Point2f rlOrigin = new Point2f(playerX + 12, playerY + 36);
        Vector2f rlVector = new Vector2f(xhair.x - (getBottomLeft().x + 12), -xhair.y + (getBottomLeft().y + 36));

        if (xhair.x < getCenter().x) {
            playerWidth *= -1;
            playerX += sprite.width;

            rlHeight *= -1;
//            rlX += 40;
            rlY += 40;
        }

        g2.drawImage(sprite.image, playerX, playerY, playerWidth, playerHeight, null);

        AffineTransform trans = new AffineTransform();
        trans.rotate(rlVector.getDirection(), rlOrigin.x, rlOrigin.y); // the points to rotate around (the center in my example, your left side for your problem)
        g2.transform(trans);
//            g2d.drawImage( image, sprite.x, sprite.y );  // the actual location of the sprite

        g2.drawImage(rl.image, rlX, rlY, rlWidth, rlHeight, null);
        g2.setTransform(canvas.backup);
    }
}
