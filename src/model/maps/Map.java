package model.maps;

import model.geometry.Point2f;
import model.geometry.Polygon;
import view.Canvas;

import java.awt.*;
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
    public ArrayList<Polygon> statics;
    public ArrayList<Point2f> spawnpoints_red, spawnpoints_blue;
    public Point2f redflag, blueflag;

    public void draw(Canvas canvas, Graphics2D g2) {
        g2.setColor(Color.black);
        Graphics2D g3 = (Graphics2D) g2.create();
        //g3.translate(cameraOffsetX, )
        g3.translate(canvas.cameraOffsetX, canvas.getHeight() - canvas.cameraOffsetY);
        g3.scale(1, -1);
        for (Polygon b : statics)
            b.draw(canvas, g3);
        //g3.fillPolygon(b.getAwtPoly());
    }
}
