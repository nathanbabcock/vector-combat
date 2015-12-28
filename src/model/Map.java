package model;

import model.geometry.AABB;
import model.geometry.Point2D;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Nathan on 8/20/2015.
 */
public class Map {
    public BufferedImage background;
    public final int WIDTH = 2000;
    public final int HEIGHT = 2000;
    //    public ArrayList<Line2D> boundaries;
    public ArrayList<AABB> statics;

    public ArrayList<Point2D> spawnpoints_red, spawnpoints_blue;

    public Map() {
/*        try {
            background = ImageIO.read(new File("res/simplemap.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        statics = new ArrayList<>();
        statics.add(new AABB(0, 0, 2000, 64));
        statics.add(new AABB(0, 128, 400, 40));
        statics.add(new AABB(1600, 128, 400, 40));
        statics.add(new AABB(600, 256, 400, 40));

        statics.add(new AABB(0, 0, 40, 1000));
        statics.add(new AABB(600, 400, 40, 1000));
        statics.add(new AABB(200, 400, 40, 1000));

        spawnpoints_red = new ArrayList();
        spawnpoints_red.add(new Point2D(400, 850));

        spawnpoints_blue = new ArrayList();
        spawnpoints_blue.add(new Point2D(400, 850));
    }
}
