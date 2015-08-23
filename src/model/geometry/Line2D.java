package model.geometry;

/**
 * Created by Nathan on 8/22/2015.
 */
public class Line2D {
    public Point2D a, b;

    public Line2D(Point2D a, Point2D b) {
        this.a = a;
        this.b = b;
    }

    public Line2D(float x0, float y0, float x1, float y1) {
        a = new Point2D(x0, y0);
        b = new Point2D(x1, y1);
    }

    public float slope() {
        return (b.y - a.y) / (b.x - a.x);
    }

    public float intercept() {
        return a.y - (slope() * a.x);
    }

    public Point2D intersection(Line2D other) {
        float x, y;
        if (a.x.equals(b.x)) {
            x = a.x;
            y = other.slope() * x + other.intercept();
        } else if (other.a.x.equals(other.b.x)) {
            x = other.a.x;
            y = slope() * x + intercept();
        } else {
            x = (other.intercept() - intercept()) / (slope() - other.slope());
            y = slope() * x + intercept();
        }
        // TODO more elegant approach to this mess?
        if (x >= Math.min(a.x, b.x) && x <= Math.max(a.x, b.x) && y >= Math.min(a.y, b.y) && y <= Math.max(a.y, b.y)
                && (x >= Math.min(other.a.x, other.b.x) && x <= Math.max(other.a.x, other.b.x) && y >= Math.min(other.a.y, other.b.y) && y <= Math.max(other.a.y, other.b.y)))
            return new Point2D(x, y);
        return null;
    }
}
