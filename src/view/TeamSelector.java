package view;

import characters.Team;

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
        Font font = GUI.FONT_HEADING.deriveFont(25f);
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
        blue.setBackground(DEFAULT);
        red.setBackground(DEFAULT);
        if (selectedTeam == Team.RED)
            red.setBackground(RED_HIGHLIGHT);
        else if (selectedTeam == Team.BLUE)
            blue.setBackground(BLUE_HIGHLIGHT);
    }
}
