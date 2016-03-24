package characters;

import core.Player;
import geometry.Collision;
import geometry.Point2f;
import geometry.Polygon;
import geometry.Vector2f;
import maps.JumpPad;
import network.InputState;
import particles.Particle;
import view.Canvas;
import view.GUI;
import view.Sprite;

import java.awt.*;
import java.util.Random;

/**
 * Created by Nathan on 8/19/2015.
 */
abstract public class Character extends Polygon {
    // Constants
    public transient Float moveSpeed = 200f;
    public transient Float jumpVelocity = 300f;
    public transient Float attackInterval = 1.0f;

    public transient Player player;
    public Point2f xhair;
    public int health;
    public float currentAttackDelay;

    // States, written to by controls and read from for sprites
    public boolean movingLeft, movingRight, movingUp, movingDown, attacking, altAttacking, dead, onGround, wallLeft, wallRight;

    public transient Sprite sprite;
    public transient float spriteTime;

    // Final variables from Shape2D
//    public final Point2D position;
//    public final float width, height;

    public Character() {
    }

    public Character(Player player) {
        super(player.game);//, Polygon.makeAABB(0, 0, 24, 80));
        this.player = player;
//        position = hitbox.position;// = new Point2D(400, 850);
//        width = hitbox.width;
//        height = hitbox.height;
        xhair = new Point2f(0, 0);
        health = 200;
        updateSprite(0);
    }

    /**
     * This method handles merging a character received over the network with the local copy.
     * Specifically it copies over the transient fields that the server copy is missing.
     *
     * @param other
     */
    public void merge(Character other) {
        moveSpeed = other.moveSpeed;
        jumpVelocity = other.jumpVelocity;
        attackInterval = other.attackInterval;
        sprite = other.sprite;
        spriteTime = other.spriteTime;
    }

    @Override
    public void update(float deltaTime) {
        if (game.map.outsideBoundaries(this)) {
            player.kill();
            return;
        }

//        if (getCenter().x > game.map.width || getCenter().y > game.map.height || getCenter().x < 0 || getCenter().y < 0) {
//            player.kill();
//            return;
//        }

        if (game.countdown <= 0) {
            jump(deltaTime);
            move(deltaTime);
            attack(deltaTime);
            altAttack(deltaTime);
        }
        checkHealth();
        applyPhysics(deltaTime);
        checkCollisions();
        updateSprite(deltaTime);
    }

    public void jump(float deltaTime) {
        if (onGround && movingUp)
            velocity.add(new Vector2f(0, jumpVelocity));
    }

    public void kill() {
    }

    public void move(float deltaTime) {
        // Calculate actual velocity by applying controls
        if (onGround) {
            if (movingRight)
                velocity.x = moveSpeed;
            if (movingLeft)
                velocity.x = -moveSpeed;
        } else {
            if (movingRight) {
                if (velocity.x >= moveSpeed)
                    return;
                velocity.x += moveSpeed * deltaTime * 2.5f;
            }
            if (movingLeft) {
                if (velocity.x <= -moveSpeed)
                    return;
                velocity.x -= moveSpeed * deltaTime * 2.5f;
            }
        }
    }

    public void checkCollisions() {
        // Reset states
        onGround = wallLeft = wallRight = false;

        // Check collisions
        for (Polygon box : game.map.statics) {
            Collision collision = collision(box);
            if (collision != null)
                handleCollision(collision);
        }
    }

    public void handleCollision(Collision collision) {
        // Jump pads
        if (collision.collider instanceof JumpPad) {
            translate(collision.delta);
            //translate(0, 80);
            velocity = ((JumpPad) collision.collider).velocity.copy();
            onGround = false;
            return;
        }

        if (collision.delta.x > 0) {
            if (collision.delta.y == 0)
                wallLeft = true;
            if (velocity.x < 0)
                velocity.x = 0f;
        } else if (collision.delta.x < 0) {
            if (collision.delta.y == 0)
                wallRight = true;
            if (velocity.x > 0)
                velocity.x = 0f;
        }

//        boolean handled = false;
        if (2 * collision.delta.y > Math.abs(collision.delta.x)) {
            // Custom slope handling
//            handled = true;
//            Point2f probe = getBottomLeft().translate(getWidth() / 2, 0);
//            Collision c = probe.collision((Polygon) collision.collider, collision.delta.copy().normalize());
//            if(c != null) {
            onGround = true;
            if (!movingUp)
                velocity.x = 0f;
            velocity.y = 0f;
            acceleration.y = 0f;
//                translate(c.delta);
//            }
        } else if (collision.delta.y < 0) {
            // wallAbove = true;
            if (velocity.y > 0)
                velocity.y = 0f;
        }
//        if(!handled)
        translate(collision.delta);
    }

    public void damage(int damage, Point2f position, Player dealer) {
        health -= damage;
        generateBloodParticles();
        checkHealth();

        if (health <= 0) {
            dealer.kills++;
            player.deaths++;
            game.checkWin();
        }

    }

    private void checkHealth() {
        if (health <= 0)
            player.kill();
    }

    public void generateBloodParticles() {
        // Particle effects
        final int AVG_PARTICLES = 6;
        final int AVG_SIZE = 5;
        final int MAX_DEVIATION = 3;
        final int AVG_VELOCITY = 100;

        Random r = new Random();
        for (int i = 0; i < AVG_PARTICLES; i++) {
            Particle particle = new Particle(game);
            particle.position = getCenter().copy();
            int sign;
            if (r.nextBoolean())
                sign = -1;
            else
                sign = 1;
            particle.size = AVG_SIZE + (r.nextInt(MAX_DEVIATION + 1) * sign);
            particle.color = new Color(255, 0, 0);
            particle.angle = (float) Math.toRadians(r.nextInt(360));
            particle.growth = 0;// -15; // - (r.nextInt(5) + 10);
            particle.rotation = (float) Math.toRadians(r.nextInt(361));
            particle.velocity = new Vector2f(r.nextInt(AVG_VELOCITY * 2) - AVG_VELOCITY, r.nextInt(AVG_VELOCITY * 2) - AVG_VELOCITY);
            particle.acceleration = new Vector2f(0, game.GRAVITY);
            game.particles.add(particle);
        }
    }

    public void importState(InputState state) {
        movingLeft = state.movingLeft;
        movingRight = state.movingRight;
        movingDown = state.movingDown;
        movingUp = state.movingUp;
        attacking = state.attacking;
        altAttacking = state.altAttacking;
        xhair = state.xhair;
    }

    abstract public void updateSprite(float deltaTime);

    abstract public void attack(float deltaTime);

    public void altAttack(float deltaTime) {
    } // Unused by default(?)

    abstract public void draw(Graphics2D g2);

    abstract public void draw(Canvas canvas, Graphics2D g2);

    public void draw(Canvas canvas, Graphics2D g2, String clientName) {
        // Draw client name
        // Player
        int playerX = (int) getBottomLeft().x + canvas.cameraOffsetX;
        int playerY = (int) (canvas.getHeight() - canvas.cameraOffsetY - getBottomLeft().y - getHeight());
        int playerWidth = (int) getWidth();//24;

//        final int fontSize = 14;
//        g2.setFont(GameClient.FONT_TEXT);
        final int fontSize = 12;
        g2.setFont(new Font("Lucida Sans", Font.PLAIN, fontSize));
        if (player.team == Team.BLUE)
            g2.setColor(Color.BLUE);
        else if (player.team == Team.RED)
            g2.setColor(Color.RED);
        else
            g2.setColor(Color.BLACK);
        int estWidth = clientName.length() * fontSize;
//        g2.drawString(clientName, playerX + playerWidth - (estWidth / 2), playerY - fontSize);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setFont(GUI.FONT_SEMIBOLD.deriveFont(14f));
        GUI.drawString_centerHoriz(g2, clientName, playerX + playerWidth / 2, playerY - 15, 0);
        draw(canvas, g2);
    }

    abstract public String getName();

    abstract public CharClass getCharClass();

}
