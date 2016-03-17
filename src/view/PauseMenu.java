package view;

import model.Game;
import model.Player;
import model.characters.*;
import model.characters.Character;
import model.geometry.Point2f;

import java.awt.*;
import java.util.ArrayList;

/**
 * Created by Nathan on 3/14/2016.
 */
public class PauseMenu {
    private Game game;
    Player dummyPlayer;

    public boolean open;
    public CharClass selectedClass;
    public Team selectedTeam = Team.BLUE;

    int totwidth;

    final int char_padding = 75;
    final int char_height = 100;

    boolean listenersInitialized;

    Character[] classes;

    public ArrayList<Clickable> clickables;

    public PauseMenu(Game game) {
        this.game = game;

        // Draw players
        dummyPlayer = new Player();
        dummyPlayer.team = Team.RED;
        Rocketman rocketman = new Rocketman(dummyPlayer);
        Ninja ninja = new Ninja(dummyPlayer);
        Commando commando = new Commando(dummyPlayer);
        Scout scout = new Scout(dummyPlayer);

        classes = new Character[]{rocketman, ninja, commando, scout};

        for (Character cClass : classes) {
            totwidth += cClass.getWidth() + char_padding;
            cClass.xhair = new Point2f(Integer.MAX_VALUE, 0);
            cClass.onGround = true;
            cClass.updateSprite(0);
        }
        totwidth -= char_padding;
    }

    public void checkClick(int x, int y) {
        for (Clickable c : clickables)
            if (c.contains(x, y)) {
                c.listener.onClick();
                c.isClicked = true;
            }
    }

    public void checkMouseover(int x, int y) {
        for (Clickable c : clickables)
            if (c.contains(x, y)) {
                c.listener.onMouseover();
                c.isMouseover = true;
            }
    }

    public void initListeners(int width, int height) {
        listenersInitialized = true;
        clickables = new ArrayList<>();
        int xOffset = (width - totwidth) / 2;
        int yOffset = char_height;

        for (Character cClass : classes) {
            Clickable click = new Clickable(xOffset - char_padding / 2, (int) (yOffset - cClass.getHeight() + 30), (int) cClass.getWidth() + char_padding, (int) cClass.getHeight() + 30);
            click.listener = new Clickable.Listener() {
                @Override
                public void onClick() {
                    //System.out.println("Clicked on "+cClass.getName());
                    selectedClass = cClass.getCharClass();
                }

                @Override
                public void onMouseover() {
                    //System.out.println("Mouseover on "+cClass.getName());
                }
            };
            clickables.add(click);
            xOffset += cClass.getWidth() + char_padding;
        }
    }

    public void draw(Graphics2D g2, int width, int height) {
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (!listenersInitialized)
            initListeners(width, height);

        g2.setColor(new Color(0, 0, 0, 0.75f));
        g2.fillRect(0, 0, width, height); // Background overlay

        Graphics2D charCanvas = (Graphics2D) g2.create();
        charCanvas.translate((width - totwidth) / 2, char_height);
        charCanvas.setColor(Color.WHITE);
        charCanvas.setFont(GUI.FONT_TEXT.deriveFont(12f));
        int i = 0;
        for (Character cClass : classes) {
            cClass.draw(charCanvas); // Class image
            GUI.drawString_centerHoriz(charCanvas, cClass.getName(), -char_padding / 2, 30, (int) (cClass.getWidth() + char_padding)); // Class name

            final int triangle_width = 15;
            final int triangle_height = 10;
            final int triangle_margintop = 10;

//            if(selectedClass == cClass.getCharClass())
//                charCanvas.drawPolygon(new int[]{}, new int[]{}, 3);

            // TODO draw a triangle here

            final int triangleCenter = (int) (cClass.getWidth() / 2);

            if (selectedClass == cClass.getCharClass())
                charCanvas.fillPolygon( // Selector triangle
                        new int[]{triangleCenter - triangle_width / 2, triangleCenter, triangleCenter + triangle_width / 2},
                        new int[]{30 + triangle_margintop + triangle_height, 30 + triangle_margintop, 30 + triangle_margintop + triangle_height},
                        3);

            charCanvas.translate(cClass.getWidth() + char_padding, 0);
        }

        // Menu buttons
        final int menu_width = 500;
        final int menu_height = 400;
        Graphics2D menuC = (Graphics2D) g2.create();
        menuC.translate((width - menu_width) / 2, (height - menu_height) / 2);
        menuC.setColor(new Color(1, 0, 0, 0.5f));
        menuC.fillRect(0, 0, menu_width, menu_height);
    }
}
