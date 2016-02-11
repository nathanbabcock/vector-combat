package model.geometry;

import model.Collision;
import model.Game;
import model.entities.Entity;
import view.Canvas;

import java.awt.*;

/**
 * Created by Nathan on 8/24/2015.
 * <p>
 * Represents an axis-aligned bounding box for collision detection. AABB is defined by a position point and 2 half-width vectors
 */
public class AABB extends Entity {
    public float width, height;

    public AABB() {
    }

    public AABB(Game game, Point2f position, float width, float height) {
        super(game, position);
        this.width = width;
        this.height = height;
    }

    public AABB(Game game, float x, float y, float width, float height) {
        this(game, new Point2f(x, y), width, height);
    }

    public AABB(Game game, int x, int y, int width, int height) {
        this(game, (float) x, (float) y, (float) width, (float) height);
    }

    public float getHalfX() {
        return width / 2;
    }

    public float getHalfY() {
        return height / 2;
    }

    public Point2f getCenter() {
        return new Point2f(position.x + getHalfX(), position.y + getHalfY());
    }

    public Point2f getBottomLeft() {
        return position;
    }

    @Override
    public void draw(Canvas canvas, Graphics2D g2) {
    }

    public Collision collision(AABB other) {
        float dx = getCenter().x - other.getCenter().x;
        float px = (other.getHalfX() + getHalfX()) - Math.abs(dx);
        if (px <= 0)
            return null;

        float dy = getCenter().y - other.getCenter().y;
        float py = (other.getHalfY() + getHalfY()) - Math.abs(dy);
        if (py <= 0)
            return null;

        Collision collision = new Collision();
        collision.collider = this;
        if (px < py) {
            float sx = Math.signum(dx);
            collision.delta = new Vector2f(px * sx, 0);
            collision.normal = new Vector2f(sx, 0);
            collision.position = new Point2f(getCenter().x + (getHalfX() * sx), getCenter().y);
        } else {
            float sy = Math.signum(dy);
            collision.delta = new Vector2f(0, py * sy);
            collision.normal = new Vector2f(0, sy);
            collision.position = new Point2f(getCenter().x, getCenter().y + (getHalfY() * sy));
        }
        return collision;
    }

    // TODO collision.position is wrong?

    // TODO fix this
    public Collision collision(Circle other) {
        float dx = other.getCenter().x - getCenter().x;
        float px = (other.radius + getHalfX()) - Math.abs(dx);
        if (px <= 0)
            return null;

        float dy = other.getCenter().y - getCenter().y;
        float py = (other.radius + getHalfY()) - Math.abs(dy);
        if (py <= 0)
            return null;

        Collision collision = new Collision();
        if (px < py) {
            float sx = Math.signum(dx);
            collision.delta = new Vector2f(px * sx, 0);
            collision.normal = new Vector2f(sx, 0);
            collision.position = new Point2f(getCenter().x + (getHalfX() * sx), getCenter().y);
        } else {
            float sy = Math.signum(dy);
            collision.delta = new Vector2f(0, py * sy);
            collision.normal = new Vector2f(0, sy);
            collision.position = new Point2f(getCenter().x, getCenter().y + (getHalfY() * sy));
        }
        return collision;
    }

}
