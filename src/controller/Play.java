package controller;

import model.Game;
import view.Canvas;

import javax.swing.*;
import java.awt.*;

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
        setSize(new Dimension(game.map.WIDTH, game.map.HEIGHT));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        add(canvas);
        repaint();

        // Listeners
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
