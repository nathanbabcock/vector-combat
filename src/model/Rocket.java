package model;

import model.geometry.Circle2D;
import model.geometry.Vector2D;

/**
 * Created by Nathan on 8/25/2015.
 */
public class Rocket extends Circle2D {
    public Vector2D velocity, acceleration;
    public Player owner;

    public static final float RADIUS = 10;
    public static final float VELOCITY = 100;
    public static final float EXPLOSION_RADIUS = 100;

    public Rocket(float x, float y, float radius) {
        super(x, y, radius);
    }

}
