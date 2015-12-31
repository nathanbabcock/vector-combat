package model.characters;

import model.Player;
import model.entities.Grapple;
import model.entities.Rocket;
import model.geometry.AABB;
import model.geometry.Point2D;
import model.geometry.Vector2D;
import view.Canvas;

import java.awt.*;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by Nathan on 8/31/2015.
 */
public class Ninja extends Character {
    public Grapple grapple;
    public ArrayList<model.geometry.Point2D> grapplePoints;

    public final int SWORD_DAMAGE = 50;

    public Ninja(Player player) {
        super(player);
        attackInterval = 0.3f;
    }

    @Override
    public void updateSprite(float deltaTime) {
        if (attacking && sprite != game.sprites.get("ninja_attack_1") && sprite != game.sprites.get("ninja_attack_2") && sprite != game.sprites.get("ninja_attack_3")) {
            sprite = game.sprites.get("ninja_attack_1");
            spriteTime = 0;
        } else if (sprite == game.sprites.get("ninja_attack_1")) {
            if (spriteTime >= 0.1f) {
                sprite = game.sprites.get("ninja_attack_2");
                spriteTime = 0;
            }
        } else if (sprite == game.sprites.get("ninja_attack_2")) {
            if (spriteTime >= 0.05f) {
                sprite = game.sprites.get("ninja_attack_3");
                spriteTime = 0;
            }
        } else if (sprite == game.sprites.get("ninja_attack_3")) {
            if (spriteTime >= 0.1f) {
                if (attacking)
                    sprite = game.sprites.get("ninja_attack_1");
                else
                    sprite = game.sprites.get("ninja_standing");
                spriteTime = 0;
            }
        } else if (movingLeft || movingRight) {
            float spriteInterval = 0.25f;
            if (spriteTime >= spriteInterval) {
                if (sprite == game.sprites.get("ninja_standing"))
                    sprite = game.sprites.get("ninja_walking");
                else if (sprite == game.sprites.get("ninja_walking"))
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
                hitbox.position.translate(deltaRope);
            }

            // Apply GRAVITY
            velocity.add(acceleration.copy().scale(deltaTime));
            acceleration.y = game.GRAVITY;

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
            velocity.setDirection(closerAngle(oldAngle, newAngle1, newAngle2));

/*            // Controls affect momentum
            float deltaMagnitude = 0;
            if (movingLeft && !movingRight) {
                if (velocity.x < 0)
                    deltaMagnitude = moveSpeed * deltaTime * 2;
                else if (velocity.x > 0)
                    deltaMagnitude = -moveSpeed * deltaTime * 2;
            } else if (movingRight && !movingLeft) {
                if (velocity.x < 0)
                    deltaMagnitude = -moveSpeed * deltaTime * 2;
                else if (velocity.x > 0)
                    deltaMagnitude = moveSpeed * deltaTime * 2;
            }
            velocity.setMagnitude(velocity.getMagnitude() + deltaMagnitude);*/

            // Finally apply velocity
            hitbox.position.translate(velocity.copy().scale(deltaTime));
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
        if (currentAttackDelay > 0)
            currentAttackDelay -= deltaTime;

        if (!attacking || currentAttackDelay > 0)
            return;

        AABB hitbox = new AABB(getBottomLeft().x, getBottomLeft().y + 40, 80, 80);
//        if (xhair.x < getCenter().x)
//            position.x -= (80 - width);

        for (Player player : game.players.values()) {
            if (player.clientName.equals(player.clientName)) continue;
            if (player.character.hitbox.collision(hitbox) != null)
                player.character.damage(SWORD_DAMAGE, player);
        }
        currentAttackDelay = attackInterval;
    }

    @Override
    public void move(float deltaTime) {
        if (grapplePoints != null)
            return;
        super.move(deltaTime);
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
            grapple.owner = player.clientName;
            model.geometry.Point2D origin = getCenter();
            grapple.velocity = new Vector2D(xhair.x - origin.x, xhair.y - origin.y);
            grapple.velocity.setMagnitude(Rocket.VELOCITY);
            grapple.acceleration = new Vector2D(0, 0);
            game.entities.put(UUID.randomUUID().toString(), grapple);
        }

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

        if (xhair.x < getCenter().x) {
            playerWidth *= -1;
            playerX += hitbox.width + sprite.offsetX - 0;
        }

        g2.drawImage(sprite.image, playerX, playerY, playerWidth, playerHeight, null);
        g2.setTransform(canvas.backup);
    }
}
