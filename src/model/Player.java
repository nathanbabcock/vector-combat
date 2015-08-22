package model;

import controller.geometry.Point2D;
import controller.geometry.Vector2D;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Player {
    public Float x, y;
    public Float width, height;
    public Float jumpHeight, moveSpeed;
    public Vector2D velocity;

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
