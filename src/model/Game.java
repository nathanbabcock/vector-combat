package model;

import model.characters.Team;
import model.entities.Entity;
import model.maps.Map;
import model.maps.Map1;
import model.maps.Map2;
import model.particles.Particle;
import network.ChatMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Game {
    public List<Player> players;
    public List<Entity> entities;

    public transient List<Object> garbage;
    public transient List<Particle> particles;
    public transient List<ChatMessage> chat;
    public transient HashMap<String, Sprite> sprites;
    public transient Map map;
    public String mapID;
    public float countdown;
    public Team winner;

    public transient static final float GRAVITY = -400;
    public transient static final int RESPAWN_TIME = 5;
    public transient static final float START_COUNTDOWN = 3;
    public transient static final int SCORE_LIMIT = 10;

    public transient byte nextClientID;
    public transient float time = 0;
    public int net_tick;

    public Game() {
        setupSprites();

        // TODO CopyOnWriteArrayList cannot possibly be the most efficient data structure for this...
        players = new CopyOnWriteArrayList();
        entities = new CopyOnWriteArrayList();
        garbage = new CopyOnWriteArrayList();
        particles = new CopyOnWriteArrayList();
        chat = new CopyOnWriteArrayList();
        countdown = START_COUNTDOWN;
        nextClientID = 0;
    }

    public Player getPlayer(String clientName) {
        for (Player p : players)
            if (p.clientName.equals(clientName))
                return p;
        return null;
    }

    public Player getPlayer(byte clientID) {
        for (Player p : players)
            if (p.clientID == clientID)
                return p;
        return null;
    }

    public void setMap(String mapID) {
        this.mapID = mapID;
        switch (mapID) {
            case "Map1":
                map = new Map1();
                break;
            case "Map2":
                map = new Map2();
                break;
            default:
                System.err.println("Unknown map " + mapID + " specified");
                break;
        }
    }

    public void checkWin() {
        if (getScore(Team.RED) >= SCORE_LIMIT)
            winner = Team.RED;
        else if (getScore(Team.BLUE) >= SCORE_LIMIT)
            winner = Team.BLUE;
        else
            winner = null;
    }

    public int getScore(Team team) {
        int score = 0;
        for (Player player : players)
            if (player.team == team)
                score += player.kills;
        return score;
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
        sprites.put("ninja_attack_1", new Sprite(spriteSheet, 96, 88, 24, 120, 0, 40));
        sprites.put("ninja_attack_2", new Sprite(spriteSheet, 128, 112, 64, 96, 0, 16));
        sprites.put("ninja_attack_3", new Sprite(spriteSheet, 200, 128, 80, 80, 0, 0));

        sprites.put("soldier_standing", new Sprite(spriteSheet, 0, 216, 32, 80, -8, 0));
        sprites.put("soldier_walking", new Sprite(spriteSheet, 40, 216, 32, 80, -8, 0));
        sprites.put("soldier_gun", new Sprite(spriteSheet, 80, 216, 48, 24));

        sprites.put("scout_standing", new Sprite(spriteSheet, 0, 304, 24, 80));
        sprites.put("scout_walking", new Sprite(spriteSheet, 32, 304, 24, 80));
        sprites.put("scout_walljump", new Sprite(spriteSheet, 64, 304, 40, 80, -12, 0));
        sprites.put("scout_gun", new Sprite(spriteSheet, 112, 304, 48, 24));

        sprites.put("ninja2_standing", new Sprite(spriteSheet, 0, 392, 74, 52, -11, 0));
        sprites.put("ninja2_run1", new Sprite(spriteSheet, 81, 400, 52, 44, -22, 0).setNext("ninja2_run2").setTime(0.1f));
        sprites.put("ninja2_run2", new Sprite(spriteSheet, 142, 390, 40, 54, -11, 0).setNext("ninja2_run3").setTime(0.1f));
        sprites.put("ninja2_run3", new Sprite(spriteSheet, 187, 396, 54, 48, -22, 0).setNext("ninja2_run4").setTime(0.1f));
        sprites.put("ninja2_run4", new Sprite(spriteSheet, 249, 390, 40, 54, -11, 0).setNext("ninja2_run1").setTime(0.1f));
        sprites.put("ninja2_parry", new Sprite(spriteSheet, 298, 378, 58, 66, -22, -16).setTime(1f));
        sprites.put("ninja2_kick", new Sprite(spriteSheet, 367, 386, 58, 58, -8, -7));
        sprites.put("ninja2_grapple", new Sprite(spriteSheet, 436, 395, 54, 49, -13, 0));
        sprites.put("ninja2_attack1", new Sprite(spriteSheet, 7, 485, 60, 66, -32, 12).setNext("ninja2_attack2").setTime(0.1f));
        sprites.put("ninja2_attack2", new Sprite(spriteSheet, 77, 459, 58, 92, 0, 38).setNext("ninja2_attack3").setTime(0.1f));
        sprites.put("ninja2_attack3", new Sprite(spriteSheet, 139, 495, 97, 84, -23, 2).setNext("ninja2_attack4").setTime(0.1f));
        sprites.put("ninja2_attack4", new Sprite(spriteSheet, 243, 495, 82, 56, -52, 2).setNext("ninja2_attack1").setTime(0.1f));
    }

    /**
     * @param deltaTime in seconds
     */
    public void update(float deltaTime) {
        try {
            // Debug
            time += deltaTime;
//      System.out.println("t = " + time + ", pos = " + player.position + ", v = (" + player.velocity.x + ", " + player.velocity.y + "), a = (" + player.acceleration.x + ", " + player.acceleration.y + ")");

            // Countdown
            if (countdown > 0)
                countdown -= deltaTime;

            // Players
            for (Player player : players) // Update characters
                player.update(deltaTime);

            // Entities
            for (Entity entity : entities)
                entity.update(deltaTime);

            // Particles
            for (Particle particle : particles)
                particle.update(deltaTime);

            // Garbage
            takeOutGarbage();
        } catch (Exception e) {
            e.printStackTrace(); // Try to power through errors without crashing
        }
    }

    private void takeOutGarbage() {
        for (Object trash : garbage) {
            boolean b = players.remove(trash) || entities.remove(trash) || particles.remove(trash);
        }
    }

    public void importGame(Game other) {
        // Players
        for (Player p : other.players) {
            p.game = this;
            Player oldPlayer = getPlayer(p.clientName);
            if (p.character != null) {
                if (oldPlayer == null) {
                    oldPlayer = new Player(this, p.clientName);
                }
                if (oldPlayer.character == null) {
                    oldPlayer.charClass = p.charClass;
                    oldPlayer.team = p.team;
                    oldPlayer.spawn();
                }
                p.character.merge(oldPlayer.character);
                p.character.game = this;
                p.character.player = p;
            }
        }
        players = other.players;

        // Entities
        entities = other.entities;
        for (Entity e : entities)
            e.game = this;

        // Other
//        sent = other.sent;
        countdown = other.countdown;
        winner = other.winner;
    }

}
