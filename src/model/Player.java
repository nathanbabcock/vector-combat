package model;

import model.characters.Character;
import model.characters.*;
import model.geometry.Point2D;
import network.SpawnParams;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Nathan on 12/26/2015.
 */
public class Player implements Serializable {
    public transient Game game;

    public String clientName;
    public int clientID;
    public Class charClass;
    public Team team;
    public Character character;
    public float respawnTime;
    public int kills, deaths, ping;

    public Player() {
        this(null, null);
    }

    public Player(Game game, String clientName) {
        this.game = game;
        this.clientName = clientName;
        respawnTime = 0;
        kills = 0;
        deaths = 0;
        ping = 999;
        clientID = -1;
    }

    public void update(float delta) {
        if ((character == null || character.dead) && team != null && charClass != null) { // Waiting to spawn
            if (respawnTime > 0 && game.countdown <= 0)
                respawnTime -= delta;
            else
                spawn();
        } else if (character != null)
            character.update(delta);
    }

    public void kill() {
        character = null;
    }

    public void spawn() {
        // Initialize character
        if (charClass == null) {
            System.err.println("Attempting to spawn player " + clientName + " with no class chosen");
            return;
        } else if (charClass.equals(Rocketman.class))
            character = new Rocketman(this);
        else if (charClass.equals(Ninja.class))
            character = new Ninja(this);
        else if (charClass.equals(Soldier.class))
            character = new Soldier(this);
        else if (charClass.equals(Scout.class))
            character = new Scout(this);
        else
            return;
        character.player = this;

        // Set spawn point
        ArrayList<Point2D> spawns;
        if (team == Team.BLUE)
            spawns = game.map.spawnpoints_blue;
        else if (team == Team.RED)
            spawns = game.map.spawnpoints_red;
        else {
            System.out.println("Error: attempting to spawn player " + clientName + " who is not on a playable team.");
            return;
        }
        Random rand = new Random();
        character.hitbox.position = spawns.get(rand.nextInt(spawns.size())).copy();

        // Reset respawn timeg
        respawnTime = game.RESPAWN_TIME;
    }

    public void importSpawnParams(SpawnParams other) {
        if (team != other.team)
            team = other.team;
        if (charClass != other.charClass)
            charClass = other.charClass;
    }
}
