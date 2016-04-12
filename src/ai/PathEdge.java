package ai;

import geometry.Point2f;
import geometry.Vector2f;
import network.InputState;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 3/23/2016.
 */
public class PathEdge {
    public PathNode fromNode, toNode;
    public Point2f fromPos, toPos;
    public Vector2f fromVel;
    public List<InputState> frames;

    public PathEdge() {
        frames = new ArrayList<>();
    }

    public String toString() {
//        String result = "";
//        result += String.format("%6s %6s %6s %6s %6s %6s %6s %6s%n", "LEFT", "RIGHT", "UP", "DOWN", "ATK1", "ATK2", "X", "Y");
//        for (InputState frame : frames)
//            result += String.format("%6s %6s %6s %6s %6s %6s %6.0f %6.0f%n", frame.movingLeft, frame.movingRight, frame.movingUp, frame.movingDown, frame.attacking, frame.altAttacking, frame.xhair.x, frame.xhair.y);
//        return result;
        return "Edge[" + fromNode.index + " to " + toNode.index + " ]";
    }

//    public static List<PathEdge> getEdges(List<PathEdge> edges, int from) {
//        List<PathEdge> set = new ArrayList<>();
//        for (PathEdge edge : edges)
//            if (edge.from == from)
//                set.add(edge);
//        return set;
//    }
}
