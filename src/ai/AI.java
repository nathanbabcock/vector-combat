package ai;

import characters.CharClass;
import characters.Character;
import geometry.Point2f;
import geometry.Vector2f;
import network.InputState;

import java.io.*;
import java.util.*;

/**
 * Created by Nathan on 3/24/2016.
 */
public class AI {
    public List<PathNode> nodes;
    public List<PathEdge> edges;

    public PathNode curNode;
    public PathEdge curEdge;

    public Point2f prevPos;
    public Vector2f prevVel;
    public InputState prevInput;

    public Queue<InputState> replay;

    public boolean recording = false;

    public AI() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        replay = new LinkedList<>();
    }

    public void update(Character character, InputState inputState) {
        if (!replay.isEmpty() || !recording)
            return;

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
                    curEdge.frames.add(inputState.copy());
                    curEdge.toNode = landNode;
                    curEdge.toPos = character.getPosition();
                    curNode.edges.get(character.getCharClass()).add(curEdge);
                    edges.add(curEdge);
                    System.out.println("Saving edge from " + curEdge.fromPos + " to " + curEdge.toPos + " with " + curEdge.frames.size() + " frames.");
                    curEdge = null;
                } else {
                    System.out.println("Discarding edge to same node.");
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
                curEdge.fromNode = curNode;
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

    public void writeEdges(String filename) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename), "utf-8"))) {
            for (PathEdge edge : edges) {
                writer.write(edge.fromNode.index + " " + edge.toNode.index + " ");
                writer.write(edge.fromPos.x + " " + edge.fromPos.y + " " + edge.toPos.x + " " + edge.toPos.y + " ");
                writer.write(edge.fromVel.x + " " + edge.fromVel.y + " ");
                writer.write(edge.frames.size() + "\n");
                for (InputState input : edge.frames) {
                    writer.write(input.movingLeft + " " + input.movingRight + " " + input.movingUp + " " + input.movingDown + " ");
                    writer.write(input.attacking + " " + input.altAttacking + " ");
                    writer.write(input.xhair.x + " " + input.xhair.y);
                    writer.write("\n");
                }
            }
            writer.close();
            System.out.println("Wrote " + edges.size() + " path edges to " + filename);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void readEdges(String filename, CharClass charClass) {
        //List<PathNode> nodes = new ArrayList<>();
        File file = new File(filename);
        try {
            Scanner scanner = new Scanner(file);
            while (scanner.hasNext()) {
                PathEdge curEdge = new PathEdge();
                curEdge.fromNode = nodes.get(scanner.nextInt());
                curEdge.toNode = nodes.get(scanner.nextInt());
                curEdge.fromPos = new Point2f(scanner.nextFloat(), scanner.nextFloat());
                curEdge.toPos = new Point2f(scanner.nextFloat(), scanner.nextFloat());
                curEdge.fromVel = new Vector2f(scanner.nextFloat(), scanner.nextFloat());
                int frames = scanner.nextInt();
                for (int i = 0; i < frames; i++) {
                    InputState inputstate = new InputState();
                    inputstate.movingLeft = scanner.nextBoolean();
                    inputstate.movingRight = scanner.nextBoolean();
                    inputstate.movingUp = scanner.nextBoolean();
                    inputstate.movingDown = scanner.nextBoolean();
                    inputstate.attacking = scanner.nextBoolean();
                    inputstate.altAttacking = scanner.nextBoolean();
                    inputstate.xhair = new Point2f(scanner.nextFloat(), scanner.nextFloat());
                    curEdge.frames.add(inputstate);
                }
                edges.add(curEdge);
                curEdge.fromNode.edges.get(charClass).add(curEdge);
            }

            System.out.println("Read " + edges.size() + " path edges from " + filename);
            //return nodes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //return null;
    }

    // TODO find a better one
    public float heuristic(PathNode start, PathNode goal) {
        return start.points.get(0).distance(goal.points.get(0));
    }

    /*
     * A* pathfinding
     */
    public List<Object> getPath(PathNode start, PathNode goal) {
        // The set of nodes already evaluated.
        ArrayList<PathNode> closedSet = new ArrayList<>();

        // The set of currently discovered nodes still to be evaluated.
        // Initially, only the start node is known.
        ArrayList<PathNode> openSet = new ArrayList<>();
        openSet.add(start);

        // For each node, which node it can most efficiently be reached from.
        // If a node can be reached from many nodes, cameFrom will eventually contain the
        // most efficient previous step.
        LinkedHashMap<PathNode, PathNode> cameFrom = new LinkedHashMap<>();

        // For each node, the cost of getting from the start node to that node.
        Map<PathNode, Float> gScore = new HashMap<>();
        for (PathNode node : nodes)
            gScore.put(node, Float.MAX_VALUE);
        // The cost of going from start to start is zero.
        gScore.put(start, 0f);

        // For each node, the total cost of getting from the start node to the goal
        // by passing by that node. That value is partly known, partly heuristic.
        Map<PathNode, Float> fScore = new HashMap<>();
        for (PathNode node : nodes)
            fScore.put(node, Float.MAX_VALUE);
        // For the first node, that value is completely heuristic.
        fScore.put(start, heuristic(start, goal));

        while (!openSet.isEmpty()) {
            // Get minimum fScore node in openSet
            PathNode current = null;
            for (PathNode n : openSet)
                if (current == null || fScore.get(n) < fScore.get(current))
                    current = n;
            if (current == goal)
                return reconstruct_path(cameFrom, goal);

            openSet.remove(current);
            closedSet.add(current);
            for (PathNode neighbor : current.getNeighbors(CharClass.ROCKETMAN)) {
                if (closedSet.contains(neighbor))
                    continue; // Ignore the neighbor which is already evaluated.
                // The distance from start to a neighbor
                float tentative_gScore = gScore.get(current) + 1;//dist_between(current, neighbor);
                if (!openSet.contains(neighbor))    // Discover a new node
                    openSet.add(neighbor);
                else if (tentative_gScore >= gScore.get(neighbor))
                    continue;        // This is not a better path.

                // This path is the best until now. Record it!
                cameFrom.put(neighbor, current);
                gScore.put(neighbor, tentative_gScore);
                fScore.put(neighbor, tentative_gScore + heuristic(neighbor, goal));
            }
        }

        System.out.println("Pathfinding failed");
        return null;
    }

    public List<Object> reconstruct_path(LinkedHashMap<PathNode, PathNode> cameFrom, PathNode goal) {
        List<Object> result = new ArrayList<>(cameFrom.values());
        result.add(goal);
        return result;
    }

//    public static void main(String[] args){
//        AI ai = new AI();
//    }

}
