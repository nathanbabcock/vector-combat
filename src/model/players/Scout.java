package model.players;

import model.Game;
import model.Sprite;
import model.entities.Bullet;
import model.geometry.Point2D;
import model.geometry.Vector2D;
import model.particles.Particle;
import view.Canvas;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Random;
import java.util.UUID;

/**
 * Created by Nathan on 8/31/2015.
 */
public class Scout extends Player {
    public final float maxJumpDelay = 0.25f;
    public final float wallVelocity = -50f;

    public boolean extraJump;
    public float jumpDelay;

    public Scout(Game game) {
        super(game);

        extraJump = true;
        attackInterval = 1f;
    }

    @Override
    public void updateSprite(float deltaTime) {
        if (wallLeft || wallRight) {
            sprite = game.sprites.get("scout_walljump");
        } else if (movingLeft || movingRight) {
            float spriteInterval = 0.25f;
            if (sprite == game.sprites.get("scout_walljump"))
                sprite = game.sprites.get("scout_standing");
            if (spriteTime >= spriteInterval) {
                if (sprite == game.sprites.get("scout_standing")) {
                    sprite = game.sprites.get("scout_walking");
                } else if (sprite == game.sprites.get("scout_walking"))
                    sprite = game.sprites.get("scout_standing");
                spriteTime = 0;
            }
        } else {
            sprite = game.sprites.get("scout_standing");
        }
        spriteTime += deltaTime;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        // Reset double jump
        if (onGround || wallLeft || wallRight)
            extraJump = true;

        // Stick to walls and slide down them
        if ((wallLeft || wallRight) && velocity.y < 0) {
            velocity.y = wallVelocity;
            generateParticleTrail(deltaTime);
        }
    }

    @Override
    public void attack(float deltaTime) {
        float NUM_PELLETS = 6;
        float MAX_SPREAD = 150;
        float PELLET_SIZE = 6;
        float PELLET_VELOCITY = 600;

        if (currentAttackDelay > 0)
            currentAttackDelay -= deltaTime;

        if (!attacking || currentAttackDelay > 0)
            return;

        Random r = new Random();
        for (int i = 0; i < NUM_PELLETS; i++) {
            Bullet bullet = new Bullet(game, getCenter().x, getCenter().y, PELLET_SIZE);
            Point2D origin = getCenter();
            bullet.owner = this;
            bullet.velocity = new Vector2D(xhair.x - origin.x, xhair.y - origin.y);
            bullet.velocity.setMagnitude(PELLET_VELOCITY);
            bullet.velocity.x += r.nextInt((int) ((MAX_SPREAD * 2 + 1) - MAX_SPREAD));
            bullet.velocity.y += r.nextInt((int) ((MAX_SPREAD * 2 + 1) - MAX_SPREAD));
            game.entities.put(UUID.randomUUID().toString(), bullet);
        }
        currentAttackDelay = attackInterval;
    }

    @Override
    public void jump(float deltaTime) {
        // TODO refactor to avoid duplicate code
        if (jumpDelay > 0)
            jumpDelay -= deltaTime;

        if (!movingUp) return;

        // Normal jump
        if (onGround) {
            velocity.y = jumpVelocity;
            jumpDelay = maxJumpDelay;
        }

        // Double jump
        else if (!onGround && !wallLeft && !wallRight && extraJump && jumpDelay <= 0) {
            Vector2D jump;
            if (movingLeft)
                jump = new Vector2D(-1, 1);
            else if (movingRight)
                jump = new Vector2D(1, 1);
            else
                jump = new Vector2D(0, 1);
            jump.setMagnitude(jumpVelocity);
//            velocity.y = jumpVelocity;
            velocity = jump;
            extraJump = false;
        }
    }

    @Override
    public void move(float deltaTime) {
        // Wall jump
        if (movingRight && wallLeft) {
            Vector2D wallJump = new Vector2D(1, 1);
            wallJump.setMagnitude(jumpVelocity);
            velocity = wallJump;
            jumpDelay = maxJumpDelay;
        } else if (movingLeft && wallRight) {
            Vector2D wallJump = new Vector2D(-1, 1);
            wallJump.setMagnitude(jumpVelocity);
            velocity = wallJump;
            jumpDelay = maxJumpDelay;
        } else {
            super.move(deltaTime);
        }
    }

    private void generateParticleTrail(float deltaTime) {
        // TODO reduce particles

        // Particle effects
        final int AVG_PARTICLES = 1;
        final int AVG_SIZE = 5;
        final int MAX_DEVIATION = 3;
        final int AVG_VELOCITY = 100;

        Random r = new Random();
        for (int i = 0; i < AVG_PARTICLES; i++) {
            Particle particle = new Particle(game);
            particle.position = getBottomLeft().copy();
            if (wallRight)
                particle.position.x += width;
            int sign;
            if (r.nextBoolean())
                sign = -1;
            else
                sign = 1;
            particle.size = AVG_SIZE + (r.nextInt(MAX_DEVIATION + 1) * sign);
            particle.color = new Color(0, 0, 0);
            particle.angle = (float) Math.toRadians(r.nextInt(360));
            particle.growth = 0;// -15; // - (r.nextInt(5) + 10);
            particle.rotation = (float) Math.toRadians(r.nextInt(361));
            particle.velocity = new Vector2D(r.nextInt(AVG_VELOCITY * 2) - AVG_VELOCITY, r.nextInt(AVG_VELOCITY * 2) - AVG_VELOCITY);
            particle.acceleration = new Vector2D(0, game.gravity);
            game.particles.add(particle);
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

        // Rocket launcher
        // Draw rocket
        Sprite sg = game.sprites.get("scout_gun");
        int sgWidth = sg.width;
        int sgHeight = sg.height;
        int sgX = playerX - sprite.offsetX + 8;
        int sgY = playerY + 24;
        Point2D sgOrigin = new Point2D(playerX - sprite.offsetX + 16, playerY + 36);
        Vector2D sgVector = new Vector2D(xhair.x - (getBottomLeft().x + 12), -xhair.y + (getBottomLeft().y + 36));

        // TODO refactor to avoid code duplication
        if (wallRight) {
            playerWidth *= -1;
            playerX += sprite.width - sprite.offsetX - 4;

            if (xhair.x < getCenter().x) {
                sgHeight *= -1;
                sgY += 24;
                sgX += 8;
            }
        } else if (wallLeft) {
            sgX += 4;

            if (xhair.x < getCenter().x) {
                sgHeight *= -1;
                sgY += 24;
                sgX += 8;
            }
        } else if (xhair.x < getCenter().x) {
            playerWidth *= -1;
            playerX += sprite.width - sprite.offsetX;

            sgHeight *= -1;
            sgY += 24;
            sgX += 8;

        }

        g2.drawImage(sprite.image, playerX, playerY, playerWidth, playerHeight, null);

        AffineTransform trans = new AffineTransform();
        trans.rotate(sgVector.getDirection(), sgOrigin.x, sgOrigin.y); // the points to rotate around (the center in my example, your left side for your problem)
        g2.transform(trans);
//            g2d.drawImage( image, sprite.x, sprite.y );  // the actual location of the sprite

        g2.drawImage(sg.image, sgX, sgY, sgWidth, sgHeight, null);
        g2.setTransform(canvas.backup);
    }
}
