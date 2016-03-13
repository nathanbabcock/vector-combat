package model;

import model.characters.*;
import model.characters.Character;
import model.geometry.Point2f;
import network.Ping;
import network.SpawnParams;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Created by Nathan on 12/26/2015.
 */
public class Player {
    public transient Game game;
    public transient Queue<Ping> pings;

    public String clientName;
    public byte clientID;
    public CharClass charClass;
    public Team team;
    public Character character;
    public float respawnTime;
    public int kills, deaths, ping;

    public Player() {
    }

    public Player(Game game, String clientName) {
        this.game = game;
        this.clientName = clientName;
        respawnTime = 0;
        kills = 0;
        deaths = 0;
        ping = 999;
        clientID = game.nextClientID++;
        pings = new LinkedList<>();
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
        } else if (charClass == CharClass.ROCKETMAN)
            character = new Rocketman(this);
        else if (charClass == CharClass.NINJA)
            character = new Ninja(this);
        else if (charClass == CharClass.COMMANDO)
            character = new Commando(this);
        else if (charClass == CharClass.SCOUT)
            character = new Scout(this);
        else
            return;
        character.player = this;

        // Set spawn point
        ArrayList<Point2f> spawns;
        if (team == Team.BLUE)
            spawns = game.map.spawnpoints_blue;
        else if (team == Team.RED)
            spawns = game.map.spawnpoints_red;
        else {
            System.out.println("Error: attempting to spawn player " + clientName + " who is not on a playable team.");
            return;
        }
        Random rand = new Random();
        character.position = spawns.get(rand.nextInt(spawns.size())).copy();

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
