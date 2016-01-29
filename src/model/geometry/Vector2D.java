package model.geometry;

import java.io.Serializable;

/**
 * Created by Nathan on 8/20/2015.
 */
public class Vector2D implements Serializable {
    public Float x, y;

    public Vector2D() {
    }

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

    @Override
    public String toString() {
        return "[" + x + ", " + y + "]";
    }
}
