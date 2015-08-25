package model;

import model.geometry.AABB;
import model.geometry.Point2D;
import model.geometry.Vector2D;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Player {
    public Float x, y;
    public Float width, height;
    public Vector2D velocity, acceleration;
    public Float moveSpeed = 200f;
    public Float jumpSpeed = 100f;

    public Point2D getPos() {
        return new Point2D(x, y);
    }

    public void setPos(Point2D pos) {
        x = pos.x;
        y = pos.y;
    }

    public AABB getAABB() {
        return new AABB(x, y, x + width, y + height);
    }

//    public Rectangle getHitBox() {
//        return new Rectangle(x.intValue(), y.intValue(), width.intValue(), height.intValue());
//    }
}
