package model.characters;

import model.Player;
import model.Sprite;
import model.entities.Rocket;
import model.geometry.Point2f;
import model.geometry.Vector2f;
import view.Canvas;

import java.awt.*;

/**
 * Created by Nathan on 8/31/2015.
 */
public class Rocketman extends Character {
    public transient static final float ATTACK_INTERVAL = 0.8f;

    public transient float armSpriteTime, legSpriteTime;
    public transient Sprite arms, legs;

    public Rocketman() {
    }

    public Rocketman(Player player) {
        super(player);
        width = 16;
        height = 74;
        attackInterval = ATTACK_INTERVAL;
    }

    @Override
    public void updateSprite(float deltaTime) {
        if (player == null) return;

        String team = null;
        if (player.team == Team.BLUE)
            team = "blue";
        else if (player.team == Team.RED)
            team = "red";

        if (sprite == null) {
            sprite = game.getSprite("rocketman_body_" + team);
            legs = game.getSprite("legs_stand");
            arms = game.getSprite("rocketman_launcher_" + team);
            return;
        }

        if (onGround && (movingLeft || movingRight)) {
            // Handle legs
            if (legs == null) {
                legs = game.getSprite("legs_walk_1");
                legSpriteTime = 0;
            } else if (legSpriteTime >= legs.time) {
                legs = game.getSprite(legs.next);
                legSpriteTime = 0;
            }
            legSpriteTime += deltaTime;
        } else {
            if (legs == null || !legs.name.equals("legs_stand"))
                legs = game.getSprite("legs_stand");
        }
    }

    @Override
    public void attack(float deltaTime) {
        if (currentAttackDelay > 0)
            currentAttackDelay -= deltaTime;

        if (!attacking || currentAttackDelay > 0)
            return;


        Point2f origin = getProjectileOrigin();
        Rocket rocket = new Rocket(game, origin.x, origin.y, Rocket.RADIUS);
        rocket.owner = player.clientID;
        rocket.velocity = new Vector2f(xhair.x - origin.x, xhair.y - origin.y);
        rocket.velocity.setMagnitude(Rocket.VELOCITY);
        rocket.acceleration = new Vector2f(0, 0);
        game.entities.add(rocket);
        currentAttackDelay = attackInterval;
    }

    private Point2f getProjectileOrigin() {
        final float GUN_LENGTH = 40;
        Point2f rot = getRotationOrigin();
        Vector2f delta = new Vector2f(rot, xhair).setMagnitude(GUN_LENGTH);
        rot.translate(delta);
        rot.y += 9;
        return rot;
    }

    private Point2f getRotationOrigin() {
        final Point2f RELATIVE_ORIGIN = new Point2f(16, 17);
        Sprite gun = game.getSprite("rocketman_launcher_red");
        Point2f origin = getBottomLeft().copy();
        origin.x += gun.offsetX + RELATIVE_ORIGIN.x;
        origin.y += gun.offsetY + gun.height - RELATIVE_ORIGIN.y;

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

        // Setup arm coordinate space
        final Point2f ARM_ORIGIN = new Point2f(16, 17); // The arms rotation center, in canvas coordinates, relative to the arm sprite
        Graphics2D g3 = (Graphics2D) g2.create();
        g3.translate(arms.offsetX + 1, -(arms.offsetY + arms.height));
        g3.rotate(-new Vector2f(getRotationOrigin(), xhair).getDirection(), ARM_ORIGIN.x, ARM_ORIGIN.y);

        // Flip horizontally
        if (xhair.x < position.x) {
            g2.scale(-1, 1);
            g2.translate(-width, 0);

            g3.scale(1, -1);
            g3.translate(0, -34);
//            g3.translate(-width, 0);
        }

        // Draw legs
        if (legs != null)
            g2.drawImage(legs.image, legs.offsetX + 1, -(legs.offsetY + legs.height), legs.width, legs.height, null);

        // Draw main sprite
        if (sprite != null)
            g2.drawImage(sprite.image, sprite.offsetX + 1, -(sprite.offsetY + sprite.height), sprite.width, sprite.height, null);

        // Draw arms
        if (arms != null)
            g3.drawImage(arms.image, 0, 0, arms.width, arms.height, null);
    }

    @Override
    public void draw(Canvas canvas, Graphics2D g2) {
        // Debug hitboxes
//        Point2f rot = getRotationOrigin();
//        Point2f proj = getProjectileOrigin();
//        Graphics2D g3 = (Graphics2D) g2.create();
//        g3.setColor(Color.GREEN);
//        g3.translate(canvas.cameraOffsetX, canvas.getHeight() - canvas.cameraOffsetY);

        g2 = (Graphics2D) g2.create();
        g2.translate(getBottomLeft().x + canvas.cameraOffsetX, canvas.getHeight() - canvas.cameraOffsetY - getBottomLeft().y);
        draw(g2);

//        g3.fillRect(((int) rot.x - 1), -((int) rot.y - 1), 2, 2);
//        g3.fillRect(((int) proj.x - 1), -((int) proj.y - 1), 2, 2);
    }

    @Override
    public void merge(Character other) {
        super.merge(other);
        if (!(other instanceof Rocketman))
            return;
        final Rocketman otherRocketman = (Rocketman) other;
        legs = otherRocketman.legs;
        legSpriteTime = otherRocketman.legSpriteTime;
        arms = otherRocketman.arms;
        armSpriteTime = otherRocketman.armSpriteTime;
    }
}
