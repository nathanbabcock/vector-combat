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

//    public static List<PathEdge> getEdges(List<PathEdge> edges, int from) {
//        List<PathEdge> set = new ArrayList<>();
//        for (PathEdge edge : edges)
//            if (edge.from == from)
//                set.add(edge);
//        return set;
//    }
}
