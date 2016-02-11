package model.maps;

import model.geometry.AABB;
import model.geometry.Point2f;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Created by Nathan on 8/20/2015.
 *
 * TODO make this an interface if it doesn't have any methods
 */
public abstract class Map {
    public BufferedImage background;
    public int width, height;
    //    public ArrayList<Line2D> boundaries;
    public ArrayList<AABB> statics;
    public ArrayList<Point2f> spawnpoints_red, spawnpoints_blue;
}
