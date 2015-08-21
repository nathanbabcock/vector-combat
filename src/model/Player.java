package model;

import controller.geometry.Point2D;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Player {
    public Point2D position;
    public Float width, height, velocity, acceleration;
    public Float jumpHeight, moveSpeed;

//    public Rectangle getHitBox() {
//        return new Rectangle(x.intValue(), y.intValue(), width.intValue(), height.intValue());
//    }
}
