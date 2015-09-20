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
import java.util.Random;

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
    int messageMode;
    boolean menuOpen;

    final int pref_width = 800;
    final int pref_height = 600;

    long lastFpsTime;
    public int fps;


    public Client(String host, int port, String username) {
        clientName = username;
        game = new Game();
        inputState = new InputState();
        messageMode = 0;
        menuOpen = false;

        layoutGUI();
        connectToServer(host, port);
        setupListeners();
        gameLoop();
    }

    private void layoutGUI() {
        // Layout
        setSize(pref_width, pref_height);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        canvas = new Canvas(game, clientName);
        add(canvas);

        setVisible(true);
    }

    private void connectToServer(String host, int port) {
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
                    canvas.clientName = clientName;
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
    }

    private void gameLoop() {
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
//                System.out.println("FPS: " + fps);
                lastFpsTime = 0;
                fps = 0;
            }

            // Update model
//            game.update(OPTIMAL_TIME / 1000000000f);
            inputState.xhair = new Point2D(canvas.xhair.x - canvas.cameraOffsetX, canvas.getHeight() - canvas.cameraOffsetY - canvas.xhair.y);
            repaint();
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
                Thread.sleep(Math.max(0, (lastLoopTime - System.nanoTime() + OPTIMAL_TIME) / 1000000));
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
                if (messageMode != 0 || menuOpen) return;
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
                if (messageMode != 0 || menuOpen) return;
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
                if (messageMode != 0 || menuOpen) return;
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
                if (messageMode != 0 || menuOpen) return;
                inputState.movingDown = true;
            }
        };
        am.put("downPressed", downPressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "downPressed");

        // DOWN released
        Action downReleased = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (messageMode != 0 || menuOpen) return;
                inputState.movingDown = false;
            }
        };
        am.put("downReleased", downReleased);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "downReleased");

        // ENTER pressed
        Action enterPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (menuOpen) return;
                if (messageMode != 0)
                    sendChat();
                else
                    showChat();
            }
        };
        am.put("enterPressed", enterPressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "enterPressed");

        // ESC pressed
        Action escPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (messageMode != 0)
                    hideChat();
                else if (menuOpen)
                    hideMenu();
                else
                    showMenu();
            }
        };
        am.put("escPressed", escPressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "escPressed");

        canvas.addMouseListener(new MouseListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (messageMode != 0 || menuOpen) return;
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
                if (messageMode != 0 || menuOpen) return;
                Point xhair = e.getPoint();
                canvas.xhair = new Point2D(xhair.x, xhair.y);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (messageMode != 0 || menuOpen) return;
                Point xhair = e.getPoint();
                canvas.xhair = new Point2D(xhair.x, xhair.y);
            }
        });

    }

    private void sendChat() {
        String message = canvas.chatPanel.textField.getText();
        if (!message.equals("")) {
            try {
                out.writeObject(new ChatMessage(clientName, message, game.players.get(clientName).team, messageMode == 2));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        hideChat();
    }

    private void hideChat() {
        messageMode = 0;
        canvas.chatPanel.textField.setText("");
        canvas.chatPanel.textField.setVisible(false);
    }

    private void showChat() {
        messageMode = 1;
        canvas.chatPanel.textField.setVisible(true);
        canvas.chatPanel.textField.grabFocus();
    }

    private void refreshChat() {
        String chatText = "";
        for (ChatMessage msg : game.chat) {
            chatText += "\n" + msg.player + ": " + msg.content;
        }
        canvas.chatPanel.textArea.setText(chatText);
    }

    private void showMenu() {
        System.out.println("Menu opened");
        menuOpen = true;
    }

    private void hideMenu() {
        System.out.println("Menu closed");
        menuOpen = false;
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
                        game.importGame((Game) received);
                    else if (received instanceof ChatMessage) {
                        game.chat.add((ChatMessage) received);
                        refreshChat();
                    } else
                        System.out.println(received);
                }
            } catch (SocketException | EOFException e) {
                return; // "gracefully" terminate after disconnect
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Client("localhost", 9001, new Random().nextInt(1000) + "");
    }
}
