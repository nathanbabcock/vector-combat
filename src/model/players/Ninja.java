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
        // ERROR only occurs when pivot.x < player.x (grappling to left)

        if (grapplePoints != null) {
            // Apply gravity
            Vector2D deltaV = acceleration.copy().scale(deltaTime);
            velocity.add(deltaV);
            float oldAngle = velocity.getDirection();
            acceleration.y = game.gravity;

            // Reverse direction

            // Pendulum motion
            Point2D pivot = grapplePoints.get(grapplePoints.size() - 1);
            Vector2D radius = new Vector2D(getCenter(), pivot);

            float newAngle1 = (float) (radius.getDirection() + Math.toRadians(90));
            float newAngle2 = (float) (newAngle1 + Math.toRadians(180));

            double _360 = Math.toRadians(360);
            double _0 = Math.toRadians(0);
            while (oldAngle < _0) oldAngle += _360;
            while (oldAngle > _360) oldAngle -= _360;
            while (newAngle1 < _0) newAngle1 += _360;
            while (newAngle1 > _360) newAngle1 -= _360;
            while (newAngle2 < _0) newAngle2 += _360;
            while (newAngle2 > _360) newAngle2 -= _360;

//            System.out.println("Originally "+Math.toDegrees(oldAngle)+", now will be "+Math.toDegrees(newAngle1)+" or "+Math.toDegrees(newAngle2) + " from radius "+Math.toDegrees(radius.getDirection()));

            float diff1 = Math.abs(newAngle1 - oldAngle);
            float diff2 = Math.abs(newAngle2 - oldAngle);

            System.out.println("radius: " + radius + " (" + Math.toDegrees(radius.getDirection()) + ")");

            if (diff1 < diff2) {
                velocity.setDirection(newAngle1);
//                System.out.println("setting to "+Math.toDegrees(newAngle1));
            } else {
                velocity.setDirection(newAngle2);
//                System.out.println("setting to " + Math.toDegrees(newAngle2));
            }

//            System.out.println("Result: "+velocity);
//            System.out.println("Result: "+Math.toDegrees(velocity.getDirection()));

            position.translate(velocity.copy().scale(deltaTime));


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
