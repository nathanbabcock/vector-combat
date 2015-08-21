package controller.geometry;

/**
 * Created by Nathan on 8/20/2015.
 */
public class Vector2D {
    float magnitude, direction;

    public Vector2D(float magnitude, float direction) {
        this.magnitude = magnitude;
        this.direction = direction;
    }

    public Vector2D scale(float scalar) {
        return new Vector2D(magnitude * scalar, direction);
    }
}
