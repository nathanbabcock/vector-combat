package model.geometry;

/**
 * Created by Nathan on 8/20/2015.
 */
public class Vector2f {
    public Float x, y;

    public Vector2f() {
    }

    public Vector2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2f(Point2f a, Point2f b) {
        this(b.x - a.x, b.y - a.y);
    }

    public Vector2f add(Vector2f other) {
        x += other.x;
        y += other.y;
        return this;
    }

    public Vector2f scale(float scalar) {
        x *= scalar;
        y *= scalar;
        return this;
    }

    public Vector2f copy() {
        return new Vector2f(x, y);
    }

    public float getMagnitude() {
        return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public Vector2f setMagnitude(Float magnitude) {
        float ratio = magnitude / getMagnitude();
        return scale(ratio);
    }

    public Vector2f setDirection(float angle) {
        float m = getMagnitude();
        float dangle = (float) Math.toDegrees(angle);
        if (dangle >= 0 && dangle < 90) {
            x = (float) (m * Math.cos(angle));
            y = (float) (m * Math.sin(angle));
        } else if (dangle >= 90 && dangle < 180) {
            x = (float) (-m * (Math.cos(Math.toRadians(180) - angle)));
            y = (float) (m * (Math.sin(Math.toRadians(180) - angle)));
        } else if (dangle >= 180 && dangle < 270) {
            x = (float) (-m * (Math.cos(angle - Math.toRadians(180))));
            y = (float) (-m * (Math.sin(angle - Math.toRadians(180))));
        } else {
            x = (float) (m * (Math.cos(Math.toRadians(360) - angle)));
            y = (float) (-m * (Math.sin(Math.toRadians(360) - angle)));
        }
        return this;
    }

    public float getDirection() {
//        float angle = (float) Math.atan(y / x);
//        double _360 = Math.toRadians(360);
//        double _0 = Math.toRadians(0);
//        while (angle < _0) angle += _360;
//        while (angle > _360) angle -= _360;
//
//        if(y < 0)
//            angle += Math.toRadians(360);

        // Handle vertical case (division by zero)
        if (x == 0) {
            if (y > 0)
                return (float) Math.toRadians(90);
            else
                return (float) Math.toRadians(270);
        }
/*
        // Handle horizontal
        if(y == 0){
            if(x > 0)
                return (float) Math.toRadians(0);
            else
                return (float) Math.toRadians(180);
        }*/

        if (x > 0 && y >= 0)
            return (float) Math.atan(y / x);
        else if (x < 0 && y >= 0)
            return (float) (Math.toRadians(180) - Math.atan(Math.abs(y / x)));
        else if (x < 0 && y < 0)
            return (float) (Math.toRadians(270) - Math.atan(Math.abs(x / y)));
        else
            return (float) (Math.toRadians(360) - Math.atan(Math.abs(y / x)));
    }

    public float dot(Vector2f other) {
        return x * other.x + y * other.y;
    }

    public Vector2f normal() {
        return copy().setDirection((float) (getDirection() + Math.toRadians(90)));
    }

    public Vector2f project(Vector2f onto) {
        return onto.copy().scale(onto.dot(this) / (onto.getMagnitude() * onto.getMagnitude()));
    }

    public void zero() {
        x = y = 0f;
    }

    public boolean isZero() {
        return x == 0f && y == 0f;
    }

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
