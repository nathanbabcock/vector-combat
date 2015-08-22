package controller;

import model.Game;
import view.Canvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;

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
        setSize(new Dimension(game.map.WIDTH, game.map.HEIGHT + 40));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);


        // HERE ARE THE KEY BINDINGS
        setupListeners();
        // END OF KEY BINDINGS


        add(canvas);
        repaint();

        // Listeners
//        setupListeners();
//        KeyboardFocusManager manager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
//        manager.addKeyEventDispatcher(new MyDispatcher());

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
//                System.out.println("Right pressed");

                game.player.velocity.x += game.player.moveSpeed;

                am.remove("rightPressed");
            }
        };
        am.put("rightPressed", rightPressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), "rightPressed");

        // RIGHT released
        Action rightReleased = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                System.out.println("Right released");

                game.player.velocity.x -= game.player.moveSpeed;

                am.put("rightPressed", rightPressed);
            }
        };
        am.put("rightReleased", rightReleased);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), "rightReleased");

        // LEFT pressed
        Action leftPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                System.out.println("Left pressed");

                game.player.velocity.x -= game.player.moveSpeed;

                am.remove("leftPressed");
            }
        };
        am.put("leftPressed", leftPressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), "leftPressed");

        // LEFT released
        Action leftReleased = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                System.out.println("Left released");

                game.player.velocity.x += game.player.moveSpeed;

                am.put("leftPressed", leftPressed);
            }
        };
        am.put("leftReleased", leftReleased);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), "leftReleased");

        // SPACE pressed
        Action spacePressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
//                System.out.println("Space pressed");
                game.player.velocity.y = game.player.jumpSpeed;
            }
        };
        am.put("spacePressed", spacePressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0, false), "spacePressed");


    }


//    private class MyDispatcher implements KeyEventDispatcher {
//        @Override
//        public boolean dispatchKeyEvent(KeyEvent e) {
//            int keyCode = e.getKeyCode();
//            switch (keyCode) {
//                case KeyEvent.VK_SPACE:
////                    player.jump();
//                    break;
//                case KeyEvent.VK_LEFT:
////                    game.player.moveingLeft = true;
//                    break;
//                case KeyEvent.VK_RIGHT:
//                    game.player.moveRight();
//                    break;
//            }
//            return true;
//        }
//    }

    public static void main(String[] args) {
        new Play();
    }
}
