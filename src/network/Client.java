package network;

import model.Game;
import model.geometry.Point2D;
import view.Canvas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Nathan on 9/12/2015.
 */
public class Client extends JFrame {
    Game game;
    String clientName;
    InputState inputState;
    Socket server;
    ObjectOutputStream out;
    ObjectInputStream in;
    Canvas canvas;

    long lastFpsTime;
    public int fps;


    public Client(String host, int port, String username) {
        clientName = username;
        game = new Game();
        canvas = new Canvas(game, clientName);
        inputState = new InputState();

        // Layout
        setSize(new Dimension(canvas.WIDTH, canvas.HEIGHT + 40));
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        add(canvas);
        repaint();

//        if (host == null || port == null || clientName == null)
//            return;

        try {
            // Open a connection to the server
            server = new Socket(host, port);
            out = new ObjectOutputStream(server.getOutputStream());
            in = new ObjectInputStream(server.getInputStream());

            // Handle duplicate client names
            while (true) {
                out.writeObject(clientName);
                if ((boolean) in.readObject() == true) {
                    System.out.println("Connection accepted by server " + host + " on port " + port + " with username " + clientName);
                    break;
                } else {
                    System.out.println("Server denied connection; duplicate username " + clientName);
                    clientName = JOptionPane.showInputDialog("A player with that username is already connected to the server.\nPlease choose a different user name:");
                }
            }

            // add a listener that sends a disconnect command to when closing
            addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent arg0) {
                    try {
                        out.writeObject(null);
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

            // start a thread for handling server events
            new Thread(new ServerHandler()).start();

        } catch (Exception e) {
            e.printStackTrace();
        }

        setupListeners();
        gameLoop();
    }

    public void gameLoop() {
        long lastLoopTime = System.nanoTime();
        final int TARGET_FPS = 60;
        final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;

        while (!server.isClosed()) {
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

            // Update model
//            game.update(OPTIMAL_TIME / 1000000000f);
//            if (game.players.get(clientName) != null && game.players.get(clientName).xhair != null)
            inputState.xhair = new Point2D(canvas.xhair.x - canvas.cameraOffsetX, canvas.HEIGHT - canvas.cameraOffsetY - canvas.xhair.y);
            canvas.repaint();
            try {
                out.writeObject(ObjectCloner.deepCopy(inputState));
            } catch (Exception e) {
                e.printStackTrace();
            }

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
                inputState.movingRight = true;
            }
        };
        am.put("rightPressed", rightPressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, false), "rightPressed");

        // RIGHT released
        Action rightReleased = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputState.movingRight = false;
            }
        };
        am.put("rightReleased", rightReleased);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_D, 0, true), "rightReleased");

        // LEFT pressed
        Action leftPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputState.movingLeft = true;
            }
        };
        am.put("leftPressed", leftPressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, false), "leftPressed");

        // LEFT released
        Action leftReleased = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputState.movingLeft = false;
            }
        };
        am.put("leftReleased", leftReleased);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_A, 0, true), "leftReleased");

        // UP pressed
        Action upPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputState.movingUp = true;
            }
        };
        am.put("upPressed", upPressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, false), "upPressed");

        // UP released
        Action upReleased = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputState.movingUp = false;
            }
        };
        am.put("upReleased", upReleased);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_W, 0, true), "upReleased");

        // DOWN pressed
        Action downPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputState.movingDown = true;
            }
        };
        am.put("downPressed", downPressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "downPressed");

        // DOWN released
        Action downReleased = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                inputState.movingDown = false;
            }
        };
        am.put("downReleased", downReleased);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "downReleased");

        canvas.addMouseListener(new MouseListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e))
                    inputState.altAttacking = true;
                else
                    inputState.attacking = true;
            }

            @Override
            public void mouseClicked(MouseEvent e) {
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e))
                    inputState.altAttacking = false;
                else
                    inputState.attacking = false;
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

    /**
     * This class reads and executes commands sent from the server
     *
     * @author Gabriel Kishi
     */
    private class ServerHandler implements Runnable {
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                while (true) {
                    Object received = in.readObject();
                    if (received instanceof Game)
//                        System.out.println(game);
                        game.importGame((Game) received);
                }
            } catch (SocketException | EOFException e) {
                return; // "gracefully" terminate after disconnect
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Client("localhost", 9001, "excalo");
    }
}
