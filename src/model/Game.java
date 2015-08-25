package model;

import model.geometry.AABB;
import model.geometry.Point2D;
import model.geometry.Vector2D;

import java.util.ArrayList;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Game {
    public Player player;
    public Map map;

    public static final float gravity = -98;

    public float time = 0;

    public Game() {
        map = new Map();

        // Spawn player
        player = new Player();
        player.x = 400f;
        player.y = 549f;
        player.width = 25f;
        player.height = 50f;
        player.velocity = new Vector2D(0, 0);
        player.acceleration = new Vector2D(0, gravity);
    }

    public void update(float deltaTime) {
        // Debug
        time += deltaTime;
        System.out.println("t = " + time + ", pos = (" + player.x + ", " + player.y + "), v = (" + player.velocity.x + ", " + player.velocity.y + "), a = (" + player.acceleration.x + ", " + player.acceleration.y + ")");

        // Apply gravity
        player.velocity = player.velocity.add(player.acceleration.scale(deltaTime));

        // Move player
        movePlayer(deltaTime);
    }

    private void movePlayer(float deltaTime) {
        // Move player
        player.setPos(player.getPos().displace(player.acceleration, player.velocity, deltaTime));

        // Check collisions
        checkCollisions_SAT();
    }


    private void checkCollisions_SAT() {
        AABB hitbox = player.getAABB();
        for (AABB box : map.boxes) {
            float dx = box.center.x - hitbox.center.x;
            float px = (box.halfX + hitbox.halfX) - Math.abs(dx);
            if (px <= 0)
                continue;

            float dy = box.center.y - hitbox.center.y;
            float py = (box.halfY + hitbox.halfY) - Math.abs(dy);
            if (py <= 0)
                continue;

            if (px < py) {
                float sx = -Math.signum(dx);
                player.x += px * sx;
            } else {
                float sy = -Math.signum(dy);
                player.y += py * sy;
            }
        }
    }

/*    private void checkCollisions_raster(Point2D oldPos) {
//        Vector2D displacement = new Vector2D(player.x - oldPos.x, player.y - oldPos.y);
//        float deltaX = player.x - oldPos.x;
//        float deltaY = player.y - oldPos.y;


        // TODO optimize by not checking every single pixel

        // Resolve collision
        ArrayList<Point2D> path = besenham(player.getPos(), oldPos);
        for (int i = 0; i <= path.size(); i++) {
            boolean collision = false;
            // First check if there is currently a collision
            int px = (int) (player.x + 1);
            int py = (int) (player.y + 1);
            outer:
            for (int x = px; x <= px + player.width; x++) {
                for (int y = py; y <= py + player.height; y++) {
                    if (Color.BLACK.equals(new Color(map.mask.getRGB(x, map.HEIGHT - y)))) {
                        System.out.println("Collision occurred at (" + x + ", " + y + ")");
                        collision = true;
                        break outer;
                    }
                }
            }

            if (!collision)
                return;

            // This triggers basically any time you are directly against a wall
            if (i == path.size()) {
                player.x = (float) Math.floor(oldPos.x);
                player.y = (float) Math.floor(oldPos.y);
                return;
            }

            // For now just step along the besenham and re-check until collision in resolved
            player.setPos(path.get(i));
            player.acceleration.y = 0f;
            player.velocity.y = 0f;
            player.velocity.x = 0f;
        }
    }

    private void checkCollisions_vector(Rect2D oldRect, Vector2D displacement) {
        // Detect edge collisions
        Point2D intersection = null;
        Line2D boundary = null;
        Line2D edge = null;
        outer:
        for (Line2D b : map.boundaries) {
            for (Line2D e : player.getEdges()) {
                intersection = b.intersection(e);
                if (intersection != null) {
                    boundary = b;
                    edge = e;
                    break outer;
                }
            }

        }

        if (intersection == null) {
            System.out.println("No collisions");
            return;
        } else {
            System.out.println("Collision");
        }

        // Pick closest vertex
        // TODO generalize to any direction/edge
        Point2D closestVertex = null;
        if (displacement.x < 0 || displacement.y < 0) {
            if (boundary.slope() <= edge.slope())
                closestVertex = oldRect.bottomLeft(); //originalSide.a;
            else// if (boundary.slope() > edge.slope())
                closestVertex = oldRect.topLeft(); //originalSide.b;
        } else {//if (displacement.x > 0 || displacement.y > 0) {
            if (boundary.slope() >= edge.slope())
                closestVertex = oldRect.bottomRight(); //originalSide.a;
            else// if (boundary.slope() > edge.slope())
                closestVertex = oldRect.topRight(); //originalSide.b;
        }

//            float slope = displacement.y / displacement.x;
        Line2D movedDisplacement = new Line2D(closestVertex, closestVertex.translate(displacement));
        Point2D newIntersection = movedDisplacement.intersection(boundary);
        Line2D newDisplacement = new Line2D(closestVertex, newIntersection);
        if (newIntersection == null) {
            for (Line2D e : oldRect.getEdges()) {
                newIntersection = new Line2D(boundary.b, boundary.b.translate(displacement.negate())).intersection(e);
                if (newIntersection != null) {
                    newDisplacement = new Line2D(boundary.b, newIntersection);
                    break;
                }
                newIntersection = new Line2D(boundary.a, boundary.a.translate(displacement.negate())).intersection(e);
                if (newIntersection != null) {
                    newDisplacement = new Line2D(boundary.a, newIntersection);
                    break;
                }
            }
        }


        // Move player out of collision
        player.setPos(oldRect.bottomLeft().translate(newDisplacement.toVector()));
        player.acceleration.y = 0f;
        player.velocity.y = 0f;
//        player.y++;
    }*/

    /**
     * Basenham's line drawing algorithm. Rasterizes a mathematical representation of a line
     */
    public static ArrayList<Point2D> besenham(Point2D a, Point2D b) {
        ArrayList<Point2D> line = new ArrayList();
//        int x0 = a.x.intValue();
//        int y0 = a.y.intValue();
//        int x1 = b.x.intValue();
//        int y1 = b.y.intValue();
        int x0 = (int) Math.floor(a.x);
        int y0 = (int) Math.floor(a.y);
        int x1 = (int) Math.ceil(b.x);
        int y1 = (int) Math.ceil(b.y); //(b.y + 1); // This line solves a bug for some reason

        int dx = Math.abs(x1 - x0), sx = x0 < x1 ? 1 : -1;
        int dy = -Math.abs(y1 - y0), sy = y0 < y1 ? 1 : -1;
        int err = dx + dy, e2; /* error value e_xy */

        while (x0 != x1 || y0 != y1) {  /* loop */
            line.add(new Point2D(x0, y0));
            e2 = 2 * err;
            if (e2 >= dy) {
                err += dy;
                x0 += sx;
            } /* e_xy+e_x > 0 */
            if (e2 <= dx) {
                err += dx;
                y0 += sy;
            } /* e_xy+e_y < 0 */
        }

        return line;
    }
}
