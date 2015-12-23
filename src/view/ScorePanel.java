package view;

import model.Game;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Nathan on 12/22/2015.
 */
public class ScorePanel extends JPanel {
    public boolean open;

    JPanel bluePlayers;
    JPanel redPlayers;


    Game game;

    public ScorePanel(Game game) {
        this.game = game;
        open = false;

        setVisible(false);
        setLayout(new GridLayout(1, 2));
        setBackground(Color.YELLOW);
        JPanel blue = new JPanel();
        blue.setLayout(new BorderLayout());
        JTextField blueHeader = new JTextField("Blue");
        blueHeader.setEditable(false);
        blueHeader.setFocusable(false);
        blueHeader.setFont(new Font("Lucida Sans", Font.BOLD, 20));
        blueHeader.setForeground(Color.BLUE);
        bluePlayers = new JPanel();
        bluePlayers.setLayout(new GridLayout(9, 1));
        blue.add(blueHeader, BorderLayout.NORTH);
        blue.add(bluePlayers, BorderLayout.CENTER);
        add(blue, BorderLayout.WEST);

        JPanel red = new JPanel();
        red.setLayout(new BorderLayout());
        JTextField redHeader = new JTextField("Red");
        redHeader.setEditable(false);
        redHeader.setFocusable(false);
        redHeader.setFont(new Font("Lucida Sans", Font.BOLD, 20));
        redHeader.setForeground(Color.RED);
        redPlayers = new JPanel();
        redPlayers.setLayout(new GridLayout(9, 1));
        red.add(redHeader, BorderLayout.NORTH);
        red.add(redPlayers, BorderLayout.CENTER);
        add(red, BorderLayout.EAST);

//        update();
    }

    public void open() {
        open = true;
        setVisible(true);
        update();
    }

    public void close() {
        open = false;
        setVisible(false);
    }

    public void update() {
        bluePlayers.removeAll();
        redPlayers.removeAll();
        bluePlayers.setLayout(new GridLayout(8, 3));
        bluePlayers.add(new JLabel("Player"));
        bluePlayers.add(new JLabel("Kills"));
        bluePlayers.add(new JLabel("Deaths"));

        bluePlayers.add(new JLabel("Player"));
        bluePlayers.add(new JLabel("Kills"));
        bluePlayers.add(new JLabel("Deaths"));

        bluePlayers.add(new JLabel("Player"));
        bluePlayers.add(new JLabel("Kills"));
        bluePlayers.add(new JLabel("Deaths"));

        bluePlayers.add(new JLabel("Player"));
        bluePlayers.add(new JLabel("Kills"));
        bluePlayers.add(new JLabel("Deaths"));

        bluePlayers.add(new JLabel("Player"));
        bluePlayers.add(new JLabel("Kills"));
        bluePlayers.add(new JLabel("Deaths"));

        bluePlayers.add(new JLabel("Player"));
        bluePlayers.add(new JLabel("Kills"));
        bluePlayers.add(new JLabel("Deaths"));

        bluePlayers.add(new JLabel("Player"));
        bluePlayers.add(new JLabel("Kills"));
        bluePlayers.add(new JLabel("Deaths"));

        bluePlayers.add(new JLabel("Player"));
        bluePlayers.add(new JLabel("Kills"));
        bluePlayers.add(new JLabel("Deaths"));
    }
}
