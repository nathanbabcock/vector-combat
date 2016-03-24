package core;

import ai.AI;
import characters.Team;
import geometry.Polygon;
import maps.Map;
import maps.Map1;
import maps.Map2;
import maps.ctf_space;
import network.ChatMessage;
import particles.Particle;

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
    //public static transient List<Sprite> sprites;
    public transient Map map;
    public String mapID;
    public float countdown;
    public Team winner;

    public int redScore, blueScore;

    public transient static final float GRAVITY = -440;
    public transient static final int RESPAWN_TIME = 5;
    public transient static final float START_COUNTDOWN = 3;
    public transient static final int SCORE_LIMIT = 10;

    public transient byte nextClientID;
    public transient float time = 0;
    public int net_tick;

    public transient AI ai;

    public Game() {
        // TODO CopyOnWriteArrayList cannot possibly be the most efficient data structure for this...
        players = new CopyOnWriteArrayList<>();
        entities = new CopyOnWriteArrayList<>();
        garbage = new CopyOnWriteArrayList<>();
        particles = new CopyOnWriteArrayList<>();
        chat = new CopyOnWriteArrayList<>();
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
                entities.add(map.redflag.setGame(this));
                entities.add(map.blueflag.setGame(this));
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

    // TODO hacky
    public int getScore(Team team) {
        if (map.redflag != null)
            return team == Team.RED ? redScore : blueScore;

        int score = 0;
        for (Player player : players)
            if (player.team == team)
                score += player.kills;
        return score;
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
        redScore = other.redScore;
        blueScore = other.blueScore;
        countdown = other.countdown;
        winner = other.winner;
    }

}
