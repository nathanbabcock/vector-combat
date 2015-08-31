package model;

import model.geometry.AABB;
import model.geometry.Point2D;
import model.geometry.Vector2D;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.Vector;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Game {
    public Player player;
    public List<Player> players;
    public List<Object> entities, garbage;
    public List<Particle> particles;
    public HashMap<String, Sprite> sprites;
    public Map map;

    public static final float gravity = -250;

    public float time = 0;

    public Game() {
        map = new Map();

        setupSprites();

        // Spawn player
        player = new Player();
        player.position = new Point2D(400, 549);
        player.acceleration.y = gravity;

        players = new Vector<>();
        players.add(player);
        players.add(new Player());
        players.get(1).position = new Point2D(400, 850);

        entities = new Vector<>();
        garbage = new Vector<>();
        particles = new Vector<>();
    }

    private void setupSprites() {
        BufferedImage spriteSheet = null;
        try {
            spriteSheet = ImageIO.read(new File("res/spritesheet.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        sprites = new HashMap();
        sprites.put("rocket_standing", new Sprite(spriteSheet, 0, 0, 24, 80));
        sprites.put("rocket_walking", new Sprite(spriteSheet, 32, 0, 24, 80));
        sprites.put("rocket_launcher", new Sprite(spriteSheet, 64, 0, 64, 24));
    }

    public void update(float deltaTime) {
        // Debug
        time += deltaTime;
//        System.out.println("t = " + time + ", pos = " + player.position + ", v = (" + player.velocity.x + ", " + player.velocity.y + "), a = (" + player.acceleration.x + ", " + player.acceleration.y + ")");

        // Dynamics
        movePlayers(deltaTime);
        moveEntities(deltaTime);
        updateSprites(deltaTime);
        updateParticles(deltaTime);

        checkHealth();
        takeOutGarbage();
    }

    private void movePlayers(float deltaTime) {
        // Apply walking velocity
//        if(player.walkingLeft && ! player.walkingRight)
//            player.velocity.x = -player.moveSpeed;
//        else if (player.walkingRight && ! player.walkingLeft)
//            player.velocity.x = player.moveSpeed;
//        else
//            player.vel

        for (Player player : players) {
            // Apply gravity
            player.velocity.add(player.acceleration.copy().scale(deltaTime));
            player.acceleration.y = gravity;

            // Move player
            player.position.displace(player.acceleration, player.velocity, deltaTime);

            // Check collisions
            for (AABB box : map.statics) {
                Collision collision = player.collision(box);
                if (collision != null) {
                    if (Math.abs(collision.delta.x) > Math.abs(collision.delta.y)) {
                        player.position.x += collision.delta.x;
                        player.velocity.x = 0f;
                        player.acceleration.x = 0f;
                    } else {
                        player.position.y += collision.delta.y;
                        player.velocity.y = 0f;
//                        player.velocity.x = 0f;

                        if (collision.delta.y > 0)
                            player.acceleration.y = 0f;
                    }
                }
            }
        }
    }

    private void moveEntities(float deltaTime) {
        for (Object entity : entities) {
            if (entity instanceof Rocket) {
                Rocket rocket = (Rocket) entity;

                if (rocket.position.x > map.WIDTH || rocket.position.y > map.HEIGHT) {
                    garbage.add(rocket);
                    continue;
                }

                // Move rocket
                rocket.position.displace(rocket.acceleration, rocket.velocity, deltaTime);

                // Check collisions
                Collision collision = null;
                for (AABB box : map.statics) { // Walls
                    collision = box.collision(rocket);
                    if (collision != null)
                        break;
                }
                if (collision == null) { // players
                    for (Player player : players) {
                        if (player == rocket.owner)
                            continue;
                        collision = player.collision(rocket);
                        if (collision != null)
                            break;
                    }
                }
                if (collision != null) {
                    garbage.add(rocket);
                    for (Player player : players) {
                        float distance = player.position.distance(rocket.position);
                        if (distance <= Rocket.EXPLOSION_RADIUS) {
                            // Knockback
                            Vector2D explosion = new Vector2D(player.getCenter().x - rocket.getCenter().x, player.getCenter().y - rocket.getCenter().y);
                            // TODO scale damage and knockback with distance
                            explosion.setMagnitude(300f);
                            player.velocity.add(explosion);
                            // Damage
                            if (rocket.owner != player)
                                player.damage(Rocket.DAMAGE);
                        }
                    }

                    // Particle effects
                    final int AVG_PARTICLES = 20;
                    final int AVG_SIZE = 10;
                    final int MAX_DEVIATION = 5;
                    final int AVG_VELOCITY = 200;

                    Random r = new Random();
                    for (int i = 0; i < AVG_PARTICLES; i++) {
                        Particle particle = new Particle();
                        particle.position = rocket.getCenter().copy();
                        int sign;
                        if (r.nextBoolean())
                            sign = -1;
                        else
                            sign = 1;
                        particle.size = AVG_SIZE + (r.nextInt(MAX_DEVIATION + 1) * sign);
                        particle.color = new Color(0, 0, 0);
                        particle.angle = (float) Math.toRadians(r.nextInt(360));
                        particle.growth = 0;// -15; // - (r.nextInt(5) + 10);
                        particle.rotation = (float) Math.toRadians(r.nextInt(361));
                        particle.velocity = new Vector2D(r.nextInt(AVG_VELOCITY * 2) - AVG_VELOCITY, r.nextInt(AVG_VELOCITY * 2) - AVG_VELOCITY);
                        particle.acceleration = new Vector2D(0, gravity);
                        particles.add(particle);
                    }
                }
            }
        }
    }

    private void updateSprites(float deltaTime) {
        for (Player player : players) {
            if (player.walkingLeft || player.walkingRight) {
                float spriteInterval = 0.25f;
                if (player.spriteTime >= spriteInterval) {
                    if (player.sprite == sprites.get("rocket_standing")) {
                        player.sprite = sprites.get("rocket_walking");
                    } else if (player.sprite == sprites.get("rocket_walking"))
                        player.sprite = sprites.get("rocket_standing");
                    player.spriteTime = 0;
                }
            } else {
                player.sprite = sprites.get("rocket_standing");
            }
            player.spriteTime += deltaTime;
        }
    }

    private void updateParticles(float deltaTime) {
        // Update existing particles
        for (Particle particle : particles) {
            if (particle.position.x > map.WIDTH || particle.position.y > map.HEIGHT) {
                garbage.add(particle);
                continue;
            }

            particle.update(deltaTime);
            if (particle.size <= 0)
                garbage.add(particle);
        }

        // Spawn new particles
        for (Object entity : entities) {
            if (entity instanceof Rocket) {
                Rocket rocket = (Rocket) entity;

                // Spawn particle trail
                final int AVG_PARTICLES = 50;
                final int AVG_SIZE = 15;
                final int MAX_DEVIATION = 5;

                float numParticles = AVG_PARTICLES * deltaTime;
                Random r = new Random();
                if (r.nextFloat() < numParticles) {
                    Particle particle = new Smoke();
                    particle.position = rocket.getCenter().copy();
                    int sign;
                    if (r.nextBoolean())
                        sign = -1;
                    else
                        sign = 1;
                    particle.size = AVG_SIZE + (r.nextInt(MAX_DEVIATION + 1) * sign);
                    particle.color = new Color(255, 255, 0);
                    particle.angle = (float) Math.toRadians(r.nextInt(360));
                    particle.growth = -15; // - (r.nextInt(5) + 10);
                    particle.rotation = (float) Math.toRadians(r.nextInt(361));
                    particles.add(particle);
                }
            }
        }
    }

    private void takeOutGarbage() {
        for (Object trash : garbage) {
            boolean b = entities.remove(trash) || players.remove(trash) || particles.remove(trash);
        }
    }

    private void checkHealth() {
        for (Player player : players) {
            if (player.health <= 0)
                garbage.add(player);
        }
    }

    public void shoot(Point2D xhair) {
        Rocket rocket = new Rocket(player.getCenter().x, player.getCenter().y, Rocket.RADIUS);
        rocket.owner = player;
        Point2D origin = player.getCenter();
        rocket.velocity = new Vector2D(xhair.x - origin.x, xhair.y - origin.y);
        rocket.velocity.setMagnitude(Rocket.VELOCITY);
        rocket.acceleration = new Vector2D(0, 0);
        entities.add(rocket);
    }
}
