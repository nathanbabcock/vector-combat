package view;

import model.players.Team;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Nathan on 12/25/2015.
 */
public class TeamSelector extends JPanel {
    final Color RED_HIGHLIGHT = new Color(255, 128, 128);
    final Color BLUE_HIGHLIGHT = new Color(128, 128, 255);
    final Color DEFAULT = new Color(255, 255, 255);

    public JTextField red, blue;
    public Team selectedTeam;

    public TeamSelector() {
        // Instance vars
        selectedTeam = Team.BLUE;

        // JPanel parameters
        setOpaque(false);
        setVisible(true);
        setLayout(new GridLayout(1, 2));

        // Red button
        red = new JTextField("RED");
        red.setForeground(Color.RED);
        red.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                selectedTeam = Team.RED;
                updateHighlight();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                red.setBackground(RED_HIGHLIGHT);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                updateHighlight();
            }
        });

        // Blue button
        blue = new JTextField("BLUE");
        blue.setForeground(Color.BLUE);
        blue.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                selectedTeam = Team.BLUE;
                updateHighlight();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                blue.setBackground(BLUE_HIGHLIGHT);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                super.mouseExited(e);
                updateHighlight();
            }
        });

        // Common button settings
        Font font = new Font("Lucida Sans", Font.BOLD, 25);
        for (JTextField text : new JTextField[]{blue, red}) {
            text.setFont(font);
            text.setFocusable(false);
            text.setEditable(false);
            text.setHorizontalAlignment(SwingConstants.CENTER);
            text.setBorder(null);
            add(text);
        }

        updateHighlight();
    }

    private void updateHighlight() {
        if (selectedTeam == Team.RED) {
            red.setBackground(RED_HIGHLIGHT);
            blue.setBackground(DEFAULT);
        } else {
            blue.setBackground(BLUE_HIGHLIGHT);
            red.setBackground(DEFAULT);
        }
    }
}
