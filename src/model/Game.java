package model;

import model.geometry.Line2D;
import model.geometry.Point2D;
import model.geometry.Vector2D;

import java.awt.*;
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
        player.y = 550f;
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
        Line2D translation = new Line2D(player.getPos(), player.getPos().displace(player.acceleration, player.velocity, deltaTime));
        for (Line2D boundary : map.boundaries) {
            Point2D intersection = boundary.intersection(translation);
            if (intersection != null && !intersection.equals(player.getPos())) {
                player.setPos(intersection);
                player.velocity.y = 0f;
                player.acceleration.y = 0f;
                return;
            }
        }
        player.setPos(translation.b);
    }

    private void movePlayer_old(float deltaTime) {
//        Vector2D displacement = player.velocity.scale(deltaTime);
//        Point2D destination = player.getPos().translate(displacement);
        Point2D destination = player.getPos().displace(player.acceleration, player.velocity, deltaTime);
        float deltaX = destination.x - player.x;
        float deltaY = destination.y - player.y;

        Point2D oldPos = player.getPos();
        player.setPos(destination);

        // Step towards destination, checking collisions at each step
        ArrayList<Point2D> line = besenham(oldPos, destination);
        Point2D prev = null;
        for (Point2D p : line) {
            if (prev == null) {
                prev = p;
                continue;
            }

            // Vertical collision detection

            if (checkCollisions_bottom(p)) {
                player.velocity.y = 0f;
                player.acceleration.y = 0f;
                player.setPos(prev);
                player.y = (float) Math.ceil(player.y);
//                palyer.y
                return;
            } else {
                player.acceleration.y = gravity;
            }

/*            // Horizontal collision detection
            if ((deltaX < 0 && checkCollisions_left(player)) || (deltaX > 0 && checkCollisions_right(player))) {
                player.velocity.x = 0f;
                player.setPos(prev);
                return;
            }*/

            prev = p;
        }


    }

    // TODO refactor collision detection to avoid duplicate code
    private boolean checkCollisions_bottom(Point2D position) {
        Float y = position.y;
        for (Float x2 = position.x; x2 <= position.x + player.width; x2++)
            if (Color.BLACK.equals(new Color(map.mask.getRGB(Math.round(x2), Math.round(map.HEIGHT - y - 1)))))
                return true;
        return false;
    }
/*
    private boolean checkCollisions_top(Player player) {
        int y = (int) (player.y + player.height);
        for (int x2 = player.x.intValue(); x2 <= player.x + player.width; x2++)
            if (Color.BLACK.equals(new Color(map.mask.getRGB(x2, (int) (map.HEIGHT - y + 1)))))
                return true;
        return false;
    }

    private boolean checkCollisions_left(Player player) {
        int x = player.x.intValue();
        for (int y2 = player.y.intValue(); y2 <= player.y + player.height; y2++)
            if (Color.BLACK.equals(new Color(map.mask.getRGB(x - 1, map.HEIGHT - y2))))
                return true;
        return false;
    }

    private boolean checkCollisions_right(Player player) {
        int x = (int) (player.x + player.width);
        for (int y2 = player.y.intValue(); y2 <= player.y + player.height; y2++)
            if (Color.BLACK.equals(new Color(map.mask.getRGB(x + 1, map.HEIGHT - y2))))
                return true;
        return false;
    }*/

    /**
     * Basenham's line drawing algorithm. Rasterizes a mathematical representation of a line
     */
    public static ArrayList<Point2D> besenham(Point2D a, Point2D b) {
        ArrayList<Point2D> line = new ArrayList();
        int x0 = a.x.intValue();
        int y0 = a.y.intValue();
        int x1 = b.x.intValue();
        int y1 = b.y.intValue();

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
