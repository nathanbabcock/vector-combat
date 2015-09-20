package view;

import model.Game;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Nathan on 9/19/2015.
 */
public class UI extends JPanel {
    Game game;
    String clientName;

    public UI(Game game, String clientName) {
        this.game = game;
        this.clientName = clientName;

        setLayout(null);

        JTextArea chatPanel = new JTextArea("hello world!");
        chatPanel.setBackground(Color.RED);
        chatPanel.setBounds(0, getHeight() / 2, getWidth() / 2, getHeight() / 2);
        add(chatPanel);

        setVisible(false);
    }
}
