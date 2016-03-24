package ai;

import geometry.Point2f;
import geometry.Vector2f;
import network.InputState;

import java.util.List;

/**
 * Created by Nathan on 3/23/2016.
 */
public class PathEdge {
    public Point2f fromPos;
    public Vector2f fromVel;
    public int fromNode;
    public int toNode;
    public List<InputState> frames;

//    public static List<PathEdge> getEdges(List<PathEdge> edges, int from) {
//        List<PathEdge> set = new ArrayList<>();
//        for (PathEdge edge : edges)
//            if (edge.from == from)
//                set.add(edge);
//        return set;
//    }
}
