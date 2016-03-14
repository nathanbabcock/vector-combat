package view;

import model.Game;
import model.Player;
import model.characters.Team;
import network.GameClient;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * Created by Nathan on 12/22/2015.
 */
public class ScorePanel extends JPanel {
    public boolean open;

    private static final int TEAMSIZE = 16;

    ScoreColumn blue;
    ScoreColumn red;

    Game game;

    public ScorePanel(Game game) {
        this.game = game;
        open = false;

        setVisible(false);
        setLayout(new GridLayout(1, 2, 20, 0));
        //setBackground(Color.LIGHT_GRAY);
        setOpaque(false);

        red = new ScoreColumn(Team.RED);
        add(red);

        blue = new ScoreColumn(Team.BLUE);
        add(blue);
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

    private class ScoreColumn extends JPanel {
        JPanel title, players;
        public JTextField team, score;

        static final float TEAMNAME_SIZE = 30;
        static final float SCORE_SIZE = 50;

        private ScoreColumn(Team teamID) {
            setLayout(new BorderLayout());
            setOpaque(false);

            title = new JPanel();
            title.setLayout(new BorderLayout());

            if (teamID == Team.RED) {
                title.setBackground(Color.RED);
                title.setBorder(new EmptyBorder(5, 20, 0, 10));
            } else {
                title.setBackground(Color.BLUE);
                title.setBorder(new EmptyBorder(5, 10, 0, 20));
            }

            team = new JTextField();
            team.setEditable(false);
            team.setFocusable(false);
            team.setFont(GameClient.FONT_HEADING.deriveFont(TEAMNAME_SIZE));
            team.setForeground(Color.WHITE);
            team.setOpaque(false);
            team.setBorder(null);

            if (teamID == Team.RED) {
                team.setText("RED");
                team.setHorizontalAlignment(SwingConstants.LEFT);
                title.add(team, BorderLayout.WEST);
            } else {
                team.setText("BLUE");
                team.setHorizontalAlignment(SwingConstants.RIGHT);
                title.add(team, BorderLayout.EAST);
            }

            score = new JTextField();
            score.setEditable(false);
            score.setFocusable(false);
            score.setFont(GameClient.FONT_HEADING.deriveFont(SCORE_SIZE));
            score.setForeground(Color.WHITE);
            score.setOpaque(false);
            score.setBorder(null);

            if (teamID == Team.RED) {
                score.setHorizontalAlignment(SwingConstants.RIGHT);
                title.add(score, BorderLayout.EAST);
            } else {
                score.setHorizontalAlignment(SwingConstants.LEFT);
                title.add(score, BorderLayout.WEST);
            }

            players = new JPanel();
            players.setOpaque(false);
            players.setLayout(new GridBagLayout());
            add(title, BorderLayout.NORTH);
            add(players, BorderLayout.CENTER);
        }
    }

    private void generateRow(JPanel panel, int gridy, Player player) {
        JTextField name = new JTextField();
        JTextField kills = new JTextField();
        JTextField deaths = new JTextField();
        JTextField ping = new JTextField();

        Color color = Color.GRAY;
        if (player == null)
            color = new Color(255, 255, 255, 192);
        else if (player.team == Team.RED)
            color = new Color(255, 0, 0, 192);
        else if (player.team == Team.BLUE)
            color = new Color(0, 0, 255, 192);

        for (JTextField text : new JTextField[]{name, kills, deaths, ping}) {
            if (player == null) {
                text.setFont(GameClient.FONT_BOLD.deriveFont(11f));
                text.setForeground(Color.BLACK);
            } else {
                text.setFont(GameClient.FONT_SEMIBOLD.deriveFont(14f));
                text.setForeground(Color.WHITE);
            }

            text.setEditable(false);
            text.setFocusable(false);
            text.setBorder(null);
            text.setHorizontalAlignment(SwingConstants.CENTER);
            text.setBackground(color);
        }

        GridBagConstraints c;

        // Name
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = gridy;
        c.gridwidth = 4;
        c.weightx = 0.5;
        c.weighty = 0.1;
        c.ipadx = 5;
        c.insets = new Insets(0, 0, 2, 0);
        //c.insets = new Insets(10, 10, 10, 10);
        name.setHorizontalAlignment(SwingConstants.LEFT);
        if (player == null) {
            c.fill = GridBagConstraints.HORIZONTAL;
            name.setText(" PLAYER");
        } else
            name.setText(player.clientName);
        panel.add(name, c);

        // kills
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 4;
        c.gridy = gridy;
        c.weightx = 0.5 / 4;
        c.weighty = 0.1;
        c.gridwidth = 1;
        c.ipadx = 5;
        c.insets = new Insets(0, 0, 2, 0);
        //c.insets = new Insets(10, 10, 10, 10);
        if (player == null) {
            c.fill = GridBagConstraints.HORIZONTAL;
            kills.setText("K");
        }
        else
            kills.setText(player.kills + "");
        panel.add(kills, c);

        // deaths
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 5;
        c.gridy = gridy;
        c.weightx = 0.5 / 4;
        c.weighty = 0.1;
        c.gridwidth = 1;
        c.ipadx = 5;
        c.insets = new Insets(0, 0, 2, 0);
        //c.insets = new Insets(10, 10, 10, 10);
        if (player == null) {
            c.fill = GridBagConstraints.HORIZONTAL;
            deaths.setText("D");
        }
        else
            deaths.setText(player.deaths + "");
        panel.add(deaths, c);

        // ping
        c = new GridBagConstraints();
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 6;
        c.gridy = gridy;
        c.gridwidth = 2;
        c.weightx = 0.5 / 2;
        c.weighty = 0.1;
        c.ipadx = 5;
        c.insets = new Insets(0, 0, 2, 0);
        //c.insets = new Insets(10, 10, 10, 10);
        if (player == null) {
            c.fill = GridBagConstraints.HORIZONTAL;
            ping.setText("PING");
        }
        else
            ping.setText(player.ping + "");
        panel.add(ping, c);
    }

    private void generateEmptyRow(JPanel panel, int gridy) {
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = gridy;
        c.gridwidth = 8;
        c.weighty = 0.1;
        JPanel empty = new JPanel();
        empty.setOpaque(false);
        panel.add(empty, c);
    }

    public void update() {
        red.players.removeAll();
        blue.players.removeAll();
        int redIndex = 0;
        int blueIndex = 0;

        // Header
        generateRow(red.players, redIndex++, null);
        generateRow(blue.players, blueIndex++, null);

        // Players
        for (Player player : game.players) {
            if (player.team == Team.RED)
                generateRow(red.players, redIndex++, player);
            else if (player.team == Team.BLUE)
                generateRow(blue.players, blueIndex++, player);
        }

        // Pad out to the bottom
        GridBagConstraints c;
        while (redIndex - 1 < TEAMSIZE)
            generateEmptyRow(red.players, redIndex++);
        while (blueIndex - 1 < TEAMSIZE)
            generateEmptyRow(blue.players, blueIndex++);

        // Score
        blue.score.setText(game.getScore(Team.BLUE) + "");
        red.score.setText(game.getScore(Team.RED) + "");

        revalidate();
    }
}
