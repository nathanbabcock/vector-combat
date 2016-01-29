package view;

import model.characters.CharClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Nathan on 12/25/2015.
 */
public class ClassSelector extends JPanel {
    public CharClass selectedClass;
    private CharClass[] classes;
    private JTextField portraits[];

    private final Color HIGHLIGHT = new Color(219, 219, 219);
    private final Color DEFAULT = new Color(255, 255, 255);

    public ClassSelector() {
        portraits = new JTextField[]{
                new JTextField("Rocketman"),
                new JTextField("Ninja"),
                new JTextField("Soldier"),
                new JTextField("Scout")
        };
        classes = new CharClass[]{CharClass.ROCKETMAN, CharClass.NINJA, CharClass.SOLDIER, CharClass.SCOUT};

        for (int i = 0; i < classes.length; i++) {
            final CharClass newClass = classes[i];
            JTextField current = portraits[i];
            current.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedClass = newClass;
                    highlight();
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                    super.mouseEntered(e);
                    current.setBackground(HIGHLIGHT);
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    super.mouseExited(e);
                    highlight();
                }
            });
        }
        setLayout(new GridLayout(1, portraits.length));
        for (JTextField p : portraits) {
            p.setBackground(Color.WHITE);
            p.setBorder(null);
            p.setEditable(false);
            p.setFocusable(false);
            p.setHorizontalAlignment(SwingConstants.CENTER);
            add(p);
        }
        highlight();
    }

    public void highlight() {
        for (int i = 0; i < portraits.length; i++) {
            if (classes[i] == selectedClass)
                portraits[i].setBackground(HIGHLIGHT);
            else
                portraits[i].setBackground(DEFAULT);
        }
    }

/*    private class ClassPortrait extends JPanel{
        private Player character;

        public ClassPortrait(Player character) {
            this.character = character;
            character.hitbox.position = new Point2D(0, 0);
            character.xhair = new Point2D(Integer.MAX_VALUE, 0);
//            setBackground(Color.YELLOW);
        }

        @Override
        public void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            AffineTransform backup = g2.getTransform();
            super.paintComponent(g);

//            character.draw()
        }
    }*/
}
