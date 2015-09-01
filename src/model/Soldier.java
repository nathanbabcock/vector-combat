package model;

import model.geometry.Point2D;
import model.geometry.Vector2D;
import view.Canvas;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Created by Nathan on 8/31/2015.
 */
public class Soldier extends Player {
    public Soldier(Game game) {
        super(game);

        attackInterval = 0.1f;
    }

    @Override
    public void updateSprite(float deltaTime) {
        if (walkingLeft || walkingRight) {
            float spriteInterval = 0.25f;
            if (spriteTime >= spriteInterval) {
                if (sprite == game.sprites.get("soldier_standing")) {
                    sprite = game.sprites.get("soldier_walking");
                } else if (sprite == game.sprites.get("soldier_walking"))
                    sprite = game.sprites.get("soldier_standing");
                spriteTime = 0;
            }
        } else {
            sprite = game.sprites.get("soldier_standing");
        }
        spriteTime += deltaTime;
    }

    @Override
    public void attack(float deltaTime) {
        if (currentAttackDelay > 0)
            currentAttackDelay -= deltaTime;

        if (!attacking || currentAttackDelay > 0)
            return;

        Bullet bullet = new Bullet(game, getCenter().x, getCenter().y, Bullet.SIZE);
        Point2D origin = getCenter();
        bullet.owner = this;
        bullet.velocity = new Vector2D(xhair.x - origin.x, xhair.y - origin.y);
        bullet.velocity.setMagnitude(Bullet.VELOCITY);
        game.entities.add(bullet);
        currentAttackDelay = attackInterval;
    }

    @Override
    public void draw(Canvas canvas, Graphics2D g2) {
        // Draw hitbox
//            g2.setColor(randColor);
//            g2.fillRect((int) player.getBottomLeft().x + cameraOffsetX, (int) (HEIGHT - cameraOffsetY - player.getBottomLeft().y - player.height), (int) player.width, (int) player.height);

        // Player
        int playerX = (int) getBottomLeft().x + canvas.cameraOffsetX + sprite.offsetX;
        int playerY = (int) (canvas.HEIGHT - canvas.cameraOffsetY - getBottomLeft().y - height - sprite.offsetY);
        int playerWidth = sprite.width;
        int playerHeight = sprite.height;

        // Rocket launcher
        // Draw rocket
        Sprite mg = game.sprites.get("soldier_gun");
        int mgWidth = mg.width;
        int mgX = playerX + 16;
        int mgY = playerY + 24;
        Point2D mgOrigin = new Point2D(playerX + 16, playerY + 36);
        Vector2D mgVector = new Vector2D(xhair.x - (getBottomLeft().x + 12), -xhair.y + (getBottomLeft().y + 36));

        if (xhair.x < getCenter().x) {
            playerWidth *= -1;
            playerX += sprite.width - sprite.offsetX;

            mgWidth *= -1;
            mgX += 8;
        }

        g2.drawImage(sprite.image, playerX, playerY, playerWidth, playerHeight, null);

        AffineTransform trans = new AffineTransform();
        trans.rotate(mgVector.getDirection(), mgOrigin.x, mgOrigin.y); // the points to rotate around (the center in my example, your left side for your problem)
        g2.transform(trans);
//            g2d.drawImage( image, sprite.x, sprite.y );  // the actual location of the sprite

        g2.drawImage(mg.image, mgX, mgY, mgWidth, mg.height, null);
        g2.setTransform(canvas.backup);
    }
}
