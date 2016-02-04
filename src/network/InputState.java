package network;

import model.characters.Character;
import model.geometry.Point2D;

import java.io.Serializable;

/**
 * Created by Nathan on 9/12/2015.
 */
public class InputState implements Serializable {
    public boolean movingLeft, movingRight, movingUp, movingDown, attacking, altAttacking;
    public Point2D xhair;
    public int lastTick;

    public InputState() {
        xhair = new Point2D(0, 0);
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
}
