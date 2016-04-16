package ai;

import characters.CharClass;
import geometry.Point2f;

/**
 * Created by Nathan on 4/16/2016.
 */
public class PathTest {
    public static void main(String[] args) {
        AI ai = new AI();
        PathNode node;
        PathEdge edge;

        // Vertices
        node = new PathNode();
        node.index = 0;
        node.points.add(new Point2f(0, 0));
        ai.nodes.add(node);

        node = new PathNode();
        node.index = 1;
        node.points.add(new Point2f(1, 1));
        ai.nodes.add(node);

        node = new PathNode();
        node.index = 2;
        node.points.add(new Point2f(2, 2));
        ai.nodes.add(node);

        node = new PathNode();
        node.index = 3;
        node.points.add(new Point2f(2, 0));
        ai.nodes.add(node);

        node = new PathNode();
        node.index = 4;
        node.points.add(new Point2f(3, 2));
        ai.nodes.add(node);

        node = new PathNode();
        node.index = 5;
        node.points.add(new Point2f(4, 3));
        ai.nodes.add(node);

        // Edges
        ai.nodes.get(0).edges.get(CharClass.ROCKETMAN).add(new PathEdge(ai.nodes.get(0), ai.nodes.get(1)));
        ai.nodes.get(1).edges.get(CharClass.ROCKETMAN).add(new PathEdge(ai.nodes.get(1), ai.nodes.get(2)));
        ai.nodes.get(0).edges.get(CharClass.ROCKETMAN).add(new PathEdge(ai.nodes.get(0), ai.nodes.get(2)));
        ai.nodes.get(2).edges.get(CharClass.ROCKETMAN).add(new PathEdge(ai.nodes.get(2), ai.nodes.get(3)));
        ai.nodes.get(0).edges.get(CharClass.ROCKETMAN).add(new PathEdge(ai.nodes.get(0), ai.nodes.get(3)));
        ai.nodes.get(3).edges.get(CharClass.ROCKETMAN).add(new PathEdge(ai.nodes.get(3), ai.nodes.get(1)));
        ai.nodes.get(2).edges.get(CharClass.ROCKETMAN).add(new PathEdge(ai.nodes.get(2), ai.nodes.get(5)));
        ai.nodes.get(5).edges.get(CharClass.ROCKETMAN).add(new PathEdge(ai.nodes.get(5), ai.nodes.get(4)));

//        System.out.println("Graph has nodes:");
//        for(PathNode n : ai.nodes)
//            System.out.println("- " + n);

        // Pathfinding
        final PathNode start = ai.nodes.get(3);
        final PathNode goal = ai.nodes.get(4);
        System.out.println(ai.getPath(start, goal));
    }
}
