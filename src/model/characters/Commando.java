package model.characters;

import model.Game;
import model.Player;
import model.Sprite;
import model.entities.Bullet;
import model.geometry.Point2f;
import model.geometry.Vector2f;
import model.particles.Fire;
import model.particles.Particle;
import view.Canvas;

import java.awt.*;
import java.util.Random;

/**
 * Created by Nathan on 8/31/2015.
 */
public class Commando extends Character {
    public float jetpackVelocity = 250f;

    public transient float armSpriteTime, legSpriteTime;
    public transient Sprite arms, legs;

    public Commando() {
    }

    public Commando(Player player) {
        super(player);
        width = 16;
        height = 80;
        attackInterval = 0.1f;
    }

    @Override
    public void updateSprite(float deltaTime) {
        if (player == null) return;

        String team = "red";
        if (player.team == Team.BLUE)
            team = "blue";

        if (sprite == null) {
            sprite = Game.getSprite("commando_body_" + team);
            legs = Game.getSprite("legs_stand");
            arms = Game.getSprite("commando_gun_" + team);
            return;
        }

        if (onGround && (movingLeft || movingRight)) {
            // Handle legs
            if (legs == null) {
                legs = Game.getSprite("legs_walk_1");
                legSpriteTime = 0;
            } else if (legSpriteTime >= legs.time) {
                legs = Game.getSprite(legs.next);
                legSpriteTime = 0;
            }
            legSpriteTime += deltaTime;
        } else {
            if (legs == null || !legs.name.equals("legs_stand"))
                legs = Game.getSprite("legs_stand");
        }
    }

    @Override
    public void attack(float deltaTime) {
        if (currentAttackDelay > 0)
            currentAttackDelay -= deltaTime;

        if (!attacking || currentAttackDelay > 0)
            return;

        Point2f origin = getProjectileOrigin();
        Bullet bullet = new Bullet(game, origin.x, origin.y, Bullet.SIZE);
        bullet.owner = player.clientID;
        bullet.velocity = new Vector2f(xhair.x - origin.x, xhair.y - origin.y);
        bullet.velocity.setMagnitude(Bullet.VELOCITY);
        game.entities.add(bullet);
        currentAttackDelay = attackInterval;
    }

    @Override
    public void jump(float deltaTime) {
        super.jump(deltaTime);

        if (!onGround && movingUp) {
            if (velocity.y <= jetpackVelocity)
                velocity.y += jetpackVelocity * deltaTime * 4;
            generateParticleTrail(deltaTime);
        }
    }

    private void generateParticleTrail(float deltaTime) {
        final int AVG_PARTICLES = 30;
        final int AVG_SIZE = 20;
        final int MAX_DEVIATION = 5;

        float numParticles = AVG_PARTICLES * deltaTime;
        Random r = new Random();
        if (r.nextFloat() < numParticles) {
            Particle particle = new Fire(game);
            particle.position = getJetpackOrigin();
            int sign;
            if (r.nextBoolean())
                sign = -1;
            else
                sign = 1;
            particle.size = AVG_SIZE + (r.nextInt(MAX_DEVIATION + 1) * sign);
            particle.color = new Color(255, 255, 0);
            particle.angle = (float) Math.toRadians(r.nextInt(360));
            particle.growth = -15; // - (r.nextInt(5) + 10);
            particle.rotation = (float) Math.toRadians(r.nextInt(361));
            game.particles.add(particle);
        }
    }

    private Point2f getJetpackOrigin() {
        Point2f point = getBottomLeft().copy();
        point.y += 40;
        if (xhair.x < getCenter().x)
            point.x += 21;
        else
            point.x -= 4;
        return point;
    }

    private Point2f getProjectileOrigin() {
        final float GUN_LENGTH = 37;
        Point2f rot = getRotationOrigin();
        Vector2f delta = new Vector2f(rot, xhair).setMagnitude(GUN_LENGTH);
        return rot.translate(delta);
    }

    private Point2f getRotationOrigin() {
        final Point2f RELATIVE_ORIGIN = new Point2f(10, 4);
        Sprite gun = Game.getSprite("commando_gun_red");
        Point2f origin = getBottomLeft().copy();
        origin.x += gun.offsetX + RELATIVE_ORIGIN.x;
        origin.y += gun.offsetY + gun.height - RELATIVE_ORIGIN.y;

        // Flip horizontally
        if (xhair.x < position.x) {
            origin.x -= 3;
        }

        return origin;
    }

    public void draw(Graphics2D g2) {
        // Draw hitbox
//        g2.setColor(Color.RED);
//        g2.drawRect(0, (int) -height, (int) width, (int) height);

        // Setup arm coordinate space
        final Point2f ARM_ORIGIN = new Point2f(10, 4); // The arms rotation center, in canvas coordinates, relative to the arm sprite
        Graphics2D g3 = (Graphics2D) g2.create();
        g3.translate(arms.offsetX + 1, -(arms.offsetY + arms.height));
        g3.rotate(-new Vector2f(getRotationOrigin(), xhair).getDirection(), ARM_ORIGIN.x, ARM_ORIGIN.y);

        // Flip horizontally
        if (xhair.x < position.x) {
            g2.scale(-1, 1);
            g2.translate(-width, 0);

            g3.translate(3, 8);
            g3.scale(1, -1);
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

        // Debug hitboxes
//        Point2f rot = getRotationOrigin();
//        Point2f proj = getProjectileOrigin();
//        Graphics2D g3 = (Graphics2D) g2.create();
//        g3.setColor(Color.GREEN);
//        g3.translate(canvas.cameraOffsetX, canvas.getHeight() - canvas.cameraOffsetY);


        g2.translate(getBottomLeft().x + canvas.cameraOffsetX, canvas.getHeight() - canvas.cameraOffsetY - getBottomLeft().y);
        draw(g2);

//        g3.fillRect(((int) rot.x), -((int) rot.y), 3, 3);
//        g3.fillRect(((int) proj.x), -((int) proj.y), 3, 3);
    }

    @Override
    public void merge(Character other) {
        super.merge(other);
        if (!(other instanceof Commando))
            return;
        final Commando otherCommando = (Commando) other;
        legs = otherCommando.legs;
        legSpriteTime = otherCommando.legSpriteTime;
        arms = otherCommando.arms;
        armSpriteTime = otherCommando.armSpriteTime;
    }

    @Override
    public String getName() {
        return "Commando";
    }


    @Override
    public CharClass getCharClass() {
        return CharClass.COMMANDO;
    }
}
