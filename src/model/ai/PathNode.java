package model.ai;

import model.geometry.Point2f;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by Nathan on 3/22/2016.
 */
public class PathNode {
    public List<Point2f> points;

    public PathNode() {
        points = new ArrayList<>();
    }

    public float minX() {
        return points.get(0).x;
    }

    public float maxX() {
        return points.get(points.size() - 1).x;
    }

    public Point2f getPoint(float x) {
        Point2f start, end, prev;
        start = end = prev = null;
        for (Point2f p : points) {
            if (p.x >= x) {
                end = p;
                start = prev;
                break;
            }
            prev = p;
        }

        if (start == null || end == null) {
            System.err.println("Could not find x coordinate " + x + " in PathNode.");
            return null;
        }

        return new Point2f(x, ((end.y - start.y) / (end.x - start.x)) * (x - start.x) + start.y);
    }

    static void writeNodes(List<PathNode> list, String file) {
        try (Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"))) {
            for (PathNode node : list) {
                for (Point2f p : node.points)
                    writer.write(p.x + " " + p.y + " ");
                writer.write("\n");
            }
            writer.close();
            System.out.println("Wrote " + list.size() + " path nodes to " + file);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    static List<PathNode> readNodes(String filename) {
        List<PathNode> nodes = new ArrayList<>();
        File file = new File(filename);
        try {
            Scanner lineScanner = new Scanner(file);
            while (lineScanner.hasNextLine()) {
                String line = lineScanner.nextLine();
                Scanner scanner = new Scanner(line);
                PathNode curNode = new PathNode();
                while (scanner.hasNextFloat())
                    curNode.points.add(new Point2f(scanner.nextFloat(), scanner.nextFloat()));
                if (curNode.points.size() > 0)
                    nodes.add(curNode);
            }
            System.out.println("Read " + nodes.size() + " path nodes from " + filename);
            return nodes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
