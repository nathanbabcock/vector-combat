package network;

import model.characters.Team;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Nathan on 9/19/2015.
 */
public class ChatMessage implements Serializable {
    Team team;
    String player, content;
    boolean teamOnly;
    Date time;

    public ChatMessage(String player, String content, Team team) {
        this(player, content, team, false);
    }

    public ChatMessage(String player, String content, Team team, boolean teamOnly) {
        this.player = player;
        this.team = team;
        this.content = content;
        this.teamOnly = teamOnly;
        this.time = new Date();
    }
}
