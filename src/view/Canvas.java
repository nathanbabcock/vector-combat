package view;

import ai.PathEdge;
import ai.PathNode;
import characters.Character;
import core.Game;
import core.Player;
import geometry.Point2f;
import geometry.Polygon;
import network.GameClient;
import particles.Particle;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Canvas extends JPanel {
    private Game game;
    public String clientName;

    public Scoreboard scoreboard;
    //public PauseMenu menu;

    public AffineTransform backup;
    public final int cameraMarginX = 250;
    public final int cameraMarginY = 250;
    public int cameraOffsetX;
    public int cameraOffsetY;

//    public ChatPanel chatPanel;

    public Point2f xhair;

/*    ArrayList<Float> positionGraph = new ArrayList();
    ArrayList<Float> velocityGraph = new ArrayList();
    ArrayList<Float> accelerationGraph = new ArrayList();*/

    public Canvas(Game game, String clientName) {
        this.game = game;
        this.clientName = clientName;
        xhair = new Point2f(0, 0);
        cameraOffsetX = cameraOffsetY = 0;

        scoreboard = new Scoreboard(game);
        //menu = new PauseMenu(game);
    }

//    private void layoutUI() {
//        setLayout(new BorderLayout());
//        chatPanel = new ChatPanel();
//        add(chatPanel);
//    }

    @Override
    public synchronized void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        backup = g2.getTransform();
        super.paintComponent(g);

        g2.clipRect(0, 0, getWidth(), getHeight());

        calculateCameraOffset();

        // Background
//        g2.drawImage(game.map.background, 0, 0, null);

//        if(game == null || game.map == null)
//            return;

        // Map
        if (!GameClient.devmode)
            game.map.draw(this, g2);

        // Players
        for (Player player : game.players)
            if (player.character != null) player.character.draw(this, g2, player.clientName);

        // Particles
        for (Particle particle : game.particles)
            particle.draw(this, g2);

        // Entities
        for (Polygon entity : game.entities)
            entity.draw(this, g2);

        // GUI --------------------------------

        // Scoreboard
        if (scoreboard.open) {
            Graphics2D g3 = (Graphics2D) g2.create();
            g3.translate((getWidth() - scoreboard.width) / 2, (getHeight() - scoreboard.height) / 2);
            scoreboard.draw(g3);
        }

//        if (menu.open) {
//            Graphics2D g3 = (Graphics2D) g2.create();
//            menu.draw(g3, getWidth(), getHeight());
//        }

        // DEVMODE ----
        if (GameClient.devmode && game.ai != null) {
            final int point_diam = 16;

            g2 = (Graphics2D) g.create();
            g2.translate(cameraOffsetX, getHeight() - cameraOffsetY);
            g2.scale(1, -1);

            // Draw map geometry
            g2.setColor(Color.GRAY);
            for (Polygon p : game.map.statics) {
                for (Point2f pt : p.vertices)
                    g2.fillOval((int) pt.x - point_diam / 2, (int) pt.y - point_diam / 2, point_diam, point_diam);
                g2.drawPolygon(p.getAwtPoly());
            }

            // Draw existing path nodes
            g2.setColor(Color.BLUE);
            for (PathNode pathNode : game.ai.nodes) {
                Point2f prev = null;
                for (Point2f pathPt : pathNode.points) {
                    g2.fillOval((int) pathPt.x - point_diam / 2, (int) pathPt.y - point_diam / 2, point_diam, point_diam);
                    if (prev != null)
                        g2.drawLine((int) prev.x, (int) prev.y, (int) pathPt.x, (int) pathPt.y);
                    prev = pathPt;
                }
                Color[] colors = new Color[]{Color.RED, Color.BLACK, Color.GREEN, Color.BLUE};
                int i = 0;
                for (java.util.List<PathEdge> edgeList : pathNode.edges.values()) {
                    g2.setColor(colors[i++]);
                    for (PathEdge edge : edgeList)
                        g2.drawLine((int) edge.fromPos.x, (int) edge.fromPos.y, (int) edge.toPos.x, (int) edge.toPos.y);
                }
            }

            // Draw cur path node
            if (game.ai.curNode != null) {
                g2.setColor(Color.GREEN);
                Point2f prev = null;
                for (Point2f pathPt : game.ai.curNode.points) {
                    g2.fillOval((int) pathPt.x - point_diam / 2, (int) pathPt.y - point_diam / 2, point_diam, point_diam);
                    if (prev != null)
                        g2.drawLine((int) prev.x, (int) prev.y, (int) pathPt.x, (int) pathPt.y);
                    prev = pathPt;
                }
            }

            g2.dispose();
        }
        // END DEVMODE

       /* // Physics graphs
        positionGraph.add(height - player.y);
        velocityGraph.add(height - player.velocity.magnitude());
        accelerationGraph.add(height - player.acceleration.magnitude());
        g2.setColor(Color.GREEN);
        for (int i = 0; i < positionGraph.size(); i++)
            g2.fillRect(i, positionGraph.getSprite(i).intValue(), 2, 2); // height
        g2.setColor(Color.BLUE);
        for (int i = 0; i < velocityGraph.size(); i++)
            g2.fillRect(i, velocityGraph.getSprite(i).intValue(), 2, 2); // velocity
        g2.setColor(Color.ORANGE);
        for (int i = 0; i < accelerationGraph.size(); i++)
            g2.fillRect(i, accelerationGraph.getSprite(i).intValue(), 2, 2); // acceleration*/


    /* // Draw origin
        g2.setColor(Color.RED);
        g2.drawRect(0, height - 1, 1, 1);*/
    }

    private void drawGUI(Graphics2D g2) {

    }

    private void drawUI(Graphics2D g2) {
        // Health
        int health;
        try {
            health = game.getPlayer(clientName).character.health;
        } catch (NullPointerException e) {
            health = 0;
        }
        g2.setFont(GUI.FONT_HEADING.deriveFont(50f));
        if (health > 150)
            g2.setColor(Color.GREEN);
        else if (health > 100)
            g2.setColor(Color.YELLOW);
        else if (health > 50)
            g2.setColor(Color.ORANGE);
        else
            g2.setColor(Color.RED);
        g2.drawString(health + "", getWidth() - 150, getHeight() - 40);
    }

    private void calculateCameraOffset() {
        if (game == null || game.getPlayer(clientName) == null || game.getPlayer(clientName).character == null) return;

        Character character = game.getPlayer(clientName).character;
        cameraOffsetX = (int) (-character.getCenter().x + ((getWidth() / 2) - (character.getWidth() / 2)));
        cameraOffsetY = (int) (-character.getCenter().y + ((getHeight() / 2) - (character.getHeight() / 2)));

/*

        // Horizontal
        Point2D pos = game.characters.getSprite(clientName).getBottomLeft();
        if (pos.x < cameraMarginX) {
            cameraOffsetX = 0;
            return;
        }
        if (pos.x + game.characters.getSprite(clientName).width > game.map.width - cameraMarginX) {
            cameraOffsetX = getWidth() - game.map.width;
            return;
        }

        int left = (int) pos.x + cameraOffsetX;
        if (left < cameraMarginX) {
            cameraOffsetX += cameraMarginX - left;
            return;
        } else if (left + game.characters.getSprite(clientName).width > getWidth() - cameraMarginX) {
            cameraOffsetX -= (left + game.characters.getSprite(clientName).width) - (getWidth() - cameraMarginX);
            return;
        }

        // Vertical

        if (pos.y < cameraMarginY) { // Bottom of map
            cameraOffsetY = 0;
            return;
        }
        if (pos.y + game.characters.getSprite(clientName).height > game.map.height - cameraMarginX) { // Top of map
            cameraOffsetY = getHeight() - game.map.height;
            return;
        }

        int bottom = (int) pos.y + cameraOffsetY;
        if (bottom < cameraMarginY) { // In bottom margin
            cameraOffsetY += cameraMarginY - bottom;
            return;
        } else if (bottom + game.characters.getSprite(clientName).height > getHeight() - cameraMarginY) { // In top margin
            cameraOffsetY -= (bottom + game.characters.getSprite(clientName).height) - (getHeight() - cameraMarginY);
            return;
        }
*/

    }
}
