package model;

import controller.geometry.Point2D;
import controller.geometry.Vector2D;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Game {
    public Player player;
    public Map map;
    Vector2D gravity = new Vector2D(100, -90); // acceleration in px/sec^2

    public Game() {
        map = new Map();

        // Spawn player
        player = new Player();
        player.x = 400f;
        player.y = 550f;
        player.width = 25f;
        player.height = 50f;
        player.velocity = new Vector2D(0, 0);
    }

    public void update(float deltaTime) {
        // Apply gravity
        // TODO something here
        player.velocity = player.velocity.add(gravity.scale(deltaTime));

        System.out.println(player.velocity.magnitude + " @ " + player.velocity.direction);

        // Move player
        movePlayer(deltaTime);
    }

    private void movePlayer(float deltaTime) {
        Point2D destination = player.getPos().translate(player.velocity.scale(deltaTime));
        Point2D prev = null;
        // Step towards destination, checking collisions at each step
        ArrayList<Point2D> line = besenham(player.getPos(), destination);
        for (Point2D p : line) {
            if (prev == null) {
                prev = p;
                continue;
            }
            int deltaX = (int) (p.x - prev.x);
            int deltaY = (int) (p.y - prev.y);
            prev = player.getPos();
            player.setPos(p);
            if ((deltaY < 0 && checkCollisions_bottom(player)) || (deltaY > 0 && checkCollisions_top(player))) {
                player.velocity.zeroY();
                player.setPos(prev);
                return;
            }
        }
    }

    private boolean checkCollisions_bottom(Player player) {
        int y = player.y.intValue();
        for (int x2 = player.x.intValue(); x2 <= player.x + player.width; x2++)
            if (Color.BLACK.equals(new Color(map.mask.getRGB(x2, (int) (map.HEIGHT - y - 1)))))
                return true;
        return false;
    }

    private boolean checkCollisions_top(Player player) {
        int y = (int) (player.y + player.height);
        for (int x2 = player.x.intValue(); x2 <= player.x + player.width; x2++)
            if (Color.BLACK.equals(new Color(map.mask.getRGB(x2, (int) (map.HEIGHT - y - 1)))))
                return true;
        return false;
    }

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
