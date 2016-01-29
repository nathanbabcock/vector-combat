package model.characters;

import model.Player;
import model.Sprite;
import model.entities.Rocket;
import model.geometry.Point2D;
import model.geometry.Vector2D;
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
        if (movingLeft || movingRight) {
            float spriteInterval = 0.25f;
            if (spriteTime >= spriteInterval) {
                if (sprite == game.sprites.get("rocket_standing")) {
                    sprite = game.sprites.get("rocket_walking");
                } else if (sprite == game.sprites.get("rocket_walking"))
                    sprite = game.sprites.get("rocket_standing");
                spriteTime = 0;
            }
        } else {
            sprite = game.sprites.get("rocket_standing");
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
        rocket.owner = player;
        Point2D origin = getCenter();
        rocket.velocity = new Vector2D(xhair.x - origin.x, xhair.y - origin.y);
        rocket.velocity.setMagnitude(Rocket.VELOCITY);
        rocket.acceleration = new Vector2D(0, 0);
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
        int playerY = (int) (canvas.getHeight() - canvas.cameraOffsetY - getBottomLeft().y - hitbox.height - sprite.offsetY);
        int playerWidth = sprite.width;
        int playerHeight = sprite.height;

        // Rocket launcher
        // Draw rocket
        Sprite rl = game.sprites.get("rocket_launcher");
        int rlWidth = rl.width;
        int rlHeight = rl.height;
        int rlX = playerX - 8;
        int rlY = playerY + 16;
        Point2D rlOrigin = new Point2D(playerX + 12, playerY + 36);
        Vector2D rlVector = new Vector2D(xhair.x - (getBottomLeft().x + 12), -xhair.y + (getBottomLeft().y + 36));

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
