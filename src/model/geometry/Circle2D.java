package model.geometry;

/**
 * Created by Nathan on 8/25/2015.
 */
public class Circle2D {
    public Point2D center;
    public float radius;

    public Circle2D(Point2D center, float radius) {
        this.center = center;
        this.radius = radius;
    }

    public Circle2D(float x, float y, float radius) {
        center = new Point2D(x, y);
        this.radius = radius;
    }
}
