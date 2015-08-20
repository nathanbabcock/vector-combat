import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Canvas extends JPanel {
    BufferedImage background;
    Game game;

    public Canvas(Game game) {
        this.game = game;

        try {
            background = ImageIO.read(new File("res/map.png"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        setSize(new Dimension(800, 600));
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        super.paintComponent(g);

        // Background
        g2.drawImage(background, 0, 0, null);

        // Player
        g2.setColor(new Color((int) (Math.random() * 0x1000000)));
        g2.fillRect(game.player.x, game.player.y, game.player.width, game.player.height);
    }
}
