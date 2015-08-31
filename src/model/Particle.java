package model;

import model.geometry.Point2D;
import model.geometry.Vector2D;

import java.awt.*;

/**
 * Created by Nathan on 8/30/2015.
 */
public class Particle {
    public Point2D position;
    public Vector2D velocity, acceleration;
    public float size, angle, age, maxAge;
    public float growth, rotation; // change in size and angle over time (per second)
    public Color color;

    public Particle() {
        maxAge = 1f;
    }

    public void update(float deltaTime) {
        size += deltaTime * growth;
        angle += deltaTime * rotation;
    }
}
