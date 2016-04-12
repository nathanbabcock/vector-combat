package ai;

import core.Game;
import core.Player;
import network.InputState;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Created by Nathan on 4/11/2016.
 */
public class AIController {
    private Player player;
    private Game game;
    private AI ai;

    private Queue<Object> path;
    private Queue<InputState> replay;

    public AIController(Player player) {
        this.player = player;
        ai = player.game.ai;
        game = player.game;
    }

    public void update(float delta) {
        if (player.character == null) {
            player.update(delta);
            return;
        }

        // Playback edge
        if (replay != null && !replay.isEmpty()) {
            player.character.importState(game.ai.replay.remove());
            return;
        }

        // Pick a path
        if (path == null || path.isEmpty()) {
            System.out.println("No path yet! Picking a random new one:");
            Random r = new Random();
            PathNode start = game.ai.closestNode(player.character.getPosition());
            PathNode dest = null;
            while (dest == null || dest == start)
                dest = game.ai.nodes.get(new Random().nextInt(7));
            System.out.println("Chose " + dest);
            Queue temp = game.ai.getPath(start, dest);
            System.out.println("Preliminary path looks like: " + temp);
            path = new LinkedList<>();
            Iterator nodes = temp.iterator();
            PathNode cur = (PathNode) nodes.next();
            path.add(cur);
            while (nodes.hasNext()) {
                PathNode node = (PathNode) nodes.next();
                PathEdge edge = cur.getEdgeTo(node, player.charClass);
                path.add(edge);
                path.add(node);
            }
            System.out.println(path);
        }

        int direction;
    }
}
