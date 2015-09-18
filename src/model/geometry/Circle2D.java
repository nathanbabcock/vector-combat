package model.geometry;

import java.io.Serializable;

/**
 * Created by Nathan on 8/25/2015.
 */
public class Circle2D implements Serializable {
    public Point2D position;
    public float radius;

    public Circle2D() {
    }

    public Circle2D(Point2D center, float radius) {
        this.position = center;
        this.radius = radius;
    }

    public Circle2D(float x, float y, float radius) {
        position = new Point2D(x, y);
        this.radius = radius;
    }

    public Point2D getCenter() {
        return position;
    }

    public Point2D getBottomLeft() {
        return new Point2D(position.x - radius, position.y - radius);
    }
}
