package model;

import model.geometry.AABB;
import model.geometry.Vector2D;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Game {
    public Player player;
    public Map map;

    public static final float gravity = -98;

    public float time = 0;

    public Game() {
        map = new Map();

        // Spawn player
        player = new Player();
        player.x = 400f;
        player.y = 549f;
        player.width = 25f;
        player.height = 50f;
        player.velocity = new Vector2D(0, 0);
        player.acceleration = new Vector2D(0, gravity);
    }

    public void update(float deltaTime) {
        // Debug
        time += deltaTime;
        System.out.println("t = " + time + ", pos = (" + player.x + ", " + player.y + "), v = (" + player.velocity.x + ", " + player.velocity.y + "), a = (" + player.acceleration.x + ", " + player.acceleration.y + ")");

        // Apply gravity
        player.velocity = player.velocity.add(player.acceleration.scale(deltaTime));

        // Move player
        movePlayer(deltaTime);
    }

    private void movePlayer(float deltaTime) {
        // Move player
        player.setPos(player.getPos().displace(player.acceleration, player.velocity, deltaTime));

        // Check collisions
        checkCollisions_SAT();
    }


    private void checkCollisions_SAT() {
        AABB hitbox = player.getAABB();
        for (AABB box : map.boxes) {
            float dx = hitbox.center.x - box.center.x;
            float px = (box.halfX + hitbox.halfX) - Math.abs(dx);
            if (px <= 0)
                continue;

            float dy = hitbox.center.y - box.center.y;
            float py = (box.halfY + hitbox.halfY) - Math.abs(dy);
            if (py <= 0)
                continue;

            if (px < py) {
                float sx = Math.signum(dx);
                player.x += px * sx;
                player.velocity.x = 0f;
                player.acceleration.x = 0f;
            } else {
                float sy = Math.signum(dy);
                player.y += py * sy;
                player.velocity.y = 0f;
//                if(sy < 0)
//                    player.acceleration.y = 0f;
            }
        }
    }
}
