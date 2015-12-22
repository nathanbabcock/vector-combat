package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

/**
 * Created by Nathan on 12/21/2015.
 */
public class UITest extends JFrame {
    static final int width = 800;
    static final int height = 600;

    static final Integer LAYER_CANVAS = new Integer(0);
    static final Integer LAYER_HUD = new Integer(1);
    static final Integer LAYER_CHAT = new Integer(2);
    static final Integer LAYER_OVERLAY = new Integer(3);

    Insets insets;
    JLayeredPane lp;
    JPanel canvas;
    JTextArea health;
    JPanel chat;

    public UITest() {
        // Initialize (add all components, JFrame properties, etc)
        initGUI();

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                layoutGUI();
            }
        });
        layoutGUI();
    }

    public int getRealWidth() {
        return getWidth() - insets.right - insets.left;
    }

    public int getRealHeight() {
        return getHeight() - insets.top - insets.bottom;
    }

    public void initGUI() {
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(width, height);

        insets = getInsets();
        lp = getLayeredPane();

        // Game rendering canvas
        canvas = new JPanel();
        canvas.setBackground(Color.BLACK);
        lp.add(canvas, LAYER_CANVAS);

        health = new JTextArea("200");
        health.setForeground(Color.GREEN);
        health.setFont(new Font("Lucida Sans", Font.BOLD, 50));
        health.setOpaque(false);
        lp.add(health, LAYER_HUD);

        chat = new JPanel();
        chat.setBackground(Color.RED);
        lp.add(chat, LAYER_CHAT);
    }

    public void layoutGUI() {
        canvas.setBounds(0, 0, getRealWidth(), getRealHeight());

        float chatWidth_relative = 0.50f;
        float chatHeight_relative = 0.50f;
        int chatWidth_absolute = (int) (chatWidth_relative * getRealWidth());
        int chatHeight_absolute = (int) (chatHeight_relative * getRealHeight());
        chat.setBounds(0, getRealHeight() - chatHeight_absolute, chatWidth_absolute, chatHeight_absolute);

        int hpWidth = 110;
        int hpHeight = 60;
        health.setBounds(getRealWidth() - hpWidth, getRealHeight() - hpHeight, hpWidth, hpHeight);

    }

    public static void main(String[] args) {
        new UITest();
    }
}
