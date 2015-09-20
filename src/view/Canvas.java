package view;

import model.Game;
import model.entities.Entity;
import model.geometry.AABB;
import model.geometry.Point2D;
import model.particles.Particle;
import model.players.Player;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.Map;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Canvas extends JPanel {
    private Game game;
    public String clientName;

    public AffineTransform backup;
    public final int cameraMarginX = 250;
    public final int cameraMarginY = 250;
    public int cameraOffsetX;
    public int cameraOffsetY;

    public Point2D xhair;

/*    ArrayList<Float> positionGraph = new ArrayList();
    ArrayList<Float> velocityGraph = new ArrayList();
    ArrayList<Float> accelerationGraph = new ArrayList();*/

    public Canvas(Game game, String clientName) {
        this.game = game;
        this.clientName = clientName;
        xhair = new Point2D(0, 0);

        cameraOffsetX = cameraOffsetY = 0;

        layoutUI();
    }

    private void layoutUI() {
        setLayout(new BorderLayout());

        // Container
        JPanel chatContainer = new JPanel();
        chatContainer.setLayout(new BorderLayout());
        chatContainer.setOpaque(false);
        chatContainer.setBackground(Color.BLUE);

        // Chat Panel
        JPanel chatPanel = new JPanel();
        chatPanel.setOpaque(false);
        chatPanel.setBackground(Color.RED);
        chatPanel.setSize(new Dimension(getWidth() / 2, getHeight() / 2));
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.PAGE_AXIS));
        chatPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Text Area
        JTextArea textArea = new JTextArea("excalo: GLHF NOOBS\nDankJr: Lel\nNobato: Get rekd fagit");
        textArea.setColumns(25);
        textArea.setLineWrap(true);
        textArea.setOpaque(false);
        textArea.setFont(new Font("Lucida Sans", Font.PLAIN, 12));
        textArea.setEditable(false);
        textArea.setFocusable(false);
        chatPanel.add(textArea);

        // Text Field
        JTextField textField = new JTextField();
        textField.setColumns(25);
        textField.setOpaque(false);
        textField.setFont(new Font("Lucida Sans", Font.PLAIN, 12));
        textField.setBorder(null);
        chatPanel.add(textField);

        chatContainer.add(chatPanel, BorderLayout.WEST);
        add(chatContainer, BorderLayout.SOUTH);
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        backup = g2.getTransform();
        super.paintComponent(g);

        calculateCameraOffset();

        // Background
//        g2.drawImage(game.map.background, 0, 0, null);

        // Boundaries
        g2.setColor(Color.black);
        for (AABB b : game.map.statics)
            g2.fillRect((int) b.getBottomLeft().x + cameraOffsetX, (int) (getHeight() - cameraOffsetY - b.getBottomLeft().y - b.height), (int) (b.width), (int) (b.height));

        // Players
        for (Map.Entry<String, Player> entry : game.players.entrySet())
            entry.getValue().draw(this, g2, entry.getKey());

        // Particles
        for (Particle particle : game.particles)
            particle.draw(this, g2);

        // Entities
        for (Entity entity : game.entities.values())
            entity.draw(this, g2);

        // UI
        drawUI(g2);

       /* // Physics graphs
        positionGraph.add(HEIGHT - player.y);
        velocityGraph.add(HEIGHT - player.velocity.magnitude());
        accelerationGraph.add(HEIGHT - player.acceleration.magnitude());
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
        g2.drawRect(0, HEIGHT - 1, 1, 1);*/
    }

    private void drawUI(Graphics2D g2) {
        // Health
        int health;
        try {
            health = game.players.get(clientName).health;
        } catch (NullPointerException e) {
            health = 0;
        }
        g2.setFont(new Font("Lucida Sans", Font.BOLD, 50));
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
        if (game.players.get(clientName) == null) return;

        Player player = game.players.get(clientName);
        cameraOffsetX = (int) (-player.getCenter().x + ((getWidth() / 2) - (player.width / 2)));
        cameraOffsetY = (int) (-player.getCenter().y + ((getHeight() / 2) - (player.height / 2)));

/*

        // Horizontal
        Point2D pos = game.players.get(clientName).getBottomLeft();
        if (pos.x < cameraMarginX) {
            cameraOffsetX = 0;
            return;
        }
        if (pos.x + game.players.get(clientName).width > game.map.WIDTH - cameraMarginX) {
            cameraOffsetX = getWidth() - game.map.WIDTH;
            return;
        }

        int left = (int) pos.x + cameraOffsetX;
        if (left < cameraMarginX) {
            cameraOffsetX += cameraMarginX - left;
            return;
        } else if (left + game.players.get(clientName).width > getWidth() - cameraMarginX) {
            cameraOffsetX -= (left + game.players.get(clientName).width) - (getWidth() - cameraMarginX);
            return;
        }

        // Vertical

        if (pos.y < cameraMarginY) { // Bottom of map
            cameraOffsetY = 0;
            return;
        }
        if (pos.y + game.players.get(clientName).height > game.map.HEIGHT - cameraMarginX) { // Top of map
            cameraOffsetY = getHeight() - game.map.HEIGHT;
            return;
        }

        int bottom = (int) pos.y + cameraOffsetY;
        if (bottom < cameraMarginY) { // In bottom margin
            cameraOffsetY += cameraMarginY - bottom;
            return;
        } else if (bottom + game.players.get(clientName).height > getHeight() - cameraMarginY) { // In top margin
            cameraOffsetY -= (bottom + game.players.get(clientName).height) - (getHeight() - cameraMarginY);
            return;
        }
*/

    }
}
