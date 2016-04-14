package ai;

import core.Game;
import core.Player;
import network.GameServer;
import network.InputState;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

import static network.Network.TIMESCALE;

/**
 * Created by Nathan on 4/11/2016.
 */
public class AIController {
    private Player player;
    private Game game;
    private AI ai;
    private Random r;

    private Queue<PathEdge> path;
    private Queue<InputState> replay;

    public AIController(Player player) {
        this.player = player;
        ai = player.game.ai;
        game = player.game;
        r = new Random();
    }

    private void moveRandom() {
        if (r.nextBoolean()) { // Is moving
            if (r.nextBoolean()) { // Left
                player.character.movingLeft = true;
                player.character.movingRight = false;
            } else { // Right
                player.character.movingRight = true;
                player.character.movingLeft = false;
            }
        }

        if (r.nextBoolean())
            player.character.movingUp = true;

    }

    public void update(float delta) {
        // TODO Detect when AI gets lost (lands on node not specified by previous edge playback)
        // TODO Rigorously test A* pathfinding

        try {
            if (player.character == null || player.charClass == null)
                return;

            // Playback edge
            if (replay != null && !replay.isEmpty()) {
                player.character.importState(replay.remove());
                return;
            }

            // Pick a path
            if (path == null || path.isEmpty()) {
                if (ai.closestNode(player.character.getPosition()) == null) {
                    moveRandom();
                    return;
                }
                System.out.println("No path yet! Picking a random new one:");
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
                while (nodes.hasNext()) {
                    PathNode node = (PathNode) nodes.next();
                    PathEdge edge = cur.getEdgeTo(node, player.charClass);
                    path.add(edge);
                }
                System.out.println(path);
            }

            // Move towards next
            float dist = path.peek().fromPos.x - player.character.getPosition().x;
            if (Math.abs(dist) < player.character.moveSpeed * (TIMESCALE / GameServer.NET_FPS)) {
                replay = new LinkedList<>(path.poll().frames);
            } else if (dist > 0) {
                player.character.movingRight = true;
                player.character.movingLeft = false;
            } else {
                player.character.movingLeft = true;
                player.character.movingRight = false;
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
