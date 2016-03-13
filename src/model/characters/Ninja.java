package model.characters;

import model.Collision;
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

    public transient final float grappleInterval = 0.5f;
    public float currentGrappleDelay = 0;

    public static transient final float KNOCKBACK = 300;
    public static transient final float KNOCKUP = 100;
    public static transient final float DIVE_SPEED = 1500;
    public static transient final int SWORD_DAMAGE = 70;

    public transient float armSpriteTime, legSpriteTime;
    public transient Sprite arms, legs;
    public transient Direction direction;

    public float currentParryDelay;
    public transient final float parryInterval = 1.5f;
    public transient final float parryWindow = 0.35f; // Time during which the parry hitbox is active

    private static enum Direction {LEFT, RIGHT}

    public Ninja() {
    }

    public Ninja(Player player) {
        super(player);
        attackInterval = 0.5f;

        width = 50;
        height = 50;
        moveSpeed = 400f;
    }

    @Override
    public void updateSprite(float deltaTime) {
        if (sprite == null)
            sprite = game.getSprite("ninja_crouch");

        if (!onGround && grapple != null) {
            // Player grapple
            if (grapple.grappleChar != null && grapple.velocity.isZero()) {
                if (grapple.grappleChar.position.x < position.x)
                    direction = Direction.LEFT;
                else
                    direction = Direction.RIGHT;

                if (!sprite.name.equals("ninja_kick")) {
                    sprite = game.getSprite("ninja_kick");
                    arms = null;
                    legs = null;
                }
            }

            // Normal grapple
            else if (!sprite.name.equals("ninja_jump")) {
                sprite = game.getSprite("ninja_jump");
                arms = game.getSprite("ninja_arm_grapple_2");
                legs = null;
            }
        } else if (currentParryDelay >= parryInterval - parryWindow) {
            if (!sprite.name.equals("ninja_parry")) {
                sprite = game.getSprite("ninja_parry");
                arms = null;
                legs = null;
            }
        } else if (movingLeft || movingRight) {
            // Preserve direction to make crouch look more natural
            if (movingLeft)
                direction = Direction.LEFT;
            else
                direction = Direction.RIGHT;

            // Initialize sprite
            if (!sprite.name.equals("ninja_body"))
                sprite = game.getSprite("ninja_body");

            // Handle legs
            if (legs == null) {
                legs = game.getSprite("ninja_legs_1");
                legSpriteTime = 0;
            } else if (legSpriteTime >= legs.time) {
                legs = game.getSprite(legs.next);
                legSpriteTime = 0;
            }
            legSpriteTime += deltaTime;

            // Handle running arms
            if (currentAttackDelay > 0) {
                if (arms == null || !arms.name.startsWith("ninja_arm_attack")) {
                    arms = game.getSprite("ninja_arm_attack_1");
                    armSpriteTime = 0;
                } else if (armSpriteTime >= arms.time) {
                    arms = game.getSprite(arms.next);
                    armSpriteTime = 0;
                }
                armSpriteTime += deltaTime;
            } else if (arms == null || !arms.name.equals("ninja_arm"))
                arms = game.getSprite("ninja_arm");
        } else if (currentAttackDelay > 0) {
            if (!sprite.name.equals("ninja_stand")) {
                sprite = game.getSprite("ninja_stand");
                legs = null;
            }

            if (arms == null || !arms.name.startsWith("ninja_stand_arm_attack")) {
                arms = game.getSprite("ninja_stand_arm_attack_1");
                armSpriteTime = 0;
            } else if (armSpriteTime >= arms.time) {
                arms = game.getSprite(arms.next);
                armSpriteTime = 0;
            }
            armSpriteTime += deltaTime;
        } else if (!sprite.name.equals("ninja_crouch")) {
            legs = null;
            arms = null;
            sprite = game.getSprite("ninja_crouch");
        }
        spriteTime += deltaTime;
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
                    Vector2f deltaRope = radius.copy().setMagnitude(200f * deltaTime * 1.2f);
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
                //System.out.println("Now is " + position.distance(grapple.position) + ", before was " + position.distance(grapple.position));

                //final float EPSILON = 0f;
                //if (position.distance(grapple.position) + EPSILON > radius.getMagnitude()) {
                //    System.out.println("Radius was too long (" + position.distance(grapple.position) + ")");

                // Position
                Vector2f newRadius = new Vector2f(getCenter(), grapple.position);
                newRadius.setMagnitude(newRadius.getMagnitude() - radius.getMagnitude());
                position.translate(newRadius);

                // Tangential velocity
                //System.out.println("radius normal = " + radius.normal());
                float oldMag = velocity.getMagnitude();
                velocity = velocity.project(radius.normal());

                // Correct for loss of magnitude from the projection, but be careful not to cause equilibrium conditions at small values
                //if (velocity.getMagnitude() > 7f)
                //    velocity.setMagnitude(oldMag);
                //System.out.println("Radius corrected to " + radius.getMagnitude());
                //}

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

        AABB hitbox = getAttackHitbox();

        for (Player player : game.players) {
            if (player.clientName.equals(this.player.clientName) || player.character == null) continue;
            Collision collision = player.character.collision(hitbox);
            if (collision != null)
                player.character.damage(SWORD_DAMAGE, collision.position, player);
        }
        currentAttackDelay = attackInterval;
    }

    @Override
    public void damage(int damage, Point2f position, Player dealer) {
        if (currentParryDelay >= parryInterval - parryWindow) {
            AABB parry = getParryHitbox();

            System.out.println("collision position = " + position);
            System.out.println("parry box = (" + parry.position.x + ", " + parry.position.y + ") - " + parry.width + "x" + parry.height);

            if (parry.contains(position)) {
                System.out.println("parried");
                return;
            }
        }
        super.damage(damage, position, dealer);
    }

    public AABB getParryHitbox() {
        AABB parry = new AABB(game, getBottomLeft().x + 29, getBottomLeft().y, 35, 69);
        if (direction == Direction.LEFT)
            parry.position.x = getBottomLeft().x - 16;
        return parry;
    }

    public AABB getAttackHitbox() {
        AABB hitbox = new AABB(game, getBottomLeft().x + width / 2, getBottomLeft().y + width / 2, 60, 60);
        if (direction == Direction.LEFT)
            hitbox.position.x -= 60;
        return hitbox;
    }

    @Override
    public void move(float deltaTime) {
        currentParryDelay -= deltaTime;
        if (grapple != null && grapple.velocity.isZero())
            return;
        if (movingDown && currentParryDelay <= 0)
            currentParryDelay = parryInterval;
        if (currentParryDelay >= parryInterval - parryWindow)
            return;
        super.move(deltaTime);
    }

    @Override
    public void jump(float deltaTime) {
        if (currentParryDelay >= parryInterval - parryWindow)
            return;
        super.jump(deltaTime);
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
            Point2f origin = getRotationOrigin();
            grapple = new Grapple(game, origin.x, origin.y, Grapple.RADIUS);
            grapple.owner = player.clientID;
            grapple.velocity = new Vector2f(xhair.x - origin.x, xhair.y - origin.y).setMagnitude(Rocket.VELOCITY);
            grapple.acceleration = new Vector2f(0, 0);
            game.entities.add(grapple);
        }

    }

    @Override
    public void merge(Character other) {
        super.merge(other);
        if (!(other instanceof Ninja))
            return;
        final Ninja otherNinja = (Ninja) other;
        legs = otherNinja.legs;
        legSpriteTime = otherNinja.legSpriteTime;
        arms = otherNinja.arms;
        armSpriteTime = otherNinja.armSpriteTime;
        direction = otherNinja.direction;
    }


    public Point2f getProjectileOrigin() {
        final float ARM_LENGTH = 27;
        Point2f rot = getRotationOrigin();
        Vector2f delta;
        if (grapple == null)
            delta = new Vector2f(rot, xhair).setMagnitude(ARM_LENGTH);
        else
            delta = new Vector2f(getRotationOrigin(), grapple.getCenter()).setMagnitude(ARM_LENGTH);
        return rot.translate(delta);
    }

    public Point2f getRotationOrigin() {
        final Point2f RELATIVE_ORIGIN = new Point2f(0, 4);
        Sprite arm = game.getSprite("ninja_arm_grapple_2");
        Point2f origin = getBottomLeft().copy();
        origin.x += arm.offsetX + RELATIVE_ORIGIN.x;
        origin.y += arm.offsetY + arm.height - RELATIVE_ORIGIN.y;

//        // Flip horizontally
//        if (xhair.x < position.x) {
//            origin.x -= 3;
//        }

        return origin;
    }

    public void draw(Graphics2D g2) {
        // Draw hitbox
//        g2.setColor(Color.RED);
//        g2.drawRect(0, (int) -height, (int) width, (int) height);

        // Flip horizontally
        if ((sprite.name.equals("ninja_crouch")
                || sprite.name.equals("ninja_body")
                || sprite.name.equals("ninja_stand")
                || sprite.name.equals("ninja_parry")
                || sprite.name.equals("ninja_kick"))
                && direction == Direction.LEFT) {
            g2.scale(-1, 1);
            g2.translate(-width, 0);
        }

        // Draw legs
        if (legs != null)
            g2.drawImage(legs.image, legs.offsetX + 2, -(legs.offsetY + legs.height), legs.width, legs.height, null);

        // Draw main sprite
        g2.drawImage(sprite.image, sprite.offsetX + 2, -(sprite.offsetY + sprite.height), sprite.width, sprite.height, null);

        // Draw arms
        if (arms != null) {
            g2.translate(arms.offsetX + 2, -(arms.offsetY + arms.height));
            if (sprite.name.equals("ninja_jump")) {
                if (grapple != null)
                    g2.rotate(-new Vector2f(getRotationOrigin(), grapple.position).getDirection(), 0, 4);
                else
                    g2.rotate(Math.toRadians(45));
            }
            g2.drawImage(arms.image, 0, 0, arms.width, arms.height, null);
        }
    }

    @Override
    public void draw(Canvas canvas, Graphics2D g2) {
        // DEBUG HITBOXES
//        Graphics2D g3 = (Graphics2D) g2.create();
//        g3.translate(canvas.cameraOffsetX, canvas.getHeight() - canvas.cameraOffsetY);
//        if (currentAttackDelay > 0) { // Attack hitbox
//            AABB attack = getAttackHitbox();
//            g3.drawRect(((int) attack.getBottomLeft().x), -(int) (attack.getBottomLeft().y + attack.height), (int) attack.width, (int) attack.height);
//        }
//        if (currentParryDelay >= parryInterval - parryWindow) { // Parry hitbox
//            AABB parry = getParryHitbox();
//            g3.drawRect(((int) parry.getBottomLeft().x), -(int) (parry.getBottomLeft().y + parry.height), (int) parry.width, (int) parry.height);
//        }

        Point2f rot = getRotationOrigin();
        //Point2f proj = getProjectileOrigin();
        Graphics2D g3 = (Graphics2D) g2.create();
        g3.setColor(Color.GREEN);
        g3.translate(canvas.cameraOffsetX, canvas.getHeight() - canvas.cameraOffsetY);

        g2 = (Graphics2D) g2.create();
        g2.translate(getBottomLeft().x + canvas.cameraOffsetX, canvas.getHeight() - canvas.cameraOffsetY - getBottomLeft().y);
        draw(g2);

        g3.fillRect(((int) rot.x), -((int) rot.y), 3, 3);
        //g3.fillRect(((int) proj.x), -((int) proj.y), 3, 3);
    }
}
