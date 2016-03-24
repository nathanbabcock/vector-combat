package network;

import characters.Character;
import geometry.Point2f;

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
