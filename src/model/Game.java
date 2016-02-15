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
import java.util.ArrayList;
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
    public transient List<Sprite> sprites;
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

    public Sprite getSprite(String id) {
        for (Sprite s : sprites)
            if (s.name.equals(id))
                return s;
        System.err.println("Error: could not find requested sprite: " + id);
        return null;
    }

    private void setupSprites() {
        BufferedImage spriteSheet = null;
        try {
            spriteSheet = ImageIO.read(new File("res/spritesheet.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        sprites = new ArrayList();

        // Rocketman
        sprites.add(new Sprite("rocket_standing")
                .setImage(spriteSheet, 0, 0, 24, 80));
        sprites.add(new Sprite("rocket_walking_1")
                .setImage(spriteSheet, 32, 0, 24, 80)
                .setTime(0.25f)
                .setNext("rocket_walking_2"));
        sprites.add(new Sprite("rocket_walking_2")
                .setImage(spriteSheet, 0, 0, 24, 80) // Same as rocket_standing
                .setTime(0.25f)
                .setNext("rocket_walking_1"));
        sprites.add(new Sprite("rocket_launcher")
                .setImage(spriteSheet, 64, 0, 64, 24));

        // Ninja
        sprites.add(new Sprite("ninja_standing")
                .setImage(spriteSheet, 0, 112, 40, 96)
                .setHitboxOffset(0, 16));
        sprites.add(new Sprite("ninja_walking_1")
                .setImage(spriteSheet, 48, 112, 40, 96)
                .setHitboxOffset(0, 16)
                .setTime(0.25f)
                .setNext("rocket_walking_2"));
        sprites.add(new Sprite("ninja_walking_2")
                .setImage(spriteSheet, 0, 112, 40, 96)
                .setHitboxOffset(0, 16)
                .setTime(0.25f)
                .setNext("ninja_walking_1"));
        sprites.add(new Sprite("ninja_attack_1")
                .setImage(spriteSheet, 96, 88, 24, 120)
                .setHitboxOffset(0, 40)
                .setTime(0.1f)
                .setNext("ninja_attack_2")
                .setInterruptible(false));
        sprites.add(new Sprite("ninja_attack_2")
                .setImage(spriteSheet, 128, 112, 64, 96)
                .setHitboxOffset(0, 16)
                .setTime(0.05f)
                .setNext("ninja_attack_3")
                .setInterruptible(false));
        sprites.add(new Sprite("ninja_attack_3")
                .setImage(spriteSheet, 200, 128, 80, 80)
                .setTime(0.1f)
                .setInterruptible(false));

        // Soldier
        sprites.add(new Sprite("soldier_standing")
                .setImage(spriteSheet, 0, 216, 32, 80)
                .setHitboxOffset(-8, 0));
        sprites.add(new Sprite("soldier_walking_1")
                .setImage(spriteSheet, 40, 216, 32, 80)
                .setHitboxOffset(-8, 0)
                .setTime(0.25f)
                .setNext("soldier_walking_2"));
        sprites.add(new Sprite("soldier_walking_2")
                .setImage(spriteSheet, 0, 216, 32, 80)
                .setHitboxOffset(-8, 0)
                .setTime(0.25f)
                .setNext("soldier_walking_1"));
        sprites.add(new Sprite("soldier_gun")
                .setImage(spriteSheet, 80, 216, 48, 24));

        // Scout
        sprites.add(new Sprite("scout_standing")
                .setImage(spriteSheet, 0, 304, 24, 80));
        sprites.add(new Sprite("scout_walking_1")
                .setImage(spriteSheet, 32, 304, 24, 80)
                .setTime(0.25f)
                .setNext("scout_walking_2"));
        sprites.add(new Sprite("scout_walking_2")
                .setImage(spriteSheet, 0, 304, 24, 80)
                .setTime(0.25f)
                .setNext("scout_walking_1"));
        sprites.add(new Sprite("scout_walljump")
                .setImage(spriteSheet, 64, 304, 40, 80)
                .setHitboxOffset(-12, 0));
        sprites.add(new Sprite("scout_gun")
                .setImage(spriteSheet, 112, 304, 48, 24));

        // Ninja2
        sprites.add(new Sprite("ninja2_standing")
                .setImage(spriteSheet, 0, 392, 74, 52)
                .setHitboxOffset(-11, 0));
        sprites.add(new Sprite("ninja2_walking_1")
                .setImage(spriteSheet, 81, 400, 52, 44)
                .setHitboxOffset(-22, 0)
                .setNext("ninja2_walking_2")
                .setTime(0.1f));
        sprites.add(new Sprite("ninja2_walking_2")
                .setImage(spriteSheet, 142, 390, 40, 54)
                .setHitboxOffset(-11, 0)
                .setNext("ninja2_walking_3")
                .setTime(0.1f));
        sprites.add(new Sprite("ninja2_walking_3")
                .setImage(spriteSheet, 187, 396, 54, 48)
                .setHitboxOffset(-22, 0)
                .setNext("ninja2_walking_4")
                .setTime(0.1f));
        sprites.add(new Sprite("ninja2_walking_4")
                .setImage(spriteSheet, 249, 390, 40, 54)
                .setHitboxOffset(-11, 0)
                .setNext("ninja2_walking_1")
                .setTime(0.1f));
        sprites.add(new Sprite("ninja2_attack_1")
                .setImage(spriteSheet, 7, 485, 60, 66)
                .setHitboxOffset(-32, 12)
                .setNext("ninja2_attack_2")
                .setTime(0.1f)
                .setInterruptible(false));
        sprites.add(new Sprite("ninja2_attack_2")
                .setImage(spriteSheet, 77, 459, 58, 92)
                .setHitboxOffset(0, 38)
                .setNext("ninja2_attack_3")
                .setTime(0.1f)
                .setInterruptible(false));
        sprites.add(new Sprite("ninja2_attack_3")
                .setImage(spriteSheet, 139, 495, 97, 84)
                .setHitboxOffset(-23, 2)
                .setNext("ninja2_attack_4")
                .setTime(0.1f)
                .setInterruptible(false));
        sprites.add(new Sprite("ninja2_attack_4")
                .setImage(spriteSheet, 243, 495, 82, 56)
                .setHitboxOffset(-52, 2)
                .setTime(0.1f)
                .setInterruptible(false));
        sprites.add(new Sprite("ninja2_parry")
                .setImage(spriteSheet, 298, 378, 58, 66)
                .setHitboxOffset(-22, 16)
                .setTime(1f)
                .setInterruptible(false));
        sprites.add(new Sprite("ninja2_kick")
                .setImage(spriteSheet, 367, 386, 58, 58)
                .setHitboxOffset(-8, -7));
        sprites.add(new Sprite("ninja2_grapple")
                .setImage(spriteSheet, 436, 395, 54, 49)
                .setHitboxOffset(-13, 0));
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
