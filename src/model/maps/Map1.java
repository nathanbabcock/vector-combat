package model.maps;

import model.geometry.Point2f;
import model.geometry.Polygon;

import java.util.ArrayList;

/**
 * Created by Nathan on 12/29/2015.
 */
public class Map1 extends Map {
    public Map1() {
/*        try {
            background = ImageIO.read(new File("res/simplemap.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        width = height = 2000;

        statics = new ArrayList<>();
        statics.add(new Polygon().makeAABB(0, 0, 2000, 64));
        statics.add(new Polygon().makeAABB(0, 128, 400, 40));
        statics.add(new Polygon().makeAABB(1600, 128, 400, 40));
        statics.add(new Polygon().makeAABB(600, 256, 400, 40));

        statics.add(new Polygon().makeAABB(0, 0, 40, 1000));
        statics.add(new Polygon().makeAABB(600, 400, 40, 1000));
        statics.add(new Polygon().makeAABB(200, 400, 40, 1000));

        spawnpoints_red = new ArrayList();
        spawnpoints_red.add(new Point2f(400, 850));

        spawnpoints_blue = new ArrayList();
        spawnpoints_blue.add(new Point2f(400, 850));
    }
}
