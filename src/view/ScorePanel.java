package view;

import model.Game;
import model.Player;
import model.characters.Team;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;

/**
 * Created by Nathan on 12/22/2015.
 */
public class ScorePanel extends JPanel {
    public boolean open;

    private static final int TEAMSIZE = 8;

    ScoreColumn blue;
    ScoreColumn red;

    Game game;

    public ScorePanel(Game game) {
        this.game = game;
        open = false;

        setVisible(false);
        setLayout(new GridLayout(1, 2));
        setBackground(Color.LIGHT_GRAY);

        blue = new ScoreColumn();
        blue.team.setForeground(Color.BLUE);
        blue.team.setText("Blue");
        blue.score.setForeground(Color.BLUE);
        add(blue, BorderLayout.WEST);

        red = new ScoreColumn();
        red.team.setForeground(Color.RED);
        red.team.setText("Red");
        red.score.setForeground(Color.RED);
        add(red, BorderLayout.EAST);
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

        private ScoreColumn() {
            setLayout(new BorderLayout());
            setOpaque(false);

            title = new JPanel();
            title.setLayout(new BorderLayout());
            title.setOpaque(false);
            title.setBorder(new EmptyBorder(10, 10, 10, 10));

            team = new JTextField();
            team.setEditable(false);
            team.setFocusable(false);
            team.setFont(new Font("Lucida Sans", Font.BOLD, 20));
            team.setHorizontalAlignment(SwingConstants.LEFT);
            team.setOpaque(false);
            team.setBorder(null);
            title.add(team, BorderLayout.WEST);

            score = new JTextField();
            score.setEditable(false);
            score.setFocusable(false);
            score.setFont(new Font("Lucida Sans", Font.BOLD, 20));
            score.setHorizontalAlignment(SwingConstants.RIGHT);
            score.setOpaque(false);
            score.setBorder(null);
            title.add(score, BorderLayout.EAST);

            players = new JPanel();
            players.setOpaque(false);
            players.setLayout(new GridLayout(TEAMSIZE + 1, 1));
            add(title, BorderLayout.NORTH);
            add(players, BorderLayout.CENTER);
        }
    }

    private class ScoreRow extends JPanel {
        JTextField name, kills, deaths, ping;

        public ScoreRow() {
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            name = new JTextField();
            kills = new JTextField();
            deaths = new JTextField();
            ping = new JTextField();
            for (JTextField text : new JTextField[]{name, kills, deaths, ping}) {
                text.setEditable(false);
                text.setFocusable(false);
                text.setOpaque(false);
                text.setBorder(null);
                text.setHorizontalAlignment(SwingConstants.CENTER);
                text.setFont(new Font("Lucida Sans", Font.PLAIN, 12));
                add(text);
            }
        }
    }

    public void update() {
//        System.out.println("Updating score");

        // Header
        for (JPanel players : new JPanel[]{blue.players, red.players}) {
            players.removeAll();
            ScoreRow header = new ScoreRow();
            header.name.setText("Player");
            header.kills.setText("Kills");
            header.deaths.setText("Deaths");
            header.ping.setText("Ping");
            for (JTextComponent text : new JTextComponent[]{header.name, header.kills, header.deaths, header.ping})
                text.setFont(new Font("Lucida Sans", Font.BOLD, 12));
            players.add(header);
        }

        for (Player player : game.players) {
            ScoreRow row = new ScoreRow();
            String name = player.clientName;
            row.name.setText(name);
            row.kills.setText(player.kills + "");
            row.deaths.setText(player.deaths + "");
            row.ping.setText(player.ping + "");
            if (player.team == Team.BLUE)
                blue.players.add(row);
            else if (player.team == Team.RED)
                red.players.add(row);
        }

        blue.score.setText(game.getScore(Team.BLUE) + "");
        red.score.setText(game.getScore(Team.RED) + "");

        revalidate();
    }
}
