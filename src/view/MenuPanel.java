package view;

import model.Player;
import model.characters.*;
import model.characters.Character;
import model.geometry.Point2f;
import network.GameClient;
import network.SpawnParams;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * Created by Nathan on 12/22/2015.
 */
public class MenuPanel extends JPanel {
    //public ClassSelector classSelector;
    //public TeamSelector teamSelector;
    public boolean open;

    public CharClass selectedClass = CharClass.ROCKETMAN;
    public Team selectedTeam = Team.BLUE;

    final int char_padding = 75;
    final int width = 500;
    final int height = 400;
    final int char_height = 90;
    final int char_bottom = 50;
    int totwidth;

    JTextField red, blue, quit, resume;

    ClassPortrait[] classPortraits;

    Player dummyPlayer;

    GameClient client;

    public MenuPanel(GameClient client) {
        this.client = client;

        setVisible(false);
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0.75f));
        setLayout(null);

        //  Init players
        dummyPlayer = new Player();
        dummyPlayer.team = Team.RED;
        Rocketman rocketman = new Rocketman(dummyPlayer);
        Ninja ninja = new Ninja(dummyPlayer);
        Commando commando = new Commando(dummyPlayer);
        Scout scout = new Scout(dummyPlayer);
        Character[] classes = new Character[]{rocketman, ninja, commando, scout};

        // Init char selector
        totwidth = 0;
        int i = 0;
        classPortraits = new ClassPortrait[4];
        for (Character character : classes) {
            totwidth += character.width + char_padding;
            character.xhair = new Point2f(Integer.MAX_VALUE, 0);
            character.onGround = true;
            character.updateSprite(0);

            ClassPortrait port = new ClassPortrait(character);
            port.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    selectedClass = port.character.getCharClass();
                }
            });
            classPortraits[i++] = port;
            add(port);
        }
        totwidth -= char_padding;

        Color blue_idle = new Color(0, 0, 255);
        Color blue_mouseover = new Color(19, 63, 255);
        Color red_idle = new Color(255, 0, 0);
        Color red_mouseover = new Color(255, 53, 59);
        Color gray_idle = new Color(63, 63, 63);
        Color gray_mouseover = new Color(101, 101, 101);

        // Init menu buttons
        blue = new JTextField("BLUE");
        blue.setBackground(blue_idle);
        blue.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedTeam = Team.BLUE;
                dummyPlayer.team = Team.BLUE;
                for (ClassPortrait p : classPortraits) {
                    p.character.sprite = null;
                    p.character.updateSprite(0);
                    p.repaint();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                blue.setBackground(blue_mouseover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                blue.setBackground(blue_idle);
            }
        });

        red = new JTextField("RED");
        red.setBackground(red_idle);
        red.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectedTeam = Team.RED;
                dummyPlayer.team = Team.RED;
                for (ClassPortrait p : classPortraits) {
                    p.character.sprite = null;
                    p.character.updateSprite(0);
                    p.repaint();
                }
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                red.setBackground(red_mouseover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                red.setBackground(red_idle);
            }
        });

        resume = new JTextField("RESUME GAME");
        resume.setBackground(gray_idle);
        resume.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                close();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                resume.setBackground(gray_mouseover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                resume.setBackground(gray_idle);
            }
        });

        quit = new JTextField("QUIT TO MAIN MENU");
        quit.setBackground(gray_idle);
        quit.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.exit(0);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                quit.setBackground(gray_mouseover);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                quit.setBackground(gray_idle);
            }
        });

        for (JTextField j : new JTextField[]{red, blue, resume, quit}) {
            j.setFont(GameClient.FONT_HEADING.deriveFont(20f));
            j.setForeground(Color.WHITE);
            j.setBorder(null);
            j.setFocusable(false);
            j.setEditable(false);
            j.setHorizontalAlignment(SwingConstants.CENTER);
            j.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.WHITE));
            add(j);
        }
    }

    public void layoutGUI() {
        // Class portraits
        int offset = -30;
        for (ClassPortrait port : classPortraits) {
            port.setBounds((getWidth() - totwidth) / 2 + offset, (getHeight() - height) / 2, (int) (port.character.width + char_padding), char_height + char_bottom);
            offset += port.character.width + char_padding;
        }

        // Menu buttons
        int button_margin = 10;
        int button_height = 45;
        int left = (getWidth() - width) / 2;
        int top = (getHeight() - height) / 2 + char_height + char_bottom + button_margin;
        red.setBounds(left, top, (width - button_margin) / 2, button_height);
        blue.setBounds(left + (width + button_margin) / 2, top, (width - button_margin) / 2, button_height);
        top += button_height + button_margin;
        quit.setBounds(left, top, width, button_height);
        top += button_height + button_margin;
        resume.setBounds(left, top, width, button_height);

        revalidate();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(new Color(0, 0, 0, 0.75f));
        g2.fillRect(0, 0, getWidth(), getHeight());

        super.paintComponent(g);
    }

    public void open() {
//        System.out.println("opening menu");
        open = true;
        setVisible(true);
    }

    public void close() {
//        System.out.println("closing menu");
        final Player player = client.game.getPlayer(client.clientName);
        if (player.team != selectedTeam || player.charClass != selectedClass)
            client.spawnParams = new SpawnParams(selectedTeam, selectedClass);

        open = false;
        setVisible(false);
    }

    class ClassPortrait extends JPanel {
        model.characters.Character character;

        public ClassPortrait() {
            setVisible(true);
            setOpaque(false);
        }

        public ClassPortrait(model.characters.Character character) {
            this.character = character;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g;
            g2.setColor(Color.WHITE);

//            g2.setColor(Color.RED);
//            g2.fillRect(0, 0, getWidth(), getHeight());


            g2.translate((getWidth() - character.width) / 2, char_height);
            character.draw(g2); // Class image
            g2.setFont(GameClient.FONT_SEMIBOLD.deriveFont(14f));
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            GUI.drawString_centerHoriz(g2, character.getName(), -char_padding / 2, 20, (int) (character.width + char_padding)); // Class name

            final int triangle_width = 15;
            final int triangle_height = 10;
            final int triangle_margintop = 10;
            final int triangleCenter = (int) (character.width / 2);

            if (selectedClass == character.getCharClass())
                g2.fillPolygon( // Selector triangle
                        new int[]{triangleCenter - triangle_width / 2, triangleCenter, triangleCenter + triangle_width / 2},
                        new int[]{20 + triangle_margintop + triangle_height, 20 + triangle_margintop, 20 + triangle_margintop + triangle_height},
                        3);

            //super.paintComponent(g);
        }
    }
}
