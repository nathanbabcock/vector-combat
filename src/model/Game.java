package model;

import model.entities.Entity;
import model.geometry.Point2D;
import model.particles.Particle;
import model.players.Player;
import model.players.Scout;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Game {
    public Player player;
    public List<Player> players;
    public List<Entity> entities;
    public List<Object> garbage;
    public List<Particle> particles;
    public HashMap<String, Sprite> sprites;
    public Map map;

    public static final float gravity = -400;

    public float time = 0;

    public Game() {
        map = new Map();

        setupSprites();

        // Spawn player
        player = new Scout(this);
        player.position = new Point2D(400, 549);
        player.acceleration.y = gravity;

        players = new Vector<>();
        players.add(player);
//        players.add(new Soldier(this));
//        players.get(1).position = new Point2D(400, 850);

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

        sprites.put("ninja_standing", new Sprite(spriteSheet, 0, 112, 40, 96, 0, 16));
        sprites.put("ninja_walking", new Sprite(spriteSheet, 48, 112, 40, 96, 0, 16));

        sprites.put("soldier_standing", new Sprite(spriteSheet, 0, 216, 32, 80, -8, 0));
        sprites.put("soldier_walking", new Sprite(spriteSheet, 40, 216, 32, 80, -8, 0));
        sprites.put("soldier_gun", new Sprite(spriteSheet, 80, 216, 48, 24));

        sprites.put("scout_standing", new Sprite(spriteSheet, 0, 304, 24, 80));
        sprites.put("scout_walking", new Sprite(spriteSheet, 32, 304, 24, 80));
        sprites.put("scout_walljump", new Sprite(spriteSheet, 64, 304, 40, 80, -12, 0));
        sprites.put("scout_gun", new Sprite(spriteSheet, 112, 304, 48, 24));
    }

    public void update(float deltaTime) {
        // Debug
        time += deltaTime;
//        System.out.println("t = " + time + ", pos = " + player.position + ", v = (" + player.velocity.x + ", " + player.velocity.y + "), a = (" + player.acceleration.x + ", " + player.acceleration.y + ")");

        // Players
        for (Player player : players) // Update players
            player.update(deltaTime);

        // Entities
        for (Entity entity : entities)
            entity.update(deltaTime);

        // Particles
        for (Particle particle : particles)
            particle.update(deltaTime);

        // Garbage
        takeOutGarbage();
    }

    private void takeOutGarbage() {
        for (Object trash : garbage) {
            boolean b = entities.remove(trash) || players.remove(trash) || particles.remove(trash);
        }
    }

}
