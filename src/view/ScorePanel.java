package view;

import model.Game;
import model.Player;
import model.characters.Team;
import network.GameClient;

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
                text.setFont(GameClient.FONT_TEXT);
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
            header.name.setText("PLAYER");
            header.kills.setText("K");
            header.deaths.setText("D");
            header.ping.setText("PING");
            for (JTextComponent text : new JTextComponent[]{header.name, header.kills, header.deaths, header.ping})
                text.setFont(GameClient.FONT_TEXT.deriveFont(11f));
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
