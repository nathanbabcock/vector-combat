package ai;

import characters.Character;
import core.Game;
import core.Player;
import geometry.Point2f;
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

    public void pathfinding() {
        try {
            if (player.character == null || player.charClass == null) {
                path = null;
                replay = null;
                return;
            }

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
                //Character excalo = game.getPlayer("excalo").character;
                //if(excalo == null) return;
                //dest = game.ai.closestNode(excalo.getPosition());
                //if (start == dest) return;

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
                System.out.println("Processed path: " + path);
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public void moveTowardsEdge() {
        try {
            if (path == null || path.isEmpty())
                return;
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

    public void aimAt(Character target) {
        player.character.xhair = target.getPosition();
    }

    public void attackTarget(Character target) {
        if (target == null)
            player.character.attacking = false;
        else {
            aimAt(target);
            player.character.attacking = true;
        }
    }

    public void offensiveMovement(Character target) {
        Point2f targetPos = target.getPosition();
        Point2f pos = player.character.getPosition();

        if (targetPos.x > pos.x) {
            player.character.movingRight = true;
            player.character.movingLeft = false;
        } else {
            player.character.movingLeft = true;
            player.character.movingRight = false;
        }
    }

    public void defensiveMovement(Character target) {
        Point2f targetPos = target.getPosition();
        Point2f pos = player.character.getPosition();

        if (targetPos.x > pos.x) {
            player.character.movingRight = false;
            player.character.movingLeft = true;
        } else {
            player.character.movingLeft = false;
            player.character.movingRight = true;
        }
    }

    public void neutralMovement() {
        player.character.movingLeft = false;
        player.character.movingRight = false;
    }

    public void combatMovement(Character target) {
        if (player.character == null) return;

        // Watch out for the edge of the node
        Point2f pos = player.character.getPosition();
        PathNode node = ai.closestNode(player.character.getPosition());

        final int MARGIN = 10;
        if (pos.x < node.minX() || Math.abs(pos.x - node.minX()) < MARGIN) {
            player.character.movingLeft = false;
            player.character.movingRight = true;
            return;
        } else if (pos.x > node.maxX() || Math.abs(pos.x - node.maxX()) < MARGIN) {
            player.character.movingLeft = true;
            player.character.movingRight = false;
            return;
        }

        // No target (move randomly)
        if (target == null) {
            if (r.nextFloat() > 0.1f) return; // Don't change
            switch (r.nextInt(3)) {
                case 0:
                    player.character.movingLeft = false;
                    player.character.movingRight = true;
                    break;
                case 1:
                    player.character.movingLeft = false;
                    player.character.movingRight = false;
                    break;
                case 2:
                    player.character.movingLeft = true;
                    player.character.movingRight = false;
                    break;
            }

            return;
        }

        // Target
        if (r.nextFloat() > 0.1f) return; // Don't change strategies
        System.out.println("Changing strategy");
        switch (r.nextInt(3)) {
            case 0:
                offensiveMovement(target);
                break;
            case 1:
                neutralMovement();
                break;
            case 2:
                defensiveMovement(target);
                break;
        }
    }

    public void randomJump() {

        if (r.nextFloat() < 0.05f)
            player.character.movingUp = true;
        else
            player.character.movingUp = false;
    }

    public void update(float delta) {
        try {
            // TODO Detect when AI gets lost (lands on node not specified by previous edge playback)
            //pathfinding();

            if (player.character == null) return;

            // Perception
            Character target = null;

            for (Player other : game.players) {
                if (other.character == null) continue;
                if (other == player) continue;
                //if (other.team == player.team) continue;

                final int SIGHT_RADIUS = 1000;
                if (player.character.getPosition().distance(other.character.getPosition()) < SIGHT_RADIUS) {
                    target = other.character;
                    break;
                }
            }

            attackTarget(target);
            combatMovement(target);
            randomJump();

            //moveTowardsEdge();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
