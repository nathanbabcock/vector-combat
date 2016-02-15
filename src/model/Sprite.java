package model;

import java.awt.image.BufferedImage;

/**
 * Created by Nathan on 8/30/2015.
 */
public class Sprite {
    public int width, height, hitboxX, hitboxY, hitboxWidth, hitboxHeight;
    public String name, next;
    public boolean interruptible;
    public float time;
    public BufferedImage image;

    public Sprite(String name) {
        this.name = name;
        interruptible = true;
    }

    public Sprite setImage(BufferedImage spriteSheet, int x, int y, int width, int height) {
        this.width = hitboxWidth = width;
        this.height = hitboxHeight = height;
        image = spriteSheet.getSubimage(x, y, width, height);
        return this;
    }

    public Sprite setHitboxOffset(int hitboxX, int hitboxY) {
        this.hitboxX = hitboxX;
        this.hitboxY = hitboxY;
        return this;
    }

    public Sprite setHitboxSize(int hitboxWidth, int hitboxHeight) {
        this.hitboxWidth = hitboxWidth;
        this.hitboxHeight = hitboxHeight;
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
