package model.characters;

import model.Player;
import model.Sprite;
import model.entities.Bullet;
import model.geometry.Point2D;
import model.geometry.Vector2D;
import model.particles.Fire;
import model.particles.Particle;
import view.Canvas;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;

/**
 * Created by Nathan on 8/31/2015.
 */
public class Soldier extends Character {
    public float jetpackVelocity = 250f;

    public Soldier() {
    }

    public Soldier(Player player) {
        super(player);

        attackInterval = 0.1f;
    }

    @Override
    public void updateSprite(float deltaTime) {
        if (movingLeft || movingRight) {
            float spriteInterval = 0.25f;
            if (spriteTime >= spriteInterval) {
                if (sprite == game.sprites.get("soldier_standing")) {
                    sprite = game.sprites.get("soldier_walking");
                } else if (sprite == game.sprites.get("soldier_walking"))
                    sprite = game.sprites.get("soldier_standing");
                spriteTime = 0;
            }
        } else {
            sprite = game.sprites.get("soldier_standing");
        }
        spriteTime += deltaTime;
    }

    @Override
    public void attack(float deltaTime) {
        if (currentAttackDelay > 0)
            currentAttackDelay -= deltaTime;

        if (!attacking || currentAttackDelay > 0)
            return;

        Bullet bullet = new Bullet(game, getCenter().x, getCenter().y, Bullet.SIZE);
        Point2D origin = getCenter();
        bullet.owner = player;
        bullet.velocity = new Vector2D(xhair.x - origin.x, xhair.y - origin.y);
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

    private Point2D getJetpackOrigin() {
        Point2D point = getBottomLeft().copy();
        point.y += 40;
        if (xhair.x < getCenter().x)
            point.x += 28;
        else
            point.x -= 4;
        return point;
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

        // Rocket launcher
        // Draw rocket
        Sprite mg = game.sprites.get("soldier_gun");
        int mgWidth = mg.width;
        int mgHeight = mg.height;
        int mgX = playerX + 16;
        int mgY = playerY + 24;
        Point2D mgOrigin = new Point2D(playerX + 16, playerY + 36);
        Vector2D mgVector = new Vector2D(xhair.x - (getBottomLeft().x + 12), -xhair.y + (getBottomLeft().y + 36));

        if (xhair.x < getCenter().x) {
            playerWidth *= -1;
            playerX += sprite.width - sprite.offsetX;

//            mgWidth *= -1;
            mgHeight *= -1;
            mgY += 24;
            mgX -= 8;
        }

        g2.drawImage(sprite.image, playerX, playerY, playerWidth, playerHeight, null);

        AffineTransform trans = new AffineTransform();
        trans.rotate(mgVector.getDirection(), mgOrigin.x, mgOrigin.y); // the points to rotate around (the center in my example, your left side for your problem)
        g2.transform(trans);
//            g2d.drawImage( image, sprite.x, sprite.y );  // the actual location of the sprite

        g2.drawImage(mg.image, mgX, mgY, mgWidth, mgHeight, null);
        g2.setTransform(canvas.backup);
    }
}
