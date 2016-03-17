package model.geometry;

import model.Collision;

/**
 * Created by Nathan on 8/20/2015.
 */
public class Point2f {
    public float x, y;

    public Point2f() {
    }

    public Point2f(int x, int y) {
        this.x = (float) x;
        this.y = (float) y;
    }

    public Point2f(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Point2f translate(float x, float y) {
        this.x += x;
        this.y += y;
        return this;
    }

    public Point2f translate(Vector2f v) {
        return translate(v.x, v.y);
    }

    public void rotate(float angle) {
        rotate(angle, new Point2f(0, 0));
    }

    public void rotate(float angle, Point2f origin) {
//        x = (float) (Math.cos(angle) * (x - origin.x) - Math.sin(angle) * (y - origin.y) + origin.x);
//        y = (float) (Math.sin(angle) * (x - origin.x) + Math.cos(angle) * (y - origin.y) + origin.y);

        x = (float) (origin.x + (x - origin.x) * Math.cos(angle) - (y - origin.y) * Math.sin(angle));
        y = (float) (origin.y + (x - origin.x) * Math.sin(angle) + (y - origin.y) * Math.cos(angle));

    }

    public Point2f displace(Vector2f acceleration, Vector2f velocity, float time) {
        float deltaX = (float) (0.5 * acceleration.x * Math.pow(time, 2) + velocity.x * time);
        float deltaY = (float) (0.5 * acceleration.y * Math.pow(time, 2) + velocity.y * time);
        x += deltaX;
        y += deltaY;
        return this;
    }

    public float distance(Point2f other) {
        return (float) Math.sqrt(Math.pow(other.x - x, 2) + Math.pow(other.y - y, 2));
    }

    public Point2f copy() {
        return new Point2f(x, y);
    }

    public boolean equals(Point2f other) {
        return x == other.x && y == other.y;
    }

    public float project(Vector2f axis) {
        return new Vector2f(x, y).project(axis).getMagnitude();
    }

    public Collision collision(Polygon polygon, Vector2f axis) {
        Projection p1 = polygon.project(axis);
        float p2 = axis.dot(new Vector2f(this));//project(axis);
        System.out.println("p1 = " + p1);
        System.out.println("p2 = " + p2);
        if (!p1.contains(p2))
            return null;

        float overlap = p1.getOverlap(p2);
        Collision collision = new Collision();
        collision.delta = axis.setMagnitude(overlap);
        return collision;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
