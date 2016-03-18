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
        for (Polygon b : statics) {
            if (!(b instanceof JumpPad))
                drawPlatform(g3, b);
            else
                b.draw(canvas, g3);
        }
        //b.draw(canvas, g3);
        //g3.fillPolygon(b.getAwtPoly());
    }

    static BufferedImage getCompatibleImage(BufferedImage image, int transparency) {
        //Get current GraphicsConfiguration
        GraphicsConfiguration graphicsConfiguration
                = GraphicsEnvironment
                .getLocalGraphicsEnvironment()
                .getDefaultScreenDevice()
                .getDefaultConfiguration();

        //Create a Compatible BufferedImage
        BufferedImage output
                = graphicsConfiguration.createCompatibleImage(
                image.getWidth(null),
                image.getHeight(null),
                transparency);
        //Copy from original Image to new Compatible BufferedImage
        Graphics tempGraphics = output.getGraphics();
        tempGraphics.drawImage(image, 0, 0, null);
        tempGraphics.dispose();

        return output;
    }

    static void drawPlatform(Graphics2D g2, Polygon poly) {
        g2.setColor(new Color(200, 200, 200));
        g2.fillPolygon(poly.getAwtPoly());
    }
}
