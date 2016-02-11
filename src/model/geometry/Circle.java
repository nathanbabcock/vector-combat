package model.geometry;

import model.Game;
import model.entities.Entity;

/**
 * Created by Nathan on 8/25/2015.
 */
public class Circle extends Entity {
    public float radius;

    public Circle() {
    }

    public Circle(Game game, Point2f center, float radius) {
        this.game = game;
        this.position = center;
        this.radius = radius;
    }

    public Circle(Game game, float x, float y, float radius) {
        this(game, new Point2f(x, y), radius);
    }

    public Point2f getCenter() {
        return position;
    }

    public Point2f getBottomLeft() {
        return new Point2f(position.x - radius, position.y - radius);
    }
}
