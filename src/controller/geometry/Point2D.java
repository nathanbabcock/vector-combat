package controller.geometry;

/**
 * Created by Nathan on 8/20/2015.
 */
public class Point2D {
    public Float x, y;

    public Point2D(int x, int y) {
        this.x = (float) x;
        this.y = (float) y;
    }

    public Point2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point2D translate(Vector2D vector) {
        float magnitude = vector.magnitude;
        float direction = vector.direction;
        float deltaX = (float) (magnitude * Math.cos(Math.toRadians(direction)));
        float deltaY = (float) (magnitude * Math.sin(Math.toRadians(direction)));
        return new Point2D(x + deltaX, y + deltaY);
    }

    public void translateTo(Vector2D vector) {
        Point2D b = translate(vector);
        x = b.x;
        y = b.y;
    }
}
