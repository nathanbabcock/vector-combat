package model.geometry;

/**
 * Created by Nathan on 8/24/2015.
 * <p>
 * Represents an axis-aligned bounding box for collision detection. AABB is defined by a center point and 2 half-width vectors
 */
public class AABB {
    public Point2D center;
    public float halfX, halfY;

    public AABB(Point2D center, float halfX, float halfY) {
        this.center = center;
        this.halfX = halfX;
        this.halfY = halfY;
    }

    public AABB(float x0, float y0, float x1, float y1) {
        halfX = Math.abs(x1 - x0) / 2;
        halfY = Math.abs(y1 - y0) / 2;
        center = new Point2D(x0 + halfX, y0 + halfY);
    }


}
