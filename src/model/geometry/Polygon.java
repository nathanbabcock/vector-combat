package model.geometry;

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

    boolean collision(Polygon other) {
        // Project onto axes
        for (Vector2f axis : getNormals()) {
            Projection p1 = project(axis);
            Projection p2 = other.project(axis);
            if (!p1.overlaps(p2))
                return false;
        }
        return true;
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

    class Projection {
        float min, max;

        public Projection(float min, float max) {
            this.min = min;
            this.max = max;
        }

        public boolean overlaps(Projection other) {
            return (min < other.max && min >= other.min) || (max <= other.max && max > other.min);
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
        Polygon poly2 = new Polygon(vertices2);

        System.out.println(poly.collision(poly2));

//        for(Vector2f v: poly.getNormals()){
//            System.out.println(v);
//        }
    }

}
