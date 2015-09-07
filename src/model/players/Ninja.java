package model.players;

import model.Game;
import model.entities.Grapple;
import model.entities.Rocket;
import model.geometry.Point2D;
import model.geometry.Vector2D;
import view.Canvas;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Nathan on 8/31/2015.
 */
public class Ninja extends Player {
    public Grapple grapple;
    public ArrayList<model.geometry.Point2D> grapplePoints;
    public float angleVelocity;

    public Ninja(Game game) {
        super(game);
        attackInterval = 1.0f;
    }

    @Override
    public void updateSprite(float deltaTime) {
        if (walkingLeft || walkingRight) {
            float spriteInterval = 0.25f;
            if (spriteTime >= spriteInterval) {
                if (sprite == game.sprites.get("ninja_standing")) {
                    sprite = game.sprites.get("ninja_walking");
                } else if (sprite == game.sprites.get("ninja_walking"))
                    sprite = game.sprites.get("ninja_standing");
                spriteTime = 0;
            }
        } else {
            sprite = game.sprites.get("ninja_standing");
        }
        spriteTime += deltaTime;
    }

    @Override
    public void applyDynamics(float deltaTime) {

        if (grapplePoints != null) {
            // Apply gravity
            velocity.add(acceleration.copy().scale(deltaTime));
            acceleration.y = game.gravity;

            // Pendulum motion
            Point2D pivot = grapplePoints.get(grapplePoints.size() - 1);
            Vector2D vel_tan = new Vector2D(pivot, getCenter());
            vel_tan.setDirection((float) (vel_tan.getDirection() + Math.toRadians(90)));
            vel_tan.setMagnitude(velocity.copy().scale(deltaTime).getMagnitude());
            if (pivot.x < getCenter().x)
                vel_tan.scale(-1);
            position.translate(vel_tan);

//            float length = vector.getMagnitude();
//            float angle = vector.getDirection();
//            float angleAccel = (float) (game.gravity / length * Math.sin(angle));
//            angleVelocity += angleAccel * deltaTime;
//            angle += angleVelocity * deltaTime;
        } else
            super.applyDynamics(deltaTime);
    }

    @Override
    public void attack(float deltaTime) {

    }

    @Override
    public void altAttack(float deltaTime) {
        // Remove grapple when alt attack is released
        if (!altAttacking) {
            game.garbage.add(grapple);
            grapple = null;
            grapplePoints = null;
            return;
        }

        // Spawn grapple entity for first time
        if (grapple == null) {
            grapple = new Grapple(game, getCenter().x, getCenter().y, Grapple.RADIUS);
            grapple.owner = this;
            model.geometry.Point2D origin = getCenter();
            grapple.velocity = new Vector2D(xhair.x - origin.x, xhair.y - origin.y);
            grapple.velocity.setMagnitude(Rocket.VELOCITY);
            grapple.acceleration = new Vector2D(0, 0);
            game.entities.add(grapple);
        }

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

        if (xhair.x < getCenter().x) {
            playerWidth *= -1;
            playerX += sprite.width - 16;
        }

        g2.drawImage(sprite.image, playerX, playerY, playerWidth, playerHeight, null);
        g2.setTransform(canvas.backup);
    }
}
