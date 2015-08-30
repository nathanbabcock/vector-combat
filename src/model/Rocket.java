package model;

import model.geometry.Circle2D;
import model.geometry.Vector2D;

/**
 * Created by Nathan on 8/25/2015.
 */
public class Rocket extends Circle2D {
    public Vector2D velocity, acceleration;
    public Player owner;
    public boolean exploded;

    public static final float RADIUS = 10;
    public static final float VELOCITY = 500;
    public static final float EXPLOSION_RADIUS = 100;
    public static final int DAMAGE = 60;

    public Rocket(float x, float y, float radius) {
        super(x, y, radius);
        exploded = false;
    }

}
