package model;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by Nathan on 8/30/2015.
 */
public class Sprite {
    public int width, height, offsetX, offsetY, rotationX, rotationY;
    public String name, next;
    public boolean interruptible;
    public float time;
    public BufferedImage image;

    public Sprite(String name) {
        this.name = name;
        interruptible = true;
    }

    public Sprite setImage(BufferedImage spriteSheet, int x, int y, int width, int height) {
        this.width = width;
        this.height = height;
        image = spriteSheet.getSubimage(x, y, width, height);
        return this;
    }

    /**
     * Convenience, debug only function allowing individual sprite images instead of a spritesheet
     *
     * @return
     */
    public Sprite setImage() {
        try {
            image = ImageIO.read(new File("res/" + name + ".png"));
            width = image.getWidth();
            height = image.getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return this;
    }

    public Sprite setRotationOrigin(int x, int y) {
        rotationX = x;
        rotationY = y;
        return this;
    }

    /**
     * Defines the offset between the BOTTOM LEFT CORNER of hitbox and sprite. (offsetX, offsetY) should be a vector
     * pointing from the hitbox to the sprite
     *
     * @param hitboxX
     * @param hitboxY
     * @return
     */
    public Sprite setOffset(int hitboxX, int hitboxY) {
        this.offsetX = hitboxX;
        this.offsetY = hitboxY;
        return this;
    }

    public Sprite setNext(String next) {
        this.next = next;
        return this;
    }

    public Sprite setTime(float time) {
        this.time = time;
        return this;
    }

    public Sprite setInterruptible(boolean b) {
        interruptible = b;
        return this;
    }
}
