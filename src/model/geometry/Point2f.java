package model.geometry;

/**
 * Created by Nathan on 8/20/2015.
 */
public class Point2f {
    public float x, y;

    public Point2f() {
    }

    public Point2f(int x, int y) {
        this.x = (float) x;
        this.y = (float) y;
    }

    public Point2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void translate(Vector2f vector) {
        x += vector.x;
        y += vector.y;
    }

    public void rotate(float angle) {
        rotate(angle, new Point2f(0, 0));
    }

    public void rotate(float angle, Point2f origin) {
//        x = (float) (Math.cos(angle) * (x - origin.x) - Math.sin(angle) * (y - origin.y) + origin.x);
//        y = (float) (Math.sin(angle) * (x - origin.x) + Math.cos(angle) * (y - origin.y) + origin.y);

        x = (float) (origin.x + (x - origin.x) * Math.cos(angle) - (y - origin.y) * Math.sin(angle));
        y = (float) (origin.y + (x - origin.x) * Math.sin(angle) + (y - origin.y) * Math.cos(angle));

    }

    public Point2f displace(Vector2f acceleration, Vector2f velocity, float time) {
        float deltaX = (float) (0.5 * acceleration.x * Math.pow(time, 2) + velocity.x * time);
        float deltaY = (float) (0.5 * acceleration.y * Math.pow(time, 2) + velocity.y * time);
        x += deltaX;
        y += deltaY;
        return this;
    }

    public float distance(Point2f other) {
        return (float) Math.sqrt(Math.pow(other.x - x, 2) + Math.pow(other.y - y, 2));
    }

    public Point2f copy() {
        return new Point2f(x, y);
    }

    public boolean equals(Point2f other) {
        return x == other.x && y == other.y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
