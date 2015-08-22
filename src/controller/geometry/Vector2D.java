package controller.geometry;

/**
 * Created by Nathan on 8/20/2015.
 */
public class Vector2D {
    public float magnitude, direction;

    public Vector2D(float magnitude, float direction) {
        this.magnitude = magnitude;
        this.direction = direction;
    }

    public Vector2D scale(float scalar) {
        return new Vector2D(magnitude * scalar, direction);
    }

    public Vector2D add(Vector2D other) {
        float deltaX = deltaX() + other.deltaX();
        float deltaY = deltaY() + other.deltaY();

        float magnitude = (float) Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaY, 2));
        float direction = (float) Math.toDegrees(Math.atan(deltaY / deltaX));
        return new Vector2D(magnitude, direction);
    }

    public void zeroY() {
        float deltaX = deltaX();
        magnitude = deltaX;
        if (deltaX > 0)
            direction = 0;
        else if (deltaX < 0)
            direction = 180;
    }

    public float deltaX() {
        return (float) (magnitude * Math.cos(Math.toRadians(direction)));
    }

    public float deltaY() {
        return (float) (magnitude * Math.sin(Math.toRadians(direction)));
    }

    public void zero() {
        magnitude = 0;
    }
}
