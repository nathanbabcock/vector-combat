package model.ai;


import model.geometry.Point2f;
import model.geometry.Polygon;
import model.maps.Map;
import model.maps.ctf_space;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Nathan on 3/22/2016.
 */
public class NodeGen extends JFrame {
    private Map map;

    private PathNode curNode;
    private List<PathNode> nodes;

    private int offsetX, offsetY;
    private final int step = 20;
    private final int point_diam = 16;

    public NodeGen(Map map) {
        this.map = map;
        nodes = new ArrayList<>();
        curNode = new PathNode();

        setVisible(true);
        setSize(1000, 800);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        setupListeners();
    }

    private void setupListeners() {
        InputMap im = ((JPanel) getContentPane()).getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = ((JPanel) getContentPane()).getActionMap();

        // RIGHT pressed
        Action rightPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                offsetX -= step;
                repaint();
            }
        };
        am.put("rightPressed", rightPressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "rightPressed");
        // LEFT pressed
        Action leftPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                offsetX += step;
                repaint();
            }
        };
        am.put("leftPressed", leftPressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "leftPressed");

        // UP pressed
        Action upPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                offsetY -= step;
                repaint();
            }
        };
        am.put("upPressed", upPressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "upPressed");

        // DOWN pressed
        Action downPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                offsetY += step;
                repaint();
            }
        };
        am.put("downPressed", downPressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "downPressed");

        // ESC pressed
        Action escPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                curNode = new PathNode();
                repaint();
            }
        };
        am.put("escPressed", escPressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "escPressed");

        // ENTER pressed
        Action enterPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nodes.add(curNode);
                curNode = new PathNode();
                repaint();
            }
        };
        am.put("enterPressed", enterPressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "enterPressed");

        // P pressed
        Action pPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                PathNode.writeNodes(nodes, JOptionPane.showInputDialog(NodeGen.this, "Enter a filename to save node data to:"));
            }
        };
        am.put("pPressed", pPressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_P, 0, false), "pPressed");

        // P pressed
        Action rPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nodes = PathNode.readNodes(JOptionPane.showInputDialog(NodeGen.this, "Enter a filename to read node data from:"));
                repaint();
            }
        };
        am.put("rPressed", rPressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_R, 0, false), "rPressed");

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                Point2f realP = new Point2f(p.x - offsetX, getHeight() - offsetY - p.y);

                for (Polygon poly : map.statics) {
                    for (Point2f pt : poly.vertices) {
                        if (pt.distance(realP) <= point_diam / 2) {
                            curNode.points.add(pt.copy());
                            repaint();
                            return;
                        }
                    }
                }

                // else
                curNode = closestNode(realP);
                if (curNode == null) curNode = new PathNode();
                repaint();
            }
        });

    }


    @Override
    public void paint(Graphics g) {
        super.paintComponents(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.translate(offsetX, getHeight() - offsetY);
        g2.scale(1, -1);

        // Draw map geometry
        g2.setColor(Color.GRAY);
        for (Polygon p : map.statics) {
            for (Point2f pt : p.vertices)
                g2.fillOval((int) pt.x - point_diam / 2, (int) pt.y - point_diam / 2, point_diam, point_diam);
            g2.drawPolygon(p.getAwtPoly());
        }

        // Draw existing path nodes
        g2.setColor(Color.BLUE);
        for (PathNode pathNode : nodes) {
            Point2f prev = null;
            for (Point2f pathPt : pathNode.points) {
                g2.fillOval((int) pathPt.x - point_diam / 2, (int) pathPt.y - point_diam / 2, point_diam, point_diam);
                if (prev != null)
                    g2.drawLine((int) prev.x, (int) prev.y, (int) pathPt.x, (int) pathPt.y);
                prev = pathPt;
            }
        }

        // Draw cur path node
        g2.setColor(Color.GREEN);
        Point2f prev = null;
        for (Point2f pathPt : curNode.points) {
            g2.fillOval((int) pathPt.x - point_diam / 2, (int) pathPt.y - point_diam / 2, point_diam, point_diam);
            if (prev != null)
                g2.drawLine((int) prev.x, (int) prev.y, (int) pathPt.x, (int) pathPt.y);
            prev = pathPt;
        }

        g2.dispose();
    }

    public PathNode closestNode(Point2f p) {
        float minDist = Float.MAX_VALUE;
        PathNode minNode = null;
        for (PathNode pathNode : nodes) {
            if (p.x < pathNode.minX() || p.x > pathNode.maxX()) continue;
            float y = pathNode.getPoint(p.x).y;
            if (y > p.y) continue;
            final float dist = Math.abs(p.y - y);
            if (dist < minDist) {
                minDist = dist;
                minNode = pathNode;
            }
        }

        return minNode;
    }

    public static void main(String[] args) {
        new NodeGen(new ctf_space());
        //PathNode.readNodes("nodes");
    }
}
