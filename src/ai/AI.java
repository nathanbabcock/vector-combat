package ai;

import characters.Character;
import geometry.Point2f;
import geometry.Vector2f;
import network.InputState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 3/24/2016.
 */
public class AI {
    public List<PathNode> nodes;

    public PathNode curNode;
    public PathEdge curEdge;

    public Point2f prevPos;
    public Vector2f prevVel;
    public InputState prevInput;

    public AI() {
        nodes = new ArrayList<>();
    }

    public void update(Character character, InputState inputState) {
        if (character == null) {
            curNode = null;
            curEdge = null;
            prevInput = null;
            prevPos = null;
            prevVel = null;
        } else if (character.onGround) {
            if (curEdge != null) {
                PathNode landNode = closestNode(character.getPosition());
                if (landNode != curNode) {
                    curEdge.toNode = curNode;
                    curEdge.toPos = character.getPosition();
                    curNode.edges.get(character.getCharClass()).add(curEdge);
                    System.out.println("Saving edge from " + curEdge.fromPos + " to " + curEdge.toPos);
                    curEdge = null;
                }
            }
            curNode = closestNode(character.getPosition());
            prevPos = character.getPosition();
            prevVel = character.velocity.copy();
            prevInput = inputState.copy();
        } else {
            if (curNode == null) return;
            if (curEdge == null) {
                curEdge = new PathEdge();
                curEdge.fromPos = prevPos;
                curEdge.fromVel = prevVel;
            }
            curEdge.frames.add(inputState.copy());
        }
    }

    public PathNode closestNode(Point2f p) {
        float minDist = Float.MAX_VALUE;
        PathNode minNode = null;
        for (PathNode pathNode : nodes) {
            if (p.x < pathNode.minX() || p.x > pathNode.maxX()) continue;
            Point2f pt = pathNode.getPoint(p.x);
            if (pt == null) continue;
            float y = pt.y;
            if (y > p.y) continue;
            final float dist = Math.abs(p.y - y);
            if (dist < minDist) {
                minDist = dist;
                minNode = pathNode;
            }
        }

        return minNode;
    }
}
