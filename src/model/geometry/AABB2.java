package model.geometry;

/**
 * Created by Nathan on 3/15/2016.
 */
public class AABB2 extends Polygon {
//    private Point2f position;
//    private int width, height;


    public boolean contains(Point2f p) {
        if (p.x < getBottomLeft().x || p.x > getBottomLeft().x + getWidth())
            return false;
        else if (p.y < getBottomLeft().y || p.y > getBottomLeft().y + getHeight())
            return false;
        return true;
    }

    public float getWidth() {
        return vertices.get(1).x - vertices.get(0).x;
    }

    public float getHeight() {
        return vertices.get(3).x - vertices.get(0).x;
    }

    public Point2f getCenter() {
        return vertices.get(0).copy().translate(getWidth() / 2, getHeight() / 2);
    }

    public Point2f getBottomLeft() {
        return vertices.get(0);
    }

    public Point2f getPosition() {
        return getBottomLeft();
    }

    public AABB2 setPosition(Point2f pos) {
        Vector2f delta = new Vector2f(getPosition(), pos);
        for (Point2f v : vertices)
            v.translate(delta);
        return this;
    }

//    public AABB2 setWidth() {
//        return this;
//    }
//
//    public AABB2 setHeight() {
//        return this;
//    }

}
