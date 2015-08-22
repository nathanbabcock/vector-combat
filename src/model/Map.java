package model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by Nathan on 8/20/2015.
 */
public class Map {
    public BufferedImage background, mask;
    public final int WIDTH = 800;
    public final int HEIGHT = 600;

    public Map() {
        try {
            mask = background = ImageIO.read(new File("res/map.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
