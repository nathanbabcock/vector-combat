package view;

import model.Game;
import model.Player;
import model.characters.Character;
import model.entities.Entity;
import model.geometry.AABB;
import model.geometry.Point2f;
import model.particles.Particle;
import network.GameClient;

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

        calculateCameraOffset();

        // Background
//        g2.drawImage(game.map.background, 0, 0, null);

//        if(game == null || game.map == null)
//            return;

        // Boundaries
        g2.setColor(Color.black);
        for (AABB b : game.map.statics)
            g2.fillRect((int) b.getBottomLeft().x + cameraOffsetX, (int) (getHeight() - cameraOffsetY - b.getBottomLeft().y - b.height), (int) (b.width), (int) (b.height));

        // Players
        for (Player player : game.players)
            if (player.character != null) player.character.draw(this, g2, player.clientName);

        // Particles
        for (Particle particle : game.particles)
            particle.draw(this, g2);

        // Entities
        for (Entity entity : game.entities)
            entity.draw(this, g2);

        // UI
        drawGUI(g2);

       /* // Physics graphs
        positionGraph.add(height - player.y);
        velocityGraph.add(height - player.velocity.magnitude());
        accelerationGraph.add(height - player.acceleration.magnitude());
        g2.setColor(Color.GREEN);
        for (int i = 0; i < positionGraph.size(); i++)
            g2.fillRect(i, positionGraph.get(i).intValue(), 2, 2); // height
        g2.setColor(Color.BLUE);
        for (int i = 0; i < velocityGraph.size(); i++)
            g2.fillRect(i, velocityGraph.get(i).intValue(), 2, 2); // velocity
        g2.setColor(Color.ORANGE);
        for (int i = 0; i < accelerationGraph.size(); i++)
            g2.fillRect(i, accelerationGraph.get(i).intValue(), 2, 2); // acceleration*/


    /* // Draw origin
        g2.setColor(Color.RED);
        g2.drawRect(0, height - 1, 1, 1);*/
    }

    private void drawGUI(Graphics2D g2) {
        if (scoreboard.open) {
            g2 = (Graphics2D) g2.create();
            g2.translate((getWidth() - scoreboard.width) / 2, (getHeight() - scoreboard.height) / 2);
            scoreboard.draw(g2);
        }
    }

    private void drawUI(Graphics2D g2) {
        // Health
        int health;
        try {
            health = game.getPlayer(clientName).character.health;
        } catch (NullPointerException e) {
            health = 0;
        }
        g2.setFont(GameClient.FONT_HEADING.deriveFont(50f));
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
        cameraOffsetX = (int) (-character.getCenter().x + ((getWidth() / 2) - (character.width / 2)));
        cameraOffsetY = (int) (-character.getCenter().y + ((getHeight() / 2) - (character.height / 2)));

/*

        // Horizontal
        Point2D pos = game.characters.get(clientName).getBottomLeft();
        if (pos.x < cameraMarginX) {
            cameraOffsetX = 0;
            return;
        }
        if (pos.x + game.characters.get(clientName).width > game.map.width - cameraMarginX) {
            cameraOffsetX = getWidth() - game.map.width;
            return;
        }

        int left = (int) pos.x + cameraOffsetX;
        if (left < cameraMarginX) {
            cameraOffsetX += cameraMarginX - left;
            return;
        } else if (left + game.characters.get(clientName).width > getWidth() - cameraMarginX) {
            cameraOffsetX -= (left + game.characters.get(clientName).width) - (getWidth() - cameraMarginX);
            return;
        }

        // Vertical

        if (pos.y < cameraMarginY) { // Bottom of map
            cameraOffsetY = 0;
            return;
        }
        if (pos.y + game.characters.get(clientName).height > game.map.height - cameraMarginX) { // Top of map
            cameraOffsetY = getHeight() - game.map.height;
            return;
        }

        int bottom = (int) pos.y + cameraOffsetY;
        if (bottom < cameraMarginY) { // In bottom margin
            cameraOffsetY += cameraMarginY - bottom;
            return;
        } else if (bottom + game.characters.get(clientName).height > getHeight() - cameraMarginY) { // In top margin
            cameraOffsetY -= (bottom + game.characters.get(clientName).height) - (getHeight() - cameraMarginY);
            return;
        }
*/

    }
}
