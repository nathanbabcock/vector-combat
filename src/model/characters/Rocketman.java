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
        if (sprite == null) {
            sprite = game.getSprite("rocketman_red_body");
            legs = game.getSprite("legs_stand");
            arms = game.getSprite("rocketman_red_launcher");
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

        Rocket rocket = new Rocket(game, getCenter().x, getCenter().y, Rocket.RADIUS);
        rocket.owner = player.clientID;
        Point2f origin = getCenter();
        rocket.velocity = new Vector2f(xhair.x - origin.x, xhair.y - origin.y);
        rocket.velocity.setMagnitude(Rocket.VELOCITY);
        rocket.acceleration = new Vector2f(0, 0);
        game.entities.add(rocket);
        currentAttackDelay = attackInterval;
    }

    public void draw(Graphics2D g2) {
        // Draw hitbox
        g2.setColor(Color.RED);
        g2.drawRect(0, (int) -height, (int) width, (int) height);

        // Setup arm coordinate space
        final Point2f ARM_ORIGIN = new Point2f(16, 17); // The arms rotation center, in canvas coordinates, relative to the arm sprite
        Graphics2D g3 = (Graphics2D) g2.create();
        g3.translate(arms.offsetX + 1, -(arms.offsetY + arms.height));
        g3.rotate(-new Vector2f(position, xhair).getDirection(), ARM_ORIGIN.x, ARM_ORIGIN.y);

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
        g2 = (Graphics2D) g2.create();
        g2.translate(getBottomLeft().x + canvas.cameraOffsetX, canvas.getHeight() - canvas.cameraOffsetY - getBottomLeft().y);
        draw(g2);
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
