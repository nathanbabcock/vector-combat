package model.geometry;

import model.Collision;

/**
 * Created by Nathan on 8/24/2015.
 * <p>
 * Represents an axis-aligned bounding box for collision detection. AABB is defined by a center point and 2 half-width vectors
 */
public class AABB {
    public Point2D center;
    public float halfX, halfY;

    public AABB(Point2D center, float halfX, float halfY) {
        this.center = center;
        this.halfX = halfX;
        this.halfY = halfY;
    }

    public AABB(float x0, float y0, float x1, float y1) {
        halfX = Math.abs(x1 - x0) / 2;
        halfY = Math.abs(y1 - y0) / 2;
        center = new Point2D(x0 + halfX, y0 + halfY);
    }

    public Collision collision(AABB other) {
        float dx = center.x - other.center.x;
        float px = (other.halfX + halfX) - Math.abs(dx);
        if (px <= 0)
            return null;

        float dy = center.y - other.center.y;
        float py = (other.halfY + halfY) - Math.abs(dy);
        if (py <= 0)
            return null;

        Collision collision = new Collision();
        if (px < py) {
            float sx = Math.signum(dx);
            collision.delta = new Vector2D(px * sx, 0);
            collision.normal = new Vector2D(sx, 0);
            collision.position = new Point2D(center.x + (halfX * sx), center.y);
        } else {
            float sy = Math.signum(dy);
            collision.delta = new Vector2D(0, py * sy);
            collision.normal = new Vector2D(0, sy);
            collision.position = new Point2D(center.x, center.y + (halfY * sy));
        }
        return collision;
    }

    // TODO refactor and combine above
    public Collision collision(Circle2D other) {
        float dx = other.center.x - center.x;
        float px = (other.radius + halfX) - Math.abs(dx);
        if (px <= 0)
            return null;

        float dy = other.center.y - center.y;
        float py = (other.radius + halfY) - Math.abs(dy);
        if (py <= 0)
            return null;

        Collision collision = new Collision();
        if (px < py) {
            float sx = Math.signum(dx);
            collision.delta = new Vector2D(px * sx, 0);
            collision.normal = new Vector2D(sx, 0);
            collision.position = new Point2D(center.x + (halfX * sx), center.y);
        } else {
            float sy = Math.signum(dy);
            collision.delta = new Vector2D(0, py * sy);
            collision.normal = new Vector2D(0, sy);
            collision.position = new Point2D(center.x, center.y + (halfY * sy));
        }
        return collision;
    }

}
