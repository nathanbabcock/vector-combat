package model.geometry;

import java.io.Serializable;

/**
 * Created by Nathan on 9/19/2015.
 */
abstract public class Shape2D implements Serializable {
    public Point2D position;

    abstract public Point2D getCenter();

    abstract public Point2D getBottomLeft();
}
