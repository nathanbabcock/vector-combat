package model;

import model.geometry.Line2D;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by Nathan on 8/20/2015.
 */
public class Map {
    public BufferedImage background, mask;
    public final int WIDTH = 800;
    public final int HEIGHT = 600;
    public ArrayList<Line2D> boundaries;

    public Map() {
        try {
            mask = background = ImageIO.read(new File("res/simplemap.png"));
            boundaries = new ArrayList<>();
            boundaries.add(new Line2D(0, 24, 800, 24));
            boundaries.add(new Line2D(400, 24, 400, 64));
            boundaries.add(new Line2D(300, 64, 400, 64));
            boundaries.add(new Line2D(200, 24, 300, 64));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
