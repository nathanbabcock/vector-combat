package view;

import javax.swing.*;
import java.awt.*;

/**
 * Created by Nathan on 12/22/2015.
 */
public class MenuPanel extends JPanel {
    public ClassSelector classSelector;
    public TeamSelector teamSelector;
    public boolean open;

    public MenuPanel() {
        setBackground(Color.GRAY);
        open = false;

        setVisible(false);
        setLayout(new GridLayout(2, 1));
        classSelector = new ClassSelector();
        teamSelector = new TeamSelector();
        add(classSelector);
        add(teamSelector);

    }

    public void open() {
//        System.out.println("opening menu");
        open = true;
        setVisible(true);
    }

    public void close() {
//        System.out.println("closing menu");
        open = false;
        setVisible(false);
    }
}
