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

    public AI() {
        nodes = new ArrayList<>();
        edges = new ArrayList<>();
        replay = new LinkedList<>();
    }

    public void update(Character character, InputState inputState) {
        if (!replay.isEmpty())
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
                    curEdge.toNode = curNode;
                    curEdge.toPos = character.getPosition();
                    curNode.edges.get(character.getCharClass()).add(curEdge);
                    edges.add(curEdge);
                    System.out.println("Saving edge from " + curEdge.fromPos + " to " + curEdge.toPos + " with " + curEdge.frames.size() + " frames.");
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

            System.out.println("Read " + nodes.size() + " path nodes from " + filename);
            //return nodes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        //return null;
    }

}
