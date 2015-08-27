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
        // TODO implement
        float ratio = magnitude / getMagnitude();
        return scale(ratio);
    }
}
