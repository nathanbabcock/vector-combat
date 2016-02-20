package model.characters;

import model.Player;
import model.Sprite;
import model.entities.Grapple;
import model.entities.Rocket;
import model.geometry.AABB;
import model.geometry.Point2f;
import model.geometry.Vector2f;
import view.Canvas;

import java.awt.*;

/**
 * Created by Nathan on 8/31/2015.
 */
public class Ninja extends Character {
    public Grapple grapple;
    //public Point2f grapplePoint;
    //public Character grappleChar;

    public float grappleInterval = 0.5f;
    public float currentGrappleDelay = 0;

    public static transient final float KNOCKBACK = 300;
    public static transient final float KNOCKUP = 100;
    public static transient final float DIVE_SPEED = 1500;
    public static transient final int SWORD_DAMAGE = 70;

    public Ninja() {
    }

    public Ninja(Player player) {
        super(player);
        attackInterval = 0.3f;

        width = 50;
        height = 50;
        moveSpeed = 400f;
    }

    @Override
    public void updateSprite(float deltaTime) {
        if (sprite == null)
            sprite = game.getSprite("ninja_crouch");

        if (movingLeft || movingRight) {
            if (!sprite.name.startsWith("ninja_legs_")) {
                sprite = game.getSprite("ninja_legs_1");
                spriteTime = 0;
            } else if (spriteTime >= sprite.time) {
                sprite = game.getSprite(sprite.next);
                spriteTime = 0;
            }
        } else
            sprite = game.getSprite("ninja_crouch");
        spriteTime += deltaTime;

        /*

        if (!sprite.interruptible) {
            if (spriteTime < sprite.time) {
                spriteTime += deltaTime;
                return;
            } else if (sprite.next != null) {
                sprite = game.getSprite(sprite.next);
                spriteTime = 0;
                return;
            }
        }

        if (grapple != null && grapple.grappleChar != null) {
            sprite = game.getSprite("ninja2_kick");
        } else if (attacking) {
            if (!sprite.name.startsWith("ninja2_attack")) {
                sprite = game.getSprite("ninja2_attack_1");
                spriteTime = 0;
            } else if (spriteTime >= sprite.time) {
                if (sprite.next != null) {
                    sprite = game.getSprite(sprite.next);
                    spriteTime = 0;
                } else
                    sprite = game.getSprite("ninja2_standing");
            }
        } else if (movingDown) {
            sprite = game.getSprite("ninja2_parry");
        } else if (!onGround) {
            sprite = game.getSprite("ninja2_grapple");
        } else if (movingLeft || movingRight) {
            if (!sprite.name.startsWith("ninja2_walking")) {
                sprite = game.getSprite("ninja2_walking_1");
                spriteTime = 0;
            } else if (spriteTime >= sprite.time) {
                sprite = game.getSprite(sprite.next);
                spriteTime = 0;
            }
        } else {
            sprite = game.getSprite("ninja2_standing");
        }
        spriteTime += deltaTime;*/
    }

    @Override
    public void checkCollisions() {
        // Handle grappling to players
        if (grapple != null && grapple.velocity.isZero() && grapple.grappleChar != null && getCenter().distance(grapple.grappleChar.getCenter()) <= width) {
            Vector2f knockback = new Vector2f(getCenter(), grapple.grappleChar.getCenter());
            knockback.y += KNOCKUP;
            knockback.setMagnitude(KNOCKBACK);
            grapple.grappleChar.velocity.add(knockback);
            velocity.x = 0f;
            velocity.y = KNOCKUP;

            currentGrappleDelay = grappleInterval;
            altAttacking = false;
            game.garbage.add(grapple);
            grapple = null;
        }

        super.checkCollisions();
    }

    @Override
    public void applyPhysics(float deltaTime) {
        // Grapple has hit something
        if (grapple != null && grapple.velocity.isZero()) {

            // Grappled to player
            if (grapple.grappleChar != null) {
                Vector2f radius = new Vector2f(getCenter(), grapple.grappleChar.getCenter());
                radius.setMagnitude(DIVE_SPEED);
                velocity = radius;
                super.applyPhysics(deltaTime);
            }

            // Grappled to wall or something
            else {
                Vector2f radius = new Vector2f(getCenter(), grapple.position);

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

                radius = new Vector2f(getCenter(), grapple.position);

                //System.out.println("before gravity = " + velocity + " = " + velocity.getMagnitude());
                //System.out.println("gravity this tick = " + acceleration.copy().scale(deltaTime) + " = " + acceleration.copy().scale(deltaTime).getMagnitude());

                // Apply normal physics
                super.applyPhysics(deltaTime);

                // Handle longer radii
                if (position.distance(grapple.position) > radius.getMagnitude()) {
                    // Position
                    Vector2f newRadius = new Vector2f(getCenter(), grapple.position);
                    newRadius.setMagnitude(newRadius.getMagnitude() - radius.getMagnitude());
                    position.translate(newRadius);

                    // Tangential velocity
                    //System.out.println("radius normal = " + radius.normal());
                    float oldMag = velocity.getMagnitude();
                    velocity = velocity.project(radius.normal());

                    // Correct for loss of magnitude from the projection, but be careful not to cause equilibrium conditions at small values
                    if (velocity.getMagnitude() > 7f)
                        velocity.setMagnitude(oldMag);
                }

                //System.out.println("adjusted vel = " + velocity + " = " + velocity.getMagnitude());
                //System.out.println("acc = " + acceleration +" = " + acceleration.getMagnitude());

                // Controls affect momentum
//                float deltaMagnitude = 0;
//                if (movingLeft && !movingRight) {
//                    if (velocity.x < 0)
//                        deltaMagnitude = moveSpeed * deltaTime * 2;
//                    else if (velocity.x > 0)
//                        deltaMagnitude = -moveSpeed * deltaTime * 2;
//                } else if (movingRight && !movingLeft) {
//                    if (velocity.x < 0)
//                        deltaMagnitude = -moveSpeed * deltaTime * 2;
//                    else if (velocity.x > 0)
//                        deltaMagnitude = moveSpeed * deltaTime * 2;
//                }
//                velocity.setMagnitude(velocity.getMagnitude() + deltaMagnitude);
//                }
            }
        } else
            super.applyPhysics(deltaTime);
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
        if (grapple != null && grapple.velocity.isZero())
            return;
        super.move(deltaTime);
    }

    @Override
    public void altAttack(float deltaTime) {
        if (currentGrappleDelay > 0)
            currentGrappleDelay -= deltaTime;

        // Remove grapple when alt attack is released
        if (!altAttacking) {
            if (grapple != null) {
                game.garbage.add(grapple);
                grapple = null;
                //currentGrappleDelay = grappleInterval;
            }
            return;
        }

        // Spawn grapple entity for first time
        if (grapple == null && currentGrappleDelay <= 0) {
            grapple = new Grapple(game, getCenter().x, getCenter().y, Grapple.RADIUS);
            grapple.owner = player.clientID;
            Point2f origin = getCenter();
            grapple.velocity = new Vector2f(xhair.x - origin.x, xhair.y - origin.y).setMagnitude(Rocket.VELOCITY);
            //        .add(velocity);
            grapple.acceleration = new Vector2f(0, 0);
            game.entities.add(grapple);
        }

    }

    @Override
    public void draw(Canvas canvas, Graphics2D g2) {
        // Draw hitbox
        g2.setColor(Color.GREEN);
        g2.drawRect((int) getBottomLeft().x + canvas.cameraOffsetX, (int) (canvas.getHeight() - canvas.cameraOffsetY - getBottomLeft().y - height), (int) width, (int) height);

        // Player
        int playerX = (int) getBottomLeft().x + canvas.cameraOffsetX + sprite.offsetX;
        int playerY = (int) (canvas.getHeight() - canvas.cameraOffsetY - getBottomLeft().y - sprite.height - sprite.offsetY);
        int playerWidth = sprite.width;
        int playerHeight = sprite.height;

        if (sprite.name.startsWith("ninja_run_")) {
            Sprite body = game.getSprite("ninja_body");
        }

        /*if ((sprite.name.startsWith("ninja2_attack") || sprite.name.startsWith("ninja2_parry")) && xhair.x < getCenter().x
                || sprite.name.startsWith("ninja2_walking") && movingLeft) {
            playerWidth *= -1;
            playerX += sprite.width - sprite.offsetX;
        }*/

        // Velocity debugging
        if (false) {
            g2.setColor(Color.RED);
            g2.drawLine((int) playerX + playerWidth / 2, (int) playerY + playerHeight / 2, (int) (playerX + playerWidth / 2 + velocity.x), (int) (playerY + playerHeight / 2 - velocity.y));
        }

        g2.drawImage(sprite.image, playerX, playerY, playerWidth, playerHeight, null);
        g2.setTransform(canvas.backup);
    }
}
