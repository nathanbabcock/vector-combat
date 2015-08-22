package test;

import model.geometry.Point2D;
import model.geometry.Vector2D;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Nathan on 8/21/2015.
 */
public class TestVector2D extends JFrame {
    final int width = 1024;
    final int height = 768;

    public TestVector2D() {
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
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            Vector2D v = new Vector2D(100, -1000);
            Point2D start = new Point2D(500, 500);
            Point2D end = start.translate(v);
            g2.setColor(Color.GREEN);
            g2.drawLine(start.x.intValue(), start.y.intValue(), end.x.intValue(), end.y.intValue());
            g2.drawOval(end.x.intValue() - 5, end.y.intValue() - 5, 10, 10);

            Vector2D v2 = new Vector2D(100, 0);
            Point2D end2 = end.translate(v2);
            g2.setColor(Color.YELLOW);
            g2.drawLine(end.x.intValue(), end.y.intValue(), end2.x.intValue(), end2.y.intValue());
            g2.drawOval(end2.x.intValue() - 5, end2.y.intValue() - 5, 10, 10);

            Vector2D v3 = v.add(v2);
            Point2D end3 = start.translate(v3);
            g2.setColor(Color.RED);
            g2.drawLine(start.x.intValue(), start.y.intValue(), end3.x.intValue(), end3.y.intValue());
            g2.drawOval(end3.x.intValue() - 5, end3.y.intValue() - 5, 10, 10);


        }
    }

    public static void main(String[] args) {
        new TestVector2D();
    }
}
