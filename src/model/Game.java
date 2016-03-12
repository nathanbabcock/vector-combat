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
        if (id == null)
            return null;

        for (Sprite s : sprites)
            if (s.name.equals(id))
                return s;
        System.err.println("Error: could not find requested sprite: " + id);
        return null;
    }

    public void initSprites() {
        System.out.println("Beginning sprite init");

        BufferedImage spriteSheet = null;
        try {
            spriteSheet = ImageIO.read(new File("res/spritesheet.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        sprites = new ArrayList();

        /*
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
                .setOffset(0, 16));
        sprites.add(new Sprite("ninja_walking_1")
                .setImage(spriteSheet, 48, 112, 40, 96)
                .setOffset(0, 16)
                .setTime(0.25f)
                .setNext("rocket_walking_2"));
        sprites.add(new Sprite("ninja_walking_2")
                .setImage(spriteSheet, 0, 112, 40, 96)
                .setOffset(0, 16)
                .setTime(0.25f)
                .setNext("ninja_walking_1"));
        sprites.add(new Sprite("ninja_attack_1")
                .setImage(spriteSheet, 96, 88, 24, 120)
                .setOffset(0, 40)
                .setTime(0.1f)
                .setNext("ninja_attack_2")
                .setInterruptible(false));
        sprites.add(new Sprite("ninja_attack_2")
                .setImage(spriteSheet, 128, 112, 64, 96)
                .setOffset(0, 16)
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
                .setOffset(-8, 0));
        sprites.add(new Sprite("soldier_walking_1")
                .setImage(spriteSheet, 40, 216, 32, 80)
                .setOffset(-8, 0)
                .setTime(0.25f)
                .setNext("soldier_walking_2"));
        sprites.add(new Sprite("soldier_walking_2")
                .setImage(spriteSheet, 0, 216, 32, 80)
                .setOffset(-8, 0)
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
                .setOffset(-12, 0));
        sprites.add(new Sprite("scout_gun")
                .setImage(spriteSheet, 112, 304, 48, 24));
 */
        // Newest ninja
        final float NINJA_RUN_TIME = 0.05f;

        sprites.add(new Sprite("ninja_crouch")
                .setImage()
                .setOffset(-14, 0));
        sprites.add(new Sprite("ninja_body")
                .setImage()
                .setOffset(1, 18));

        // Run cycle - first half
        sprites.add(new Sprite("ninja_legs_1")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-11, 0)
                .setNext("ninja_legs_2"));
        sprites.add(new Sprite("ninja_legs_2")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-10, 2)
                .setNext("ninja_legs_3"));
        sprites.add(new Sprite("ninja_legs_3")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-16, 0)
                .setNext("ninja_legs_4"));
        sprites.add(new Sprite("ninja_legs_4")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-4, 0)
                .setNext("ninja_legs_5"));
        sprites.add(new Sprite("ninja_legs_5")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(4, 0)
                .setNext("ninja_legs_6"));
        sprites.add(new Sprite("ninja_legs_6")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-5, 0)
                .setNext("ninja_legs_7"));
        sprites.add(new Sprite("ninja_legs_7")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-12, 0)
                .setNext("ninja_legs_8"));

        // Run cycle - second half
        sprites.add(new Sprite("ninja_legs_8")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-11, 0)
                .setNext("ninja_legs_9"));
        sprites.add(new Sprite("ninja_legs_9")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-10, 2)
                .setNext("ninja_legs_10"));
        sprites.add(new Sprite("ninja_legs_10")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-16, 0)
                .setNext("ninja_legs_11"));
        sprites.add(new Sprite("ninja_legs_11")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-4, 0)
                .setNext("ninja_legs_12"));
        sprites.add(new Sprite("ninja_legs_12")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(4, 0)
                .setNext("ninja_legs_13"));
        sprites.add(new Sprite("ninja_legs_13")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-5, 0)
                .setNext("ninja_legs_14"));
        sprites.add(new Sprite("ninja_legs_14")
                .setImage()
                .setTime(NINJA_RUN_TIME)
                .setOffset(-12, 0)
                .setNext("ninja_legs_1"));

        // Attack cycle - running
        final float NINJA_ATTACK_TIME = 0.5f / 8;
        sprites.add(new Sprite("ninja_arm")
                .setImage()
                .setOffset(-55, 25));
        sprites.add(new Sprite("ninja_arm_attack_1")
                .setImage()
                .setOffset(-6, 32)
                .setTime(NINJA_ATTACK_TIME)
                .setNext("ninja_arm_attack_2")
                .setInterruptible(false));
        sprites.add(new Sprite("ninja_arm_attack_2")
                .setImage()
                .setOffset(36, 24)
                .setTime(NINJA_ATTACK_TIME)
                .setNext("ninja_arm_attack_3")
                .setInterruptible(false));
        sprites.add(new Sprite("ninja_arm_attack_3")
                .setImage()
                .setOffset(33, 11)
                .setTime(NINJA_ATTACK_TIME)
                .setNext("ninja_arm_attack_4")
                .setInterruptible(false));
        sprites.add(new Sprite("ninja_arm_attack_4")
                .setImage()
                .setOffset(29, 7)
                .setTime(NINJA_ATTACK_TIME)
                .setNext("ninja_arm_attack_5")
                .setInterruptible(false));
        sprites.add(new Sprite("ninja_arm_attack_5")
                .setImage()
                .setOffset(15, -21)
                .setTime(NINJA_ATTACK_TIME * 3)
                //.setNext("ninja_arm_attack_1")
                .setInterruptible(false));

        // TODO this is redundant
        // Attack cycle - standing
        sprites.add(new Sprite("ninja_stand_arm_attack_1")
                .setImage()
                .setOffset(-14, 39)//(-6, 32)
                .setTime(NINJA_ATTACK_TIME)
                .setNext("ninja_stand_arm_attack_2")
                .setInterruptible(false));
        sprites.add(new Sprite("ninja_stand_arm_attack_2")
                .setImage()
                .setOffset(23, 31)//(36, 24) // (-13, +7)
                .setTime(NINJA_ATTACK_TIME)
                .setNext("ninja_stand_arm_attack_3")
                .setInterruptible(false));
        sprites.add(new Sprite("ninja_stand_arm_attack_3")
                .setImage()
                .setOffset(20, 18)
                .setTime(NINJA_ATTACK_TIME)
                .setNext("ninja_stand_arm_attack_4")
                .setInterruptible(false));
        sprites.add(new Sprite("ninja_stand_arm_attack_4")
                .setImage()
                .setOffset(13, 14)
                .setTime(NINJA_ATTACK_TIME)
                .setNext("ninja_stand_arm_attack_5")
                .setInterruptible(false));
        sprites.add(new Sprite("ninja_stand_arm_attack_5")
                .setImage()
                .setOffset(2, -14)
                .setTime(NINJA_ATTACK_TIME * 3)
                //.setNext("ninja_stand_arm_attack_1")
                .setInterruptible(false));

        // Ninja miscellaneous
        sprites.add(new Sprite("ninja_arm_grapple")
                .setImage()
                .setOffset(28, 15));
        sprites.add(new Sprite("ninja_arm_grapple_2")
                .setImage()
                .setOffset(29, 30));
        sprites.add(new Sprite("ninja_stand")
                .setImage()
                .setOffset(4, 0));
        sprites.add(new Sprite("ninja_parry")
                .setImage()
                .setOffset(0, 0));
        sprites.add(new Sprite("ninja_kick")
                .setImage()
                .setOffset(-35, 0));
        sprites.add(new Sprite("ninja_jump")
                .setImage()
                .setRotationOrigin(29, 0) // ??
                .setOffset(-6, 0));

        // Generic legs
        final float LEG_TIME = 0.7f / 8;
        sprites.add(new Sprite("legs_stand")
                .setImage()
                .setOffset(0, 0));
        sprites.add(new Sprite("legs_walk_1")
                .setImage()
                .setOffset(0, 0)
                .setNext("legs_walk_2")
                .setTime(LEG_TIME));
        sprites.add(new Sprite("legs_walk_2")
                .setImage()
                .setOffset(-7, 0)
                .setNext("legs_walk_3")
                .setTime(LEG_TIME));
        sprites.add(new Sprite("legs_walk_3")
                .setImage()
                .setOffset(-7, 0)
                .setNext("legs_walk_4")
                .setTime(LEG_TIME));
        sprites.add(new Sprite("legs_walk_4")
                .setImage()
                .setOffset(-4, 0)
                .setNext("legs_walk_5")
                .setTime(LEG_TIME));
        sprites.add(new Sprite("legs_walk_5")
                .setImage()
                .setOffset(0, 0)
                .setNext("legs_walk_6")
                .setTime(LEG_TIME));
        sprites.add(new Sprite("legs_walk_6")
                .setImage()
                .setOffset(-7, 0)
                .setNext("legs_walk_7")
                .setTime(LEG_TIME));
        sprites.add(new Sprite("legs_walk_7")
                .setImage()
                .setOffset(-7, 0)
                .setNext("legs_walk_8")
                .setTime(LEG_TIME));
        sprites.add(new Sprite("legs_walk_8")
                .setImage()
                .setOffset(-4, 0)
                .setNext("legs_walk_1")
                .setTime(LEG_TIME));

        // Commando
        sprites.add(new Sprite("commando_red_body")
                .setImage()
                .setOffset(-6, 31));
        sprites.add(new Sprite("commando_red_gun")
                .setImage()
                .setOffset(-1, 42));


        // Rocketman
        sprites.add(new Sprite("rocketman_red_body")
                .setImage()
                .setOffset(-1, 31));
        sprites.add(new Sprite("rocketman_red_launcher")
                .setImage()
                .setOffset(-11, 41));

        // Scout
        sprites.add(new Sprite("scout_red_body")
                .setImage()
                .setOffset(3, 30));
        sprites.add(new Sprite("scout_red_run_body")
                .setImage()
                .setOffset(1, 31));
        sprites.add(new Sprite("scout_gun")
                .setImage()
                .setOffset(-3, 48));
        sprites.add(new Sprite("scout_head")
                .setImage()
                .setOffset(1, 67));

        // Scout legs
        sprites.add(new Sprite("scout_legs_stand")
                .setImage()
                .setOffset(1, -4));
        sprites.add(new Sprite("scout_legs_run_1")
                .setImage()
                .setOffset(-23, 0)
                .setTime(NINJA_RUN_TIME)
                .setNext("scout_legs_run_2"));
        sprites.add(new Sprite("scout_legs_run_2")
                .setImage()
                .setOffset(-22, 11)
                .setTime(NINJA_RUN_TIME)
                .setNext("scout_legs_run_3"));
        sprites.add(new Sprite("scout_legs_run_3")
                .setImage()
                .setOffset(-23, 15)
                .setTime(NINJA_RUN_TIME)
                .setNext("scout_legs_run_4"));
        sprites.add(new Sprite("scout_legs_run_4")
                .setImage()
                .setOffset(-19, 0)
                .setTime(NINJA_RUN_TIME)
                .setNext("scout_legs_run_5"));
        sprites.add(new Sprite("scout_legs_run_5")
                .setImage()
                .setOffset(-10, -1)
                .setTime(NINJA_RUN_TIME)
                .setNext("scout_legs_run_6"));
        sprites.add(new Sprite("scout_legs_run_6")
                .setImage()
                .setOffset(-6, 0)
                .setTime(NINJA_RUN_TIME)
                .setNext("scout_legs_run_7"));
        sprites.add(new Sprite("scout_legs_run_7")
                .setImage()
                .setOffset(1, -1)
                .setTime(NINJA_RUN_TIME)
                .setNext("scout_legs_run_8"));
        sprites.add(new Sprite("scout_legs_run_8")
                .setImage()
                .setOffset(-3, 0)
                .setTime(NINJA_RUN_TIME)
                .setNext("scout_legs_run_1"));

        System.out.println("Finished sprite init");
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
