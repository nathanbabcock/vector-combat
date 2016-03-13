package view;

import model.Player;
import model.characters.*;
import model.characters.Character;
import model.geometry.Point2f;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Created by Nathan on 12/25/2015.
 */
public class ClassSelector extends JPanel {
    public CharClass selectedClass;
    static private CharClass[] classes = new CharClass[]{CharClass.ROCKETMAN, CharClass.NINJA, CharClass.COMMANDO, CharClass.SCOUT};
    private ClassPortrait portraits[];
    private final Color HIGHLIGHT = new Color(219, 219, 219);
    private final Color DEFAULT = new Color(255, 255, 255);

    public ClassSelector() {
        portraits = new ClassPortrait[]{
                new ClassPortrait(new Rocketman(new Player())),
                new ClassPortrait(new Ninja(new Player())),
                new ClassPortrait(new Commando(new Player())),
                new ClassPortrait(new Scout(new Player()))
        };


        for (int i = 0; i < classes.length; i++) {
            final CharClass newClass = classes[i];
            ClassPortrait current = portraits[i];
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
        for (ClassPortrait p : portraits) {
            p.setBackground(Color.WHITE);
            p.setBorder(null);
            //p.setEditable(false);
            p.setFocusable(false);
            //p.setHorizontalAlignment(SwingConstants.CENTER);
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

    private class ClassPortrait extends JPanel {
        final int width = 500 / classes.length;
        final int height = 300 / 2;

        private Character character;

        public String getName() {
            if (character instanceof Rocketman)
                return "Rocketman";
            if (character instanceof Ninja)
                return "Ninja";
            if (character instanceof Commando)
                return "Commando";
            if (character instanceof Scout)
                return "Scout";
            return null;
        }

        void setTeam(Team team) {
            character.player.team = team;
            repaint();
        }

        public ClassPortrait(Character character) {
            this.character = character;
            character.player.team = Team.RED;
            character.xhair = new Point2f(Integer.MAX_VALUE, 0);
            character.position.x = (width - character.width) / 2; // TODO the 500 is the hardcoded pause menu width
            character.position.y = (height - character.height) / 2;
            character.onGround = true;
            character.updateSprite(0);

            System.out.println("class thing width = " + getWidth());

            setLayout(new BorderLayout());
            JTextField name = new JTextField(getName());
            name.setOpaque(false);
            name.setHorizontalAlignment(SwingConstants.CENTER);
            name.setBorder(new EmptyBorder(0, 0, 10, 0));
            name.setCursor(Cursor.getDefaultCursor());
            add(name, BorderLayout.SOUTH);
            //character.hitbox.position = new Point2f(0, 0);
            //character.xhair = new Point2D(Integer.MAX_VALUE, 0);
//            setBackground(Color.YELLOW);
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.translate(character.getBottomLeft().x, getHeight() - character.getBottomLeft().y);
            character.draw(g2);
        }
    }
}
