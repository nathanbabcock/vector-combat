package view;

import java.awt.*;

/**
 * Created by Nathan on 3/14/2016.
 */
public class GUI {
    public static void drawString_centerHoriz(Graphics2D g2, String string, int containerX, int y, int containerWidth) {
        g2.drawString(string, containerX + (containerWidth - g2.getFontMetrics().stringWidth(string)) / 2, y);
//        g2.drawRect(containerX, y - g2.getFontMetrics().getHeight(), containerWidth, g2.getFontMetrics().getHeight());
    }
}
