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
        return new Vector2D(x + other.x, y + other.y);
    }

    public Vector2D scale(float scalar) {
        return new Vector2D(x * scalar, y * scalar);
    }

    public float magnitude() {
        return (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

    public Vector2D negate() {
        return scale(-1);
    }

    public void setMagnitude(Float magnitude){

    }
}
