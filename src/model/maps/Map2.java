package model.maps;

import model.geometry.Point2f;
import model.geometry.Polygon;

import java.util.ArrayList;

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



        // Tunnel
        statics.add(new Polygon().makeAABB(2000 - 256, 164, 512, 64));

        // Center wall
        statics.add(new Polygon().makeAABB(2000 - 32, 164, 64, 500));

        // Top
        statics.add(new Polygon().makeAABB(2000 - 100, 500 + 164, 200, 32));

        // Mid left
        statics.add(new Polygon().makeAABB(2000 - 400 - 512, 350, 512, 64));

        // Mid right
        statics.add(new Polygon().makeAABB(2000 + 400, 350, 512, 64));

        spawnpoints_blue = new ArrayList();
        spawnpoints_blue.add(new Point2f(100, 200));

        spawnpoints_red = new ArrayList();
        spawnpoints_red.add(new Point2f(width - 100, 200));
    }
}
