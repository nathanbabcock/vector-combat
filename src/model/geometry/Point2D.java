package model.geometry;

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
        return new Point2D(x + vector.x, y + vector.y);
    }

    public Point2D displace(Vector2D acceleration, Vector2D velocity, float time) {
        float deltaX = (float) (0.5 * acceleration.x * Math.pow(time, 2) + velocity.x * time);
        float deltaY = (float) (0.5 * acceleration.y * Math.pow(time, 2) + velocity.y * time);
        return new Point2D(x + deltaX, y + deltaY);
    }

    public String toString() {
        return "(" + x + ", " + y + ")";
    }

    public boolean equals(Point2D other) {
        return x.equals(other.x) && y.equals(other.y);
    }

    public float distance(Point2D other){
        return (float) Math.sqrt(Math.pow(other.x - x, 2) + Math.pow(other.y - y, 2));
    }

//    public void translateTo(Vector2D vector) {
//        halfX += vector.halfX;
//        y += vector.y;
//    }
}
