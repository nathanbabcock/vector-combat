package ai;

import geometry.Point2f;
import geometry.Vector2f;
import network.InputState;

/**
 * Created by Nathan on 4/12/2016.
 */
public class CharState {
    public InputState inputState;
    public Vector2f velocity;
    public Point2f position;

    @Override
    public String toString() {
        String result = String.format("%6s %6s %6s %6s %6s %6s %6s %6s %6s %6s %6s %6s%n", "LEFT", "RIGHT", "UP", "DOWN", "ATK1", "ATK2", "X", "Y", "VX", "VY", "XX", "XY");
        result += String.format("%6s %6s %6s %6s %6s %6s %6.0f %6.0f %6.0f %6.0f %6.0f %6.0f%n",
                inputState.movingLeft,
                inputState.movingRight,
                inputState.movingUp,
                inputState.movingDown,
                inputState.attacking,
                inputState.altAttacking,
                position.x,
                position.y,
                velocity.x,
                velocity.y,
                inputState.xhair.x,
                inputState.xhair.y);
        return result;
    }
}
