package model.geometry;

/**
 * Created by Nathan on 8/20/2015.
 */
public class Vector2D {
    public Float x, y;

    public Vector2D(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2D(Point2D a, Point2D b) {
        this(b.x - a.x, b.y - a.y);
    }

    public Vector2D add(Vector2D other) {
        x += other.x;
        y += other.y;
        return this;
    }

    public Vector2D scale(float scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }

    public Vector2D copy() {
        return new Vector2D(x, y);
    }

    public float getMagnitude() {
        return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public Vector2D setMagnitude(Float magnitude) {
        float ratio = magnitude / getMagnitude();
        return scale(ratio);
    }

    public void setDirection(float angle) {
        float m = getMagnitude();
        x = (float) (m * Math.cos(angle));
        y = (float) (m * Math.sin(angle));
    }

    public float getDirection() {
        return (float) Math.atan(y / x);
//        double theta = Math.atan(x/y);
//
//        if(x >= 0 && y < 0)
//            theta += Math.toRadians(180);
//        else if(x <= 0 && y < 0)
//            theta += Math.toRadians(180);
//
//        return theta;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
