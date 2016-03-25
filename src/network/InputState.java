package network;

import characters.Character;
import geometry.Point2f;

import java.io.FileWriter;

/**
 * Created by Nathan on 9/12/2015.
 */
public class InputState {
    public boolean movingLeft, movingRight, movingUp, movingDown, attacking, altAttacking;
    public Point2f xhair;
    public int lastTick;

    public InputState() {
        xhair = new Point2f(0, 0);
    }

    public InputState copy() {
        InputState copy = new InputState();
        copy.movingLeft = movingLeft;
        copy.movingRight = movingRight;
        copy.movingUp = movingUp;
        copy.movingDown = movingDown;
        copy.attacking = attacking;
        copy.altAttacking = altAttacking;
        copy.xhair = xhair;
        return copy;
    }

    public InputState(Character player) {
        movingLeft = player.movingLeft;
        movingRight = player.movingRight;
        movingUp = player.movingUp;
        movingDown = player.movingDown;
        attacking = player.attacking;
        altAttacking = player.altAttacking;
        xhair = player.xhair;
    }

    public void write(FileWriter writer) {

    }
}
