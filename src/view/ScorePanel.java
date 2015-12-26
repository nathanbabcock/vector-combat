package view;

import model.Game;
import model.players.Player;
import model.players.Team;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.util.Map;

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
        blue.title.setForeground(Color.BLUE);
        blue.title.setText("Blue");
        add(blue, BorderLayout.WEST);

        red = new ScoreColumn();
        red.title.setForeground(Color.RED);
        red.title.setText("Red");
        add(red, BorderLayout.EAST);

        update();
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
        JTextField title;
        JPanel players;

        private ScoreColumn() {
            setLayout(new BorderLayout());
            setOpaque(false);

            title = new JTextField();
            title.setEditable(false);
            title.setFocusable(false);
            title.setFont(new Font("Lucida Sans", Font.BOLD, 20));
            title.setHorizontalAlignment(SwingConstants.CENTER);
            title.setOpaque(false);
            title.setBorder(null);

            players = new JPanel();
            players.setOpaque(false);
            players.setLayout(new GridLayout(TEAMSIZE + 1, 1));
            add(title, BorderLayout.NORTH);
            add(players, BorderLayout.CENTER);
        }
    }

    private class ScoreRow extends JPanel {
        JTextField name, kills, deaths;

        public ScoreRow() {
            setOpaque(false);
            setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
            name = new JTextField();
            kills = new JTextField();
            deaths = new JTextField();
            for (JTextField text : new JTextField[]{name, kills, deaths}) {
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
        System.out.println("Updating score");

        // Header
        for (JPanel players : new JPanel[]{blue.players, red.players}) {
            players.removeAll();
            ScoreRow header = new ScoreRow();
            header.name.setText("Player");
            header.kills.setText("Kills");
            header.deaths.setText("Deaths");
            for (JTextComponent text : new JTextComponent[]{header.name, header.kills, header.deaths}) {
                text.setFont(new Font("Lucida Sans", Font.BOLD, 12));
            }
            players.add(header);
        }

        for (Map.Entry<String, Player> entry : game.players.entrySet()) {
            ScoreRow row = new ScoreRow();
            String name = entry.getKey();
            Player player = entry.getValue();
            row.name.setText(name);
            row.kills.setText(player.kills + "");
            row.deaths.setText(player.deaths + "");
            if (player.team == Team.BLUE)
                blue.players.add(row);
            else
                red.players.add(row);
        }

        revalidate();
    }
}
