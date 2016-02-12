package model.characters;

import model.Player;
import model.entities.Grapple;
import model.entities.Rocket;
import model.geometry.AABB;
import model.geometry.Point2f;
import model.geometry.Vector2f;
import view.Canvas;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Nathan on 8/31/2015.
 */
public class Ninja extends Character {
    public Grapple grapple;
    public ArrayList<Point2f> grapplePoints;

    public static transient final int SWORD_DAMAGE = 70;

    public Ninja() {
    }

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
            Point2f pivot = grapplePoints.get(grapplePoints.size() - 1);
            Vector2f radius = new Vector2f(getCenter(), pivot);

            // Jump at end of radius
//            if (radius.getMagnitude() <= width) {
//                velocity.add(new Vector2f(0, jumpVelocity));
//                altAttacking = false;
//                grapplePoints = null;
//                return;
//            }

            // Shorten or lengthen rope if necessary
            if (movingDown || movingUp) {
                Vector2f deltaRope = radius.copy().setMagnitude(moveSpeed * deltaTime * 1.2f);
                if (movingDown)
                    deltaRope.scale(-1);
                position.translate(deltaRope);
            }

            radius = new Vector2f(getCenter(), pivot);

            System.out.println("before gravity = " + velocity + " = " + velocity.getMagnitude());
            System.out.println("gavity this tick = " + acceleration.copy().scale(deltaTime) + " = " + acceleration.copy().scale(deltaTime).getMagnitude());

            // Apply GRAVITY
            acceleration.y = game.GRAVITY;
            velocity.add(acceleration.copy().scale(deltaTime));

            System.out.println("Right after gravity " + velocity);

            // Attempt to apply normal dynamics first
            position.displace(acceleration, velocity, deltaTime);

            // Handle longer radii
            if (position.distance(pivot) > radius.getMagnitude()) {
                // Position
                Vector2f newRadius = new Vector2f(getCenter(), pivot);
                newRadius.setMagnitude(newRadius.getMagnitude() - radius.getMagnitude());
                position.translate(newRadius);

                // Tangential velocity
                System.out.println("radius normal = " + radius.normal());
                float oldMag = velocity.getMagnitude();
                velocity = velocity.project(radius.normal());

                // Correct for loss of magnitude from the projection, but be careful not to cause equilibrium conditions at small values
                if (velocity.getMagnitude() > 7f)
                    velocity.setMagnitude(oldMag);
            }

            System.out.println("adjusted vel = " + velocity + " = " + velocity.getMagnitude());
            //System.out.println("acc = " + acceleration +" = " + acceleration.getMagnitude());

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
        } else
            super.applyDynamics(deltaTime);
    }

    @Override
    public void attack(float deltaTime) {
        if (currentAttackDelay > 0)
            currentAttackDelay -= deltaTime;

        if (!attacking || currentAttackDelay > 0)
            return;

        AABB hitbox = new AABB(game, getBottomLeft().x, getBottomLeft().y + 40, 80, 80);
        if (xhair.x < getCenter().x)
            hitbox.position.x -= (80 - width);

        for (Player player : game.players) {
            if (player.clientName.equals(this.player.clientName) || player.character == null) continue;
            if (player.character.collision(hitbox) != null)
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
            grapple.owner = player.clientID;
            Point2f origin = getCenter();
            grapple.velocity = new Vector2f(xhair.x - origin.x, xhair.y - origin.y);
            grapple.velocity.setMagnitude(Rocket.VELOCITY);
            grapple.acceleration = new Vector2f(0, 0);
            game.entities.add(grapple);
        }

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

        if (xhair.x < getCenter().x) {
            playerWidth *= -1;
            playerX += width + sprite.offsetX - 0;
        }

        // Velocity debugging
        if (true) {
            g2.setColor(Color.RED);
            g2.drawLine((int) playerX + playerWidth / 2, (int) playerY + playerHeight / 2, (int) (playerX + playerWidth / 2 + velocity.x), (int) (playerY + playerHeight / 2 - velocity.y));
        }

        g2.drawImage(sprite.image, playerX, playerY, playerWidth, playerHeight, null);
        g2.setTransform(canvas.backup);
    }
}
