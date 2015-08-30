package model;

import java.awt.image.BufferedImage;

/**
 * Created by Nathan on 8/30/2015.
 */
public class Sprite {
    public int width, height, offsetX, offsetY;
    public BufferedImage image;

    public Sprite(BufferedImage spriteSheet, int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        offsetX = 0;
        offsetY = 0;
        image = spriteSheet.getSubimage(x, y, width, height);
    }
}
