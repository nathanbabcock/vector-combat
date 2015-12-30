package model.maps;

import model.geometry.AABB;
import model.geometry.Point2D;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Nathan on 8/20/2015.
 */
public abstract class Map {
    public BufferedImage background;
    public int width, height;
    //    public ArrayList<Line2D> boundaries;
    public ArrayList<AABB> statics;

    public ArrayList<Point2D> spawnpoints_red, spawnpoints_blue;
}
