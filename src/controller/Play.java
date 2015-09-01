package controller;

import model.Game;
import model.geometry.Point2D;
import view.Canvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Nathan on 8/19/2015.
 */
public class Play extends JFrame {
    Game game;
    Canvas canvas;

    long lastFpsTime;
    public int fps;

    public Play() {
        game = new Game();
        canvas = new Canvas(game);

        // Layout
        setSize(new Dimension(canvas.WIDTH, canvas.HEIGHT + 40));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        add(canvas);
        repaint();

        setupListeners();

        gameLoop();
    }

    public void gameLoop() {
        long lastLoopTime = System.nanoTime();
        final int TARGET_FPS = 60;
        final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

        while (true) {
            // work out how long its been since the last update, this
            // will be used to calculate how far the entities should
            // move this loop
            long now = System.nanoTime();
            long updateLength = now - lastLoopTime;
            lastLoopTime = now;
            float delta = updateLength / ((float) OPTIMAL_TIME);

            // update the frame counter
            lastFpsTime += updateLength;
            fps++;

            // update our FPS counter if a second has passed since
            // we last recorded
            if (lastFpsTime >= 1000000000) {
                System.out.println("FPS: " + fps);
                lastFpsTime = 0;
                fps = 0;
            }

            // update the game logic
            game.update(OPTIMAL_TIME / 1000000000f);
            game.player.xhair = new Point2D(canvas.xhair.x - canvas.cameraOffsetX, canvas.HEIGHT - canvas.cameraOffsetY - canvas.xhair.y);
            canvas.repaint();

            // we want each frame to take 10 milliseconds, to do this
            // we've recorded when we started the frame. We add 10 milliseconds
            // to this and then factor in the current time to give
            // us our final value to wait for
            // remember this is in ms, whereas our lastLoopTime etc. vars are in ns.
            try {
                Thread.sleep((lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setupListeners() {
        InputMap im = canvas.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap am = canvas.getActionMap();

        // RIGHT pressed
        Action rightPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.player.walkingRight = true;
            }
        };
        am.put("rightPressed", rightPressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "rightPressed");

        // RIGHT released
        Action rightReleased = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.player.walkingRight = false;
            }
        };
        am.put("rightReleased", rightReleased);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "rightReleased");

        // LEFT pressed
        Action leftPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.player.walkingLeft = true;
            }
        };
        am.put("leftPressed", leftPressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "leftPressed");

        // LEFT released
        Action leftReleased = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.player.walkingLeft = false;
            }
        };
        am.put("leftReleased", leftReleased);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "leftReleased");

        // SPACE pressed
        Action spacePressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.player.jumping = true;
            }
        };
        am.put("spacePressed", spacePressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "spacePressed");

        // SPACE pressed
        Action spaceReleased = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                game.player.jumping = false;
            }
        };
        am.put("spaceReleased", spaceReleased);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, true), "spaceReleased");

        canvas.addMouseListener(new MouseListener() {
            @Override
            public void mousePressed(MouseEvent e) {
//                Point xhair = canvas.xhair;//e.getPoint();
//                game.attack(new Point2D(xhair.x - canvas.cameraOffsetX, canvas.HEIGHT - canvas.cameraOffsetY - xhair.y));
//                game.player.attack();
                game.player.attacking = true;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                game.player.attacking = false;
            }

            @Override
            public void mouseEntered(MouseEvent e) {
            }

            @Override
            public void mouseExited(MouseEvent e) {
            }
        });

        canvas.addMouseMotionListener(new MouseMotionListener() {
            @Override
            public void mouseDragged(MouseEvent e) {
                Point xhair = e.getPoint();
                canvas.xhair = new Point2D(xhair.x, xhair.y);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                Point xhair = e.getPoint();
                canvas.xhair = new Point2D(xhair.x, xhair.y);
            }
        });

    }

    public static void main(String[] args) {
        new Play();
    }
}
