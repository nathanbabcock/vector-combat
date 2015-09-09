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
        if (movingLeft || movingRight) {
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
            Point2D pivot = grapplePoints.get(grapplePoints.size() - 1);
            Vector2D radius = new Vector2D(getCenter(), pivot);

            // Shorten or lengthen rope if necessary
            if (movingDown || movingUp) {
                Vector2D deltaRope = radius.copy().setMagnitude(moveSpeed).scale(deltaTime);
                if (movingDown)
                    deltaRope.scale(-1);
                position.translate(deltaRope);
            }

            // Apply gravity
            velocity.add(acceleration.copy().scale(deltaTime));
            acceleration.y = game.gravity;



/*            // Attempt to apply normal dynamics first
            Point2D newPos = position.copy().displace(acceleration, velocity, deltaTime);
            if(newPos.distance(pivot) <= radius.getMagnitude()) {
                position = newPos;
                return;
            }*/

            // Pendulum motion
            float oldAngle = velocity.getDirection();
            float newAngle1 = (float) (radius.getDirection() + Math.toRadians(90));
            float newAngle2 = (float) (newAngle1 + Math.toRadians(180));
//            System.out.println("radius: " + radius + " (" + Math.toDegrees(radius.getDirection()) + ")");
//            System.out.println("velocity: Originally " + velocity + "=" + Math.toDegrees(oldoldAngle) + ", now will be " + Math.toDegrees(newAngle1) + " or " + Math.toDegrees(newAngle2));
            velocity.setDirection(closerAngle(oldAngle, newAngle1, newAngle2));
//            System.out.println("Result: "+velocity);
//            System.out.println("Result: "+Math.toDegrees(velocity.getDirection()));
            position.translate(velocity.copy().scale(deltaTime));
        } else
            super.applyDynamics(deltaTime);
    }

    private float closerAngle(float referenceAngle, float angle1, float angle2) {
        /*double _360 = Math.toRadians(360);
        double _0 = Math.toRadians(0);
        while (referenceAngle < _0) referenceAngle += _360;
        while (referenceAngle > _360) referenceAngle -= _360;
        while (angle1 < _0) angle1 += _360;
        while (angle1 > _360) angle1 -= _360;
        while (angle2 < _0) angle2 += _360;
        while (angle2 > _360) angle2 -= _360;

//        if(angle1 < Math.toRadians(1))
//            angle1 = Math.toRadians(360)

        float diff1 = Math.abs(angle1 - referenceAngle);
        float diff2 = Math.abs(angle2 - referenceAngle);


        if (diff1 < diff2)
            return angle1;
        return angle2;*/


//        double _360 = Math.toRadians(360);
//        double _0 = Math.toRadians(0);
//        while (referenceAngle < _0) referenceAngle += _360;
//        while (referenceAngle > _360) referenceAngle -= _360;

        // Get angle range relative to the reference angle (to handle the -360 = 0 = 360 circularity)
        float _180 = (float) Math.toRadians(180);
        while (angle1 < referenceAngle - _180) angle1 += _180;
        while (angle1 > referenceAngle + _180) angle1 -= _180;
        while (angle2 < referenceAngle - _180) angle2 += _180;
        while (angle2 > referenceAngle + _180) angle2 -= _180;

        float diff1 = Math.abs(angle1 - referenceAngle);
        float diff2 = Math.abs(angle2 - referenceAngle);

        if (diff1 < diff2)
            return angle1;
        return angle2;
    }

    @Override
    public void attack(float deltaTime) {

    }

    @Override
    public void move() {
        if (grapplePoints != null)
            return;
        super.move();
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
