package ai;

import geometry.Point2f;
import geometry.Vector2f;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 3/23/2016.
 */
public class PathEdge {
    public PathNode fromNode, toNode;
    public Point2f fromPos, toPos;
    public Vector2f fromVel;
    public List<CharState> frames;

    public PathEdge() {
        frames = new ArrayList<>();
    }

    public String toString() {
        String result = "";
        result += String.format("%6s %6s %6s %6s %6s %6s %6s %6s %6s %6s %6s %6s%n", "LEFT", "RIGHT", "UP", "DOWN", "ATK1", "ATK2", "X", "Y", "VX", "VY", "XX", "XY");
        for (CharState frame : frames)
            result += String.format("%6s %6s %6s %6s %6s %6s %6.0f %6.0f %6.0f %6.0f %6.0f %6.0f%n",
                    frame.inputState.movingLeft,
                    frame.inputState.movingRight,
                    frame.inputState.movingUp,
                    frame.inputState.movingDown,
                    frame.inputState.attacking,
                    frame.inputState.altAttacking,
                    frame.position.x,
                    frame.position.y,
                    frame.velocity.x,
                    frame.velocity.y,
                    frame.inputState.xhair.x,
                    frame.inputState.xhair.y);
        return result;
        // return "Edge[" + fromNode.index + " to " + toNode.index + " ]";
    }

//    public static List<PathEdge> getEdges(List<PathEdge> edges, int from) {
//        List<PathEdge> set = new ArrayList<>();
//        for (PathEdge edge : edges)
//            if (edge.from == from)
//                set.add(edge);
//        return set;
//    }
}
