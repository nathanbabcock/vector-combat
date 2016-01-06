package model;

import model.characters.Character;
import model.characters.Team;
import model.entities.Bullet;
import model.entities.Entity;
import model.maps.Map;
import model.maps.Map1;
import model.maps.Map2;
import model.particles.Particle;
import network.ChatMessage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Game implements Serializable {
    public ConcurrentHashMap<String, Player> players;
    public ConcurrentHashMap<String, Entity> entities;

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
    public transient static final float START_COUNTDOWN = 10;
    public transient static final int SCORE_LIMIT = 10;

    public transient float time = 0;

    public Game() {
        setupSprites();

        players = new ConcurrentHashMap();
        entities = new ConcurrentHashMap();
        garbage = new Vector();
        particles = new Vector();
        chat = new Vector();
        countdown = START_COUNTDOWN;
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
        for (Player player : players.values())
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
    }

    public void update(float deltaTime) {
        // Debug
        time += deltaTime;
//      System.out.println("t = " + time + ", pos = " + player.position + ", v = (" + player.velocity.x + ", " + player.velocity.y + "), a = (" + player.acceleration.x + ", " + player.acceleration.y + ")");

        // Countdown
        if (countdown > 0)
            countdown -= deltaTime;

        // Players
        for (Player player : players.values()) // Update characters
            player.update(deltaTime);

        // Entities
        for (Entity entity : entities.values())
            entity.update(deltaTime);

        // Particles
        for (Particle particle : particles)
            particle.update(deltaTime);

        // Garbage
        takeOutGarbage();
    }

    private void takeOutGarbage() {
        for (Object trash : garbage) {
            /*boolean b;
            if (trash instanceof String) {
                String key = (String) trash;
                b = characters.remove(key) != null || entities.remove(key) != null;
            } else
                */
            boolean b = players.values().remove(trash) || entities.values().remove(trash) || particles.remove(trash);
        }
    }

    // TODO someday optimize this, as well as the weight of the gamestate other being passed over network
    public void importGame(Game other) {
        countdown = other.countdown;
        winner = other.winner;

        // Add/merge (characters)
        ConcurrentHashMap<String, Player> newPlayers = new ConcurrentHashMap();
        for (java.util.Map.Entry<String, Player> entry : other.players.entrySet()) {
            String clientName = entry.getKey();
            Player otherPlayer = entry.getValue();
            if (players.containsKey(clientName) && players.get(clientName).charClass == otherPlayer.charClass)
                newPlayers.put(clientName, players.get(clientName));
            else
                newPlayers.put(clientName, otherPlayer);

            Player player = newPlayers.get(clientName);
            player.clientName = otherPlayer.clientName;
            player.kills = otherPlayer.kills;
            player.deaths = otherPlayer.deaths;
            player.ping = otherPlayer.ping;
            player.respawnTime = otherPlayer.respawnTime;
            player.team = otherPlayer.team;
            player.charClass = otherPlayer.charClass;
            player.game = this;

            Character oldCharacter = null;
            if (players.get(clientName) != null)
                oldCharacter = players.get(clientName).character;
            Character otherCharacter = otherPlayer.character;
            if (otherCharacter == null) {
                player.character = null;
                continue;
            } else if (oldCharacter == null || oldCharacter.getClass() != otherCharacter.getClass()) {
                //player.character = charFactory(otherPlayer.charClass);
                player.spawn();
            }
            player.character.velocity = otherCharacter.velocity;
            player.character.acceleration = otherCharacter.acceleration;
            player.character.hitbox = otherCharacter.hitbox;
            player.character.xhair = otherCharacter.xhair;
            player.character.health = otherCharacter.health;
            player.character.dead = otherCharacter.dead;
            player.character.movingLeft = otherCharacter.movingLeft;
            player.character.movingRight = otherCharacter.movingRight;
            player.character.movingUp = otherCharacter.movingUp;
            player.character.movingDown = otherCharacter.movingDown;
            player.character.attacking = otherCharacter.attacking;
            player.character.altAttacking = otherCharacter.altAttacking;
            player.character.onGround = otherCharacter.onGround;
            player.character.wallLeft = otherCharacter.wallLeft;
            player.character.wallRight = otherCharacter.wallRight;
            player.character.player = player;
            player.character.game = this;
        }
        players = newPlayers;

        // Add/merge (entity)
        ConcurrentHashMap<String, Entity> newEntities = new ConcurrentHashMap();
        for (java.util.Map.Entry<String, Entity> entry : other.entities.entrySet()) {
            String key = entry.getKey();
            if (entities.containsKey(key))
                newEntities.put(key, entities.get(key));
            else
                newEntities.put(key, entry.getValue());
            Entity newEntity = newEntities.get(key);
            Entity otherEntity = entry.getValue();

            if (newEntity instanceof Bullet)
                ((Bullet) newEntity).owner = ((Bullet) otherEntity).owner;

            newEntity.velocity = otherEntity.velocity;
            newEntity.hitbox = otherEntity.hitbox;
            newEntity.game = this;
        }
        entities = newEntities;
    }

}
