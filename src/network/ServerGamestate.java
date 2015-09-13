package network;

import model.Game;
import model.geometry.Point2D;
import model.geometry.Vector2D;
import model.players.Player;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nathan on 9/12/2015.
 */
public class ServerGamestate implements Serializable {
    public HashMap<String, Vector2D> playerVelocities;
    public HashMap<String, Point2D> playerPositions;

    public ServerGamestate(Game game) {
        playerVelocities = new HashMap();
        playerPositions = new HashMap();

        for (Map.Entry<String, Player> entry : game.players.entrySet()) {
            playerVelocities.put(entry.getKey(), entry.getValue().velocity);
            playerPositions.put(entry.getKey(), entry.getValue().position);
        }
    }
}
