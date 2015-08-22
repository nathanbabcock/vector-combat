package model;

import model.geometry.Point2D;
import model.geometry.Vector2D;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Player {
    public Float x, y;
    public Float width, height;
    public Vector2D velocity;
    public boolean movingLeft, movingRight;
    public static final Vector2D leftImpulse = new Vector2D(50, 180);
    public static final Vector2D rightImpulse = new Vector2D(50, 0);
    public static final Vector2D jumpImpulse = new Vector2D(150, 90);

    public Point2D getPos() {
        return new Point2D(x, y);
    }

    public void setPos(Point2D pos) {
        x = pos.x;
        y = pos.y;
    }

//    public Rectangle getHitBox() {
//        return new Rectangle(x.intValue(), y.intValue(), width.intValue(), height.intValue());
//    }
}
