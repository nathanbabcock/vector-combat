package model;

import model.characters.Character;
import model.characters.Team;

import java.io.Serializable;

/**
 * Created by Nathan on 12/26/2015.
 */
public class Player implements Serializable {
    public String clientName;
    public Character character;
    public int kills, deaths;
    public Team team;
    public float respawnTime;
}
