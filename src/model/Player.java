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
    public static final Vector2D moveLeft = new Vector2D(256, 180);
    public static final Vector2D moveRight = new Vector2D(256, 0);

    public Point2D getPos() {
        return new Point2D(x, y);
    }

    public void setPos(Point2D pos) {
        x = pos.x;
        y = pos.y;
    }

    public void moveLeft() {
        velocity = moveLeft; //velocity.add(moveLeft);
    }

    public void moveRight() {
        velocity = moveRight; //velocity.add(moveRight);

    }

//    public Rectangle getHitBox() {
//        return new Rectangle(x.intValue(), y.intValue(), width.intValue(), height.intValue());
//    }
}
