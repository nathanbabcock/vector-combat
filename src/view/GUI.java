package view;

import java.awt.*;
import java.io.IOException;

/**
 * Created by Nathan on 3/14/2016.
 */
public class GUI {
    public static Font FONT_HEADING, FONT_TEXT, FONT_BOLD, FONT_SEMIBOLD;

    static {
        // Fonts
        try {
            FONT_HEADING = Font.createFont(Font.TRUETYPE_FONT, GUI.class.getClassLoader().getResourceAsStream("res/fonts/Cornerstone.ttf")).deriveFont(12f);
            FONT_TEXT = Font.createFont(Font.TRUETYPE_FONT, GUI.class.getClassLoader().getResourceAsStream("res/fonts/OpenSans-Regular.ttf")).deriveFont(14f);
            FONT_BOLD = Font.createFont(Font.TRUETYPE_FONT, GUI.class.getClassLoader().getResourceAsStream("res/fonts/OpenSans-Bold.ttf")).deriveFont(14f);
            FONT_SEMIBOLD = Font.createFont(Font.TRUETYPE_FONT, GUI.class.getClassLoader().getResourceAsStream("res/fonts/OpenSans-Semibold.ttf")).deriveFont(14f);

            GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(FONT_HEADING);
            ge.registerFont(FONT_TEXT);
            ge.registerFont(FONT_BOLD);
            ge.registerFont(FONT_SEMIBOLD);
        } catch (IOException | FontFormatException e) {
            e.printStackTrace();
        }
    }

    public static void drawString_centerHoriz(Graphics2D g2, String string, int containerX, int y, int containerWidth) {
        g2.drawString(string, containerX + (containerWidth - g2.getFontMetrics().stringWidth(string)) / 2, y);
//        g2.drawRect(containerX, y - g2.getFontMetrics().getHeight(), containerWidth, g2.getFontMetrics().getHeight());
    }
}
