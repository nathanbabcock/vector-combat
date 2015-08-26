package model;

import model.geometry.AABB;
import model.geometry.Point2D;
import model.geometry.Vector2D;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Game {
    public Player player;
    public List<Player> players;
    public List<Object> entities;
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

        players = new ArrayList<>();
        players.add(player);

        entities = new ArrayList<>();
//        Rocket rocket = new Rocket(500, 549, 10);
//        rocket.acceleration = new Vector2D(0, gravity);
//        rocket.velocity = new Vector2D(0, 0);
//        entities.add(rocket);
    }

    public void update(float deltaTime) {
        // Debug
        time += deltaTime;
//        System.out.println("t = " + time + ", pos = (" + player.x + ", " + player.y + "), v = (" + player.velocity.x + ", " + player.velocity.y + "), a = (" + player.acceleration.x + ", " + player.acceleration.y + ")");

        // Dynamics
        movePlayers(deltaTime);
        moveEntities(deltaTime);
    }

    private void movePlayers(float deltaTime) {
        for (Player player : players) {
            // Apply gravity
            player.velocity = player.velocity.add(player.acceleration.scale(deltaTime));

            // Move player
            player.setPos(player.getPos().displace(player.acceleration, player.velocity, deltaTime));

            // Check collisions
            for (AABB box : map.boxes) {
                Collision collision = player.getAABB().collision(box);
                if (collision != null) {
                    if (Math.abs(collision.delta.x) > Math.abs(collision.delta.y)) {
                        player.x += collision.delta.x;
                        player.velocity.x = 0f;
                        player.acceleration.x = 0f;
                    } else {
                        player.y += collision.delta.y;
                        player.velocity.y = 0f;
                    }
                }
            }
        }
    }

    private void moveEntities(float deltaTime) {
        for (Object entity : entities) {
            if (entity instanceof Rocket) {
                Rocket rocket = (Rocket) entity;
                // Apply gravity
                rocket.velocity = rocket.velocity.add(rocket.acceleration.scale(deltaTime));

                // Move player
                rocket.center = (rocket.center.displace(rocket.acceleration, rocket.velocity, deltaTime));

                // Check collisions
                for (AABB box : map.boxes) {
                    Collision collision = box.collision(rocket);
                    if (collision != null) {
//                        entities.remove(rocket);
                        for (Player player : players) {
                            float distance = player.getPos().distance(collision.position);
                            if (distance <= Rocket.EXPLOSION_RADIUS) {
                                Vector2D explosion = new Vector2D(player.getAABB().center.x - collision.position.x, player.getAABB().center.y - collision.position.y);
//                                explosion = explosion.scale(10);
                                player.velocity = player.velocity.add(explosion);
                            }
                        }

//                        if (Math.abs(collision.delta.x) > Math.abs(collision.delta.y)) {
//                            rocket.center.x += collision.delta.x;
//                            rocket.velocity.x = 0f;
//                            rocket.acceleration.x = 0f;
//                        } else {
//                            rocket.center.y += collision.delta.y;
//                            rocket.velocity.y = 0f;
//                        }
                    }
                }
            }
        }
    }

    public void shoot(Point2D xhair) {
        Rocket rocket = new Rocket(player.getAABB().center.x, player.getAABB().center.y, Rocket.RADIUS);
        rocket.owner = player;
        Point2D origin = player.getAABB().center;
        rocket.velocity = new Vector2D(xhair.x - origin.x, xhair.y - origin.y);
        rocket.velocity.setMagnitude(Rocket.VELOCITY);
        rocket.acceleration = new Vector2D(0, 0);
        entities.add(rocket);
    }
}
