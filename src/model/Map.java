package model;

import model.geometry.AABB;
import model.geometry.Line2D;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Nathan on 8/20/2015.
 */
public class Map {
    public BufferedImage background;
    public final int WIDTH = 800;
    public final int HEIGHT = 600;
    public ArrayList<Line2D> boundaries;
    public ArrayList<AABB> statics;

    public Map() {
        try {
            background = ImageIO.read(new File("res/simplemap.png"));

            statics = new ArrayList<>();
            statics.add(new AABB(0, 0, 800, 24));
            statics.add(new AABB(100, 24, 100, 40));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
