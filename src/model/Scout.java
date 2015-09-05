package model;

import model.geometry.Point2D;
import model.geometry.Vector2D;
import view.Canvas;

import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Created by Nathan on 8/31/2015.
 */
public class Scout extends Player {
    public float jetpackVelocity = 50f; //150f;
    public final float maxJumpDelay = 0.25f;
    public boolean extraJump;
    public float jumpDelay;

    public Scout(Game game) {
        super(game);

        extraJump = true;
        attackInterval = 0.01f;
    }

    @Override
    public void updateSprite(float deltaTime) {
        if (wallLeft || wallRight) {
            sprite = game.sprites.get("scout_walljump");
        } else if (walkingLeft || walkingRight) {
            float spriteInterval = 0.25f;
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

        if (onGround || wallLeft || wallRight)
            extraJump = true;
    }

    @Override
    public void attack(float deltaTime) {
        if (currentAttackDelay > 0)
            currentAttackDelay -= deltaTime;

        if (!attacking || currentAttackDelay > 0)
            return;

        Bullet bullet = new Bullet(game, getCenter().x, getCenter().y, Bullet.SIZE);
        Point2D origin = getCenter();
        bullet.owner = this;
        bullet.velocity = new Vector2D(xhair.x - origin.x, xhair.y - origin.y);
        bullet.velocity.setMagnitude(Bullet.VELOCITY);
        game.entities.add(bullet);
        currentAttackDelay = attackInterval;
    }

    @Override
    public void jump(float deltaTime) {
        if (jumpDelay > 0)
            jumpDelay -= deltaTime;

        if (!jumping) return;

        // Normal jump
        if (onGround) {
            velocity.y = jumpVelocity;
            jumpDelay = maxJumpDelay;
        }

        // Wall jump
        else if (wallLeft) {
            Vector2D wallJump = new Vector2D(1, 1);
            wallJump.setMagnitude(jumpVelocity);
            velocity.add(wallJump);
            jumpDelay = maxJumpDelay;
        } else if (wallRight) {
            Vector2D wallJump = new Vector2D(1, 1);
            wallJump.setMagnitude(jumpVelocity);
            velocity.add(wallJump);
            jumpDelay = maxJumpDelay;
        }

        // Double jump
        else if (!onGround && extraJump && jumpDelay <= 0) {
            velocity.y = jumpVelocity;
            extraJump = false;
        }
    }

    private void generateParticleTrail(float deltaTime) {
/*        final int AVG_PARTICLES = 30;
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
        }*/
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
        int sgX = playerX + 8;
        int sgY = playerY + 24;
        Point2D sgOrigin = new Point2D(playerX + 16, playerY + 36);
        Vector2D sgVector = new Vector2D(xhair.x - (getBottomLeft().x + 12), -xhair.y + (getBottomLeft().y + 36));

        if (xhair.x < getCenter().x) {
            playerWidth *= -1;
            playerX += sprite.width - sprite.offsetX;

            sgWidth *= -1;
            sgX += 8;
        }

        g2.drawImage(sprite.image, playerX, playerY, playerWidth, playerHeight, null);

        AffineTransform trans = new AffineTransform();
        trans.rotate(sgVector.getDirection(), sgOrigin.x, sgOrigin.y); // the points to rotate around (the center in my example, your left side for your problem)
        g2.transform(trans);
//            g2d.drawImage( image, sprite.x, sprite.y );  // the actual location of the sprite

        g2.drawImage(sg.image, sgX, sgY, sgWidth, sg.height, null);
        g2.setTransform(canvas.backup);
    }
}
