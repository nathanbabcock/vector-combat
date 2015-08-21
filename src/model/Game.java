package model;

import controller.geometry.Point2D;
import controller.geometry.Vector2D;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Game {
    public Player player;
    Vector2D gravity = new Vector2D(9.8f, -90); // acceleration in px/sec^2

    public Game() {
        // Spawn player
        player = new Player();
        player.position = new Point2D(400, 500);
        player.width = 25f;
        player.height = 50f;
    }

    public void update(float delta) {
        player.position.translateTo(gravity.scale(delta));
    }
}
