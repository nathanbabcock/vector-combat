package test;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Nathan on 8/21/2015.
 */
public class TestCollisionDetection extends JFrame {
    final int width = 1024;
    final int height = 768;

    public TestCollisionDetection() {
        // Layout
        setSize(new Dimension(width, height));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        add(new VectorCanvas());
    }

    private class VectorCanvas extends JPanel {
        public VectorCanvas() {
            setSize(width, height);
        }

        @Override
        public void paintComponent(Graphics g) {/*
            Graphics2D g2 = (Graphics2D) g;

            // Init
            Map map = new Map();
            Player player = new Player();
            player.x = 700f;
            player.y = 375f;
            player.width = 25f;
            player.height = 50f;
            ArrayList<Line2D> boundaries = new ArrayList<>();
            Random rand = new Random();
            boundaries.add(new Line2D(300, 400, 600, 400));
            boundaries.add(new Line2D(300, 400, 300, 500));

            // Boundaries
            g2.setColor(Color.MAGENTA);
            for (Line2D b : boundaries)
                g2.drawLine(b.a.x.intValue(), height - b.a.y.intValue(), b.b.x.intValue(), height - b.b.y.intValue());

            // Draw original position
            g2.setColor(Color.GREEN);
            g2.fillRect(player.x.intValue(), height - player.y.intValue() - player.height.intValue(), player.width.intValue(), player.height.intValue());

            // Draw collided position
            Vector2D displacement = new Vector2D(-220, 0);
            Rect2D oldRect = player.getRect();
            player.setPos(player.getPos().translate(displacement));

            // Draw original position
            g2.setColor(Color.RED);
            g2.drawRect(player.x.intValue(), height - player.y.intValue() - player.height.intValue(), player.width.intValue(), player.height.intValue());

            // Draw displacement vector
            g2.setColor(Color.GRAY);
            g2.drawLine(oldRect.center().x.intValue(), height - oldRect.center().y.intValue(), player.center().x.intValue(), height - player.center().y.intValue());

            // Detect edge collisions
            Point2D intersection = null;
            Line2D boundary = null;
            Line2D edge = null;
            for (Line2D b : boundaries) {
                int i = 0;
                for (Line2D e : player.getEdges()) {
                    intersection = b.intersection(e);
                    if (intersection != null) {
                        boundary = b;
                        edge = e;
                        break;
                    }
                    i++;
                }

            }

            if (intersection == null)
                return;

            g2.setColor(Color.BLACK);
            g2.drawOval(intersection.x.intValue() - 2, height - intersection.y.intValue() - 2, 4, 4);

            // Pick closest vertex
            // TODO generalize to any direction/edge
            Point2D closestVertex = null;
            if (displacement.x < 0 || displacement.y < 0) {
                if (boundary.slope() <= edge.slope())
                    closestVertex = oldRect.bottomLeft(); //originalSide.a;
                else// if (boundary.slope() > edge.slope())
                    closestVertex = oldRect.topLeft(); //originalSide.b;
            } else {//if (displacement.x > 0 || displacement.y > 0) {
                if (boundary.slope() >= edge.slope())
                    closestVertex = oldRect.bottomRight(); //originalSide.a;
                else// if (boundary.slope() > edge.slope())
                    closestVertex = oldRect.topRight(); //originalSide.b;
            }

            g2.drawOval(closestVertex.x.intValue() - 2, height - closestVertex.y.intValue() - 2, 4, 4);

//            float slope = displacement.y / displacement.x;
            Line2D movedDisplacement = new Line2D(closestVertex, closestVertex.translate(displacement));
            Point2D newIntersection = movedDisplacement.intersection(boundary);
            Line2D newDisplacement = new Line2D(closestVertex, newIntersection);

            g2.setColor(Color.BLACK);
            g2.drawLine(newDisplacement.a.x.intValue(), height - newDisplacement.a.y.intValue(), newDisplacement.b.x.intValue(), height - newDisplacement.b.y.intValue());

            // Move player out of collision
            player.setPos(oldRect.bottomLeft().translate(newDisplacement.toVector()));
            g2.setColor(Color.GREEN);
            g2.drawRect(player.x.intValue(), height - player.y.intValue() - player.height.intValue(), player.width.intValue(), player.height.intValue());
*/
        }
    }

    public static void main(String[] args) {
        new TestCollisionDetection();
    }
}
