package model.maps;

import model.geometry.Point2f;
import model.geometry.Polygon;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Nathan on 12/29/2015.
 */
public class Map2 extends Map {
    public Map2() {
/*        try {
            background = ImageIO.read(new File("res/simplemap.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        width = 4000;
        height = 2000;

        statics = new ArrayList<>();
        // Boundaries
        statics.add(new Polygon().makeAABB(0, 0, width, 64));
        statics.add(new Polygon().makeAABB(0, height - 64, width, 64));
        statics.add(new Polygon().makeAABB(0, 0, 64, height));
        statics.add(new Polygon().makeAABB(width - 64, 0, 64, height));

        // Ramp
        statics.add(new Polygon(null, new ArrayList<>(Arrays.asList(new Point2f[]{new Point2f(100, 64), new Point2f(300, 128), new Point2f(300, 64)}))));

        // Tunnel
        statics.add(new Polygon().makeAABB(width / 2 - 256, 164, 512, 64));

        // Center wall
        statics.add(new Polygon().makeAABB(width / 2 - 32, 164, 64, 500));

        // Top
        statics.add(new Polygon().makeAABB(width / 2 - 100, 500 + 164, 200, 32));

        // Mid left
        statics.add(new Polygon().makeAABB(width / 2 - 400 - 512, 350, 512, 64));

        // Mid right
        statics.add(new Polygon().makeAABB(width / 2 + 400, 350, 512, 64));

        spawnpoints_blue = new ArrayList();
        spawnpoints_blue.add(new Point2f(100, 200));

        spawnpoints_red = new ArrayList();
        spawnpoints_red.add(new Point2f(width - 100, 200));
    }
}
