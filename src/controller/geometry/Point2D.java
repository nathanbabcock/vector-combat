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
        return new Point2D(x + vector.deltaX(), y + vector.deltaY());
    }

    public void translateTo(Vector2D vector) {
        Point2D b = translate(vector);
        x = b.x;
        y = b.y;
    }
}
