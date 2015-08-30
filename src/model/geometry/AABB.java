package model.geometry;

import model.Collision;

/**
 * Created by Nathan on 8/24/2015.
 * <p>
 * Represents an axis-aligned bounding box for collision detection. AABB is defined by a position point and 2 half-width vectors
 */
public class AABB {
    public Point2D position;
    public float width, height;

    public AABB() {
    }

    public AABB(Point2D position, float width, float height) {
        this.width = width;
        this.height = height;
        this.position = position;
    }

    public AABB(float x, float y, float width, float height) {
        this(new Point2D(x, y), width, height);
    }

    public AABB(int x, int y, int width, int height) {
        this((float) x, (float) y, (float) width, (float) height);
    }

    public float getHalfX() {
        return width / 2;
    }

    public float getHalfY() {
        return height / 2;
    }

    public Point2D getCenter() {
        return new Point2D(position.x + getHalfX(), position.y + getHalfY());
    }

    public Point2D getBottomLeft() {
        return position;
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
        if (px < py) {
            float sx = Math.signum(dx);
            collision.delta = new Vector2D(px * sx, 0);
            collision.normal = new Vector2D(sx, 0);
            collision.position = new Point2D(getCenter().x + (getHalfX() * sx), getCenter().y);
        } else {
            float sy = Math.signum(dy);
            collision.delta = new Vector2D(0, py * sy);
            collision.normal = new Vector2D(0, sy);
            collision.position = new Point2D(getCenter().x, getCenter().y + (getHalfY() * sy));
        }
        return collision;
    }

    // TODO collision.position is wrong?

    // TODO fix this
    public Collision collision(Circle2D other) {
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
            collision.delta = new Vector2D(px * sx, 0);
            collision.normal = new Vector2D(sx, 0);
            collision.position = new Point2D(getCenter().x + (getHalfX() * sx), getCenter().y);
        } else {
            float sy = Math.signum(dy);
            collision.delta = new Vector2D(0, py * sy);
            collision.normal = new Vector2D(0, sy);
            collision.position = new Point2D(getCenter().x, getCenter().y + (getHalfY() * sy));
        }
        return collision;
    }

}
