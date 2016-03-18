package model;

import model.characters.Team;
import model.geometry.Polygon;
import model.maps.Map;
import model.maps.Map1;
import model.maps.Map2;
import model.maps.ctf_space;
import model.particles.Particle;
import network.ChatMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Game {
    public List<Player> players;
    public List<Polygon> entities;
    public transient List<Object> garbage;
    public transient List<Particle> particles;
    public transient List<ChatMessage> chat;
    public static transient List<Sprite> sprites;
    public transient Map map;
    public String mapID;
    public float countdown;
    public Team winner;

    public transient static final float GRAVITY = -440;
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
            case "ctf_space":
                map = new ctf_space();
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

    public static Sprite getSprite(String id) {
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
            spriteSheet = ImageIO.read(Game.class.getResourceAsStream("/res/sprites/spritesheet.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        sprites = new ArrayList();

        // Newest ninja
        final float NINJA_RUN_TIME = 0.05f;


        for (String team : new String[]{"red", "blue"}) {
            sprites.add(new Sprite("ninja_crouch_" + team)
                    .setImage()
                    .setOffset(-14, 0));
            sprites.add(new Sprite("ninja_body_" + team)
                    .setImage()
                    .setOffset(1, 18));
        }

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
        for (String team : new String[]{"red", "blue"}) {
            sprites.add(new Sprite("ninja_stand_" + team)
                    .setImage()
                    .setOffset(4, 0));
            sprites.add(new Sprite("ninja_parry_" + team)
                    .setImage()
                    .setOffset(0, 0));
            sprites.add(new Sprite("ninja_kick_" + team)
                    .setImage()
                    .setOffset(-35, 0));
            sprites.add(new Sprite("ninja_jump_" + team)
                    .setImage()
                    .setRotationOrigin(29, 0) // ??
                    .setOffset(-6, 0));
        }

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
        for (String team : new String[]{"red", "blue"}) {
            sprites.add(new Sprite("commando_body_" + team)
                    .setImage()
                    .setOffset(-6, 31));
            sprites.add(new Sprite("commando_gun_" + team)
                    .setImage()
                    .setOffset(-1, 42));
        }


        // Rocketman
        for (String team : new String[]{"red", "blue"}) {
            sprites.add(new Sprite("rocketman_body_" + team)
                    .setImage()
                    .setOffset(-1, 31));
            sprites.add(new Sprite("rocketman_launcher_" + team)
                    .setImage()
                    .setOffset(-11, 41));
        }

        // Scout
        for (String team : new String[]{"red", "blue"}) {
            sprites.add(new Sprite("scout_walljump_" + team)
                    .setImage()
                    .setOffset(-4, -3));
            sprites.add(new Sprite("scout_body_" + team)
                    .setImage()
                    .setOffset(3, 30));
            sprites.add(new Sprite("scout_run_body_" + team)
                    .setImage()
                    .setOffset(1, 31));
        }
        sprites.add(new Sprite("scout_gun")
                .setImage()
                .setOffset(-3, 48));
        sprites.add(new Sprite("scout_gun_onehand")
                .setImage()
                .setOffset(-3, 48));
        sprites.add(new Sprite("scout_head")
                .setImage()
                .setOffset(2, 67));

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

        // Menu head sprites
        sprites.add(getSprite("scout_head").copy()
                .setName("scout_head_menu")
                .setOffset(-8, 0));
        sprites.add(new Sprite("commando_head_red")
                .setImage()
                .setOffset(-2, -2));
        sprites.add(new Sprite("commando_head_blue")
                .setImage()
                .setOffset(-2, -2));
        sprites.add(new Sprite("ninja_head_red")
                .setImage());
        sprites.add(new Sprite("ninja_head_blue")
                .setImage());
        sprites.add(new Sprite("rocketman_head")
                .setImage());

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
            for (Polygon entity : entities)
                entity.update(deltaTime);

            // Particles
            for (Particle particle : particles)
                particle.update(deltaTime);

            // Garbage
            collectGarbage();
            takeOutGarbage();
        } catch (Exception e) {
            e.printStackTrace(); // Try to power through errors without crashing
        }
    }

    private void collectGarbage() {
        for (Polygon entity : entities)
            if (entity.getCenter().x > map.width || entity.getCenter().y > map.height || entity.getCenter().x < 0 || entity.getCenter().y < 0)
                garbage.add(this);
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
        for (Polygon e : entities)
            e.game = this;

        // Other
//        sent = other.sent;
        countdown = other.countdown;
        winner = other.winner;
    }

}
