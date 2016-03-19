package model.entities;

import model.Collision;
import model.Game;
import model.Player;
import model.Sprite;
import model.characters.Character;
import model.characters.Team;
import model.geometry.Point2f;
import model.geometry.Polygon;
import model.geometry.Vector2f;
import view.Canvas;
import view.GUI;

import java.awt.*;

/**
 * Created by Nathan on 3/18/2016.
 */
public class Flag extends Polygon {
    static final float flag_countdown = 30;

    public Team team;

    Point2f spawn;

    float countdown;
    boolean inBase = false;
    byte holder = -1;

    public Flag() {
        super();
    }

    public Flag(Game game, Team team) {
        super(game);
        this.team = team;
        makeAABB(0, 0, 22, 41);
    }

    public Flag setSpawn(Point2f spawn) {
        this.spawn = spawn;
        return this;
    }

    public void drop() {
        holder = -1;
        acceleration = new Vector2f(0, Game.GRAVITY);
        countdown = flag_countdown;
    }

    public void reset() {
        setPosition(spawn);
        inBase = true;
        holder = -1;
    }

    @Override
    public void update(float deltaTime) {
        if (!inBase) {
            // Outside map bounds
            if (game.map.outsideBoundaries(this))
                reset();

            if (holder != -1) { // Player holding flag
                Player player = game.getPlayer(holder);
                if (player.team == team || player.character == null) { // Drop flag
                    drop();
                    return;
                }
                velocity = player.character.velocity;
                setPosition(player.character.getPosition());
            } else if (countdown <= 0) {// Flag not in base and not held
                reset();
            } else {
                countdown -= deltaTime;
                applyPhysics(deltaTime);
            }
        }
        checkCollisions();
    }

    @Override
    public void checkCollisions() {
        if (holder != -1) {
            Flag otherFlag = team == Team.RED ? game.map.blueflag : game.map.redflag;
            Character holder = game.getPlayer(this.holder).character;
            if (holder == null || !otherFlag.inBase) return;
            Collision c = holder.collision(otherFlag);
            if (c != null)
                handleCollision(c);
            return;
            // handle capping
        }

        // Players grabbing flag
        for (Player player : game.players) {
            if (player.character == null || player.team == team) continue;
            Collision collision = collision(player.character);
            if (collision != null)
                handleCollision(collision);
        }

        // Flag falling into map
        if (!inBase) {
            for (Polygon box : game.map.statics) {
                Collision collision = collision(box);
                if (collision != null)
                    handleCollision(collision);
            }
        }

    }

    @Override
    public void handleCollision(Collision collision) {
        if (collision.collider instanceof Character) { // Player taking flag
            System.out.println("A PLAYER HAS TAKEN THE FLAG");
            inBase = false;
            holder = ((Character) collision.collider).player.clientID;
        } else if (collision.collider instanceof Flag) {
            if (team == Team.RED)
                game.blueScore++;
            else
                game.redScore++;
            reset();
        } else { // Flag falling to ground
            translate(collision.delta);
            if (collision.delta.y != 0) {
                velocity.y = 0f;
                if (collision.delta.y > 0)
                    velocity.x = 0f;
            }
            if (collision.delta.x != 0)
                velocity.x = 0f;
        }
    }

    @Override
    public void draw(Canvas canvas, Graphics2D g2) {
        g2 = (Graphics2D) g2.create();
        g2.setFont(GUI.FONT_HEADING.deriveFont(35f));
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // No holder
        if (holder == -1) {
            g2.translate(getBottomLeft().x + canvas.cameraOffsetX, canvas.getHeight() - canvas.cameraOffsetY - getBottomLeft().y - getHeight());
            Sprite sprite = Sprite.getSprite(team == Team.RED ? "flag_red" : "flag_blue");
            g2.drawImage(sprite.image, 0, 0, null);

            if (!inBase) {
                g2.setColor(Color.WHITE);
                GUI.drawString_centerHoriz(g2, (int) countdown + "", sprite.width / 2, -10, 0);
            }

            return;
        }

        g2.setColor(team == Team.RED ? Color.RED : Color.BLUE);

        // Holder
        Character holder = game.getPlayer(this.holder).character;
        if (holder == null) return;
        g2.translate(holder.getBottomLeft().x + canvas.cameraOffsetX, canvas.getHeight() - canvas.cameraOffsetY - holder.getBottomLeft().y - holder.getHeight() - 40);

        // Draw triangle
        final int triangle_width = 30;
        final int triangle_height = 15;
        int midpoint = (int) holder.getWidth() / 2;
        g2.fillPolygon(
                new int[]{midpoint - triangle_width / 2, midpoint, midpoint + triangle_width / 2},
                new int[]{-triangle_height, 0, -triangle_height},
                3);

        // Draw team name
        GUI.drawString_centerHoriz(g2, "FLAG", midpoint, -(triangle_height + 15), 0);

        //draw(g2);
    }
}
