package model.geometry;

import java.util.ArrayList;

/**
 * Created by Nathan on 8/23/2015.
 */
public class Rect2D {
    public Float x, y, width, height;

    public ArrayList<Line2D> getEdges() {
        ArrayList<Line2D> list = new ArrayList<>();
        list.add(left());
        list.add(top());
        list.add(right());
        list.add(bottom());
        return list;
    }

    public Rect2D getRect() { // GET REKD M8
        Rect2D clone = new Rect2D();
        clone.x = x;
        clone.y = y;
        clone.width = width;
        clone.height = height;
        return clone;
    }

    public Line2D left() {
        return new Line2D(bottomLeft(), topLeft());
    }

    public Line2D top() {
        return new Line2D(topLeft(), topRight());
    }

    public Line2D right() {
        return new Line2D(topRight(), bottomRight());
    }

    public Line2D bottom() {
        return new Line2D(bottomRight(), bottomLeft());
    }


    public Point2D bottomLeft() {
        return new Point2D(x, y);
    }

    public Point2D topLeft() {
        return new Point2D(x, y + height);
    }

    public Point2D topRight() {
        return new Point2D(x + width, y + height);
    }

    public Point2D bottomRight() {
        return new Point2D(x + width, y);
    }

    public Point2D center() {
        return new Point2D(x + width / 2, y + height / 2);
    }
}
