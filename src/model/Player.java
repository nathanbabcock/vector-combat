package model;

import model.geometry.AABB;
import model.geometry.Point2D;
import model.geometry.Vector2D;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Player extends AABB {
    public Vector2D velocity, acceleration;
    public Float moveSpeed = 200f;
    public Float jumpSpeed = 100f;

    public boolean walkingLeft;
    public boolean walkingRight;

    public Sprite sprite;
    public float spriteTime;

    public int health;

    public Player() {
        velocity = new Vector2D(0, 0);
        acceleration = new Vector2D(0, 0);
        position = new Point2D(0, 0);
        width = 24;
        height = 80;
        health = 200;
    }

    public void damage(int damage) {
        health -= damage;
    }
}
