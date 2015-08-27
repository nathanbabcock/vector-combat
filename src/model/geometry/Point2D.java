package model.geometry;

/**
 * Created by Nathan on 8/20/2015.
 */
public class Point2D {
    public float x, y;

    public Point2D(int x, int y) {
        this.x = (float) x;
        this.y = (float) y;
    }

    public Point2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void translate(Vector2D vector) {
        x += vector.x;
        y += vector.y;
    }

    public void displace(Vector2D acceleration, Vector2D velocity, float time) {
        float deltaX = (float) (0.5 * acceleration.x * Math.pow(time, 2) + velocity.x * time);
        float deltaY = (float) (0.5 * acceleration.y * Math.pow(time, 2) + velocity.y * time);
        x += deltaX;
        y += deltaY;
    }

    public float distance(Point2D other) {
        return (float) Math.sqrt(Math.pow(other.x - x, 2) + Math.pow(other.y - y, 2));
    }

    public Point2D copy() {
        return new Point2D(x, y);
    }

    public boolean equals(Point2D other) {
        return x == other.x && y == other.y;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
