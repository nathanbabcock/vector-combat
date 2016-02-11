package network;

import model.characters.CharClass;
import model.characters.Team;

/**
 * Created by Nathan on 12/25/2015.
 */
public class SpawnParams {
    public Team team;
    public CharClass charClass;

    public SpawnParams() {
    }

    public SpawnParams(Team team, CharClass charClass) {
        this.team = team;
        this.charClass = charClass;
    }
}
