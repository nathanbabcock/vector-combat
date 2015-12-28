package network;

import model.characters.Team;

import java.io.Serializable;

/**
 * Created by Nathan on 12/25/2015.
 */
public class SpawnParams implements Serializable {
    public Team team;
    public Class charClass;

    public SpawnParams(Team team, Class charClass) {
        this.team = team;
        this.charClass = charClass;
    }
}
