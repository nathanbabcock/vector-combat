package model.geometry;

import model.Collision;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 3/15/2016.
 */
public class Polygon {
    public List<Point2f> vertices;

    Polygon(List<Point2f> vertices) {
        this.vertices = vertices;
    }

    public Polygon() {
    }

    List<Vector2f> getSides() {
        List<Vector2f> sides = new ArrayList<>();
        for (int i = 0; i < vertices.size() - 1; i++)
            sides.add(new Vector2f(vertices.get(i), vertices.get(i + 1)));
        sides.add(new Vector2f(vertices.get(vertices.size() - 1), vertices.get(0)));
        return sides;
    }

    // TODO will list duplicate normals for parallel sides
    List<Vector2f> getNormals() {
        List<Vector2f> normals = new ArrayList<>();
        for (Vector2f v : getSides())
            normals.add(v.normal().normalize());
        return normals;
    }

    public Point2f getPosition() {
        return vertices.get(0);
    }

    public Polygon setPosition(Point2f pos) {
        Vector2f delta = new Vector2f(getPosition(), pos);
        for (Point2f v : vertices)
            v.translate(delta);
        return this;
    }

    public static Polygon makeAABB(Point2f position, float width, float height) {
        List<Point2f> vertices = new ArrayList<>();
        vertices.add(position);
        vertices.add(position.copy().translate(width, 0));
        vertices.add(position.copy().translate(width, height));
        vertices.add(position.copy().translate(0, height));
        return new Polygon(vertices);
    }

    public static Polygon makeAABB(float x, float y, float width, float height) {
        return makeAABB(new Point2f(x, y), width, height);
    }

    public static Polygon makeAABB(int x, int y, int width, int height) {
        return makeAABB((float) x, (float) y, (float) width, (float) height);
    }

    Collision collision(Polygon other) {
        float overlap = Float.MAX_VALUE;
        Vector2f smallest = null;
        // Project onto axes
        for (Vector2f axis : getNormals()) {
            Projection p1 = project(axis);
            Projection p2 = other.project(axis);
            if (!p1.overlaps(p2))
                return null;
            else {
                // get the overlap
                float o = p1.getOverlap(p2);
                // check for minimum
                if (o < overlap) {
                    // then set this one as the smallest
                    overlap = o;
                    smallest = axis;
                }
            }
        }

        Collision collision = new Collision();
        collision.delta = smallest.setMagnitude(overlap);
        collision.normal = smallest.normalize();
        return collision;
    }

    Projection project(Vector2f axis) {
        float min = Float.MAX_VALUE;
        float max = Float.MIN_VALUE;
        for (Point2f v : vertices) {
            float p = axis.dot(v);
            min = Math.min(p, min);
            max = Math.max(p, max);
        }
        return new Projection(min, max);
    }

    java.awt.Polygon getAwtPoly() {
        java.awt.Polygon polygon = new java.awt.Polygon();
        for (Point2f v : vertices)
            polygon.addPoint((int) v.x, (int) v.y);
        return polygon;
    }

    class Projection {
        float min, max;

        public Projection(float min, float max) {
            this.min = min;
            this.max = max;
        }

        public boolean overlaps(Projection other) {
            return (min < other.max && min >= other.min) || (max <= other.max && max > other.min);
        }

        /**
         * Assumes there is an overlap, and returns the magnitude of it
         *
         * @param other
         * @return
         */
        public float getOverlap(Projection other) {
            return Math.min(other.max, max) - Math.max(other.min, min);
        }
    }

    public static void main(String[] args) {
        ArrayList<Point2f> vertices = new ArrayList<>();
        vertices.add(new Point2f(0, 0));
        vertices.add(new Point2f(1, 0));
        vertices.add(new Point2f(0, 1));
        Polygon poly = new Polygon(vertices);

        ArrayList<Point2f> vertices2 = new ArrayList<>();
        vertices2.add(new Point2f(2f, 0));
        vertices2.add(new Point2f(3f, 0));
        vertices2.add(new Point2f(2f, 1));
        Polygon poly2 = new Polygon(vertices);

        Collision c = poly.collision(poly2);

        if (c == null)
            System.out.println(c);
        else
            System.out.println(c.delta);

//        for(Vector2f v: poly.getNormals()){
//            System.out.println(v);
//        }
    }

}
