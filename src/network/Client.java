package network;

import model.Game;
import model.Player;
import model.characters.Character;
import model.characters.Rocketman;
import model.characters.Team;
import model.geometry.Point2D;
import view.Canvas;
import view.ChatPanel;
import view.MenuPanel;
import view.ScorePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * Created by Nathan on 9/12/2015.
 */
public class Client extends JFrame {
    Game game;
    String clientName, host;
    int port;
    InputState inputState;
    ArrayList<ChatMessage> chatQueue;
    SpawnParams spawnParams;
    Socket server;
    ObjectOutputStream out;
    ObjectInputStream in;
    Canvas canvas;
    int messageMode;

    static final int PREF_WIDTH = 1024;
    static final int PREF_HEIGHT = 768;

    static final Integer LAYER_CANVAS = new Integer(0);
    static final Integer LAYER_HUD = new Integer(1);
    static final Integer LAYER_CHAT = new Integer(2);
    static final Integer LAYER_OVERLAY = new Integer(3);

    long lastFpsTime;
    public int fps;

    Insets insets;
    JLayeredPane lp;
    ChatPanel chat;
    ScorePanel scores;
    MenuPanel menu;
    JTextArea health;
    JTextField respawn, winner;

    public Client(String host, int port, String username) {
        clientName = username;

        connectToServer(host, port);
    }

    private void initGame(Game game) {
        inputState = new InputState();
        messageMode = 0;
        chatQueue = new ArrayList();

        this.game = new Game();
        this.game.setMap(game.mapID);
        this.game.importGame(game);

        initGUI();
        setupListeners();

        Thread gameUpdater = new Thread() {
            @Override
            public void run() {
                gameLoop();
            }
        };
        gameUpdater.setName("Client: Game updater (" + clientName + ")");
        gameUpdater.start();

    }

    private void initGUI() {
        setSize(PREF_WIDTH, PREF_HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
//        setVisible(false); // DEBUG
        setVisible(true);
        setSize(PREF_WIDTH, PREF_HEIGHT);
        setFocusTraversalKeysEnabled(false);

        insets = getInsets();
        lp = getLayeredPane();

        // Game rendering canvas
        canvas = new Canvas(game, clientName);
        lp.add(canvas, LAYER_CANVAS);

        // Health HUD
        health = new JTextArea("200");
        health.setForeground(Color.GREEN);
        health.setFont(new Font("Lucida Sans", Font.BOLD, 50));
        health.setOpaque(false);
        health.setEditable(false);
        health.setFocusable(false);
        lp.add(health, LAYER_HUD);

        // Respawn HUD
        respawn = new JTextField("Hello world");
        respawn.setOpaque(false);
        respawn.setEditable(false);
        respawn.setFocusable(false);
        respawn.setBorder(null);
        respawn.setBackground(null);
        respawn.setHorizontalAlignment(SwingConstants.CENTER);
        respawn.setVisible(false);
        lp.add(respawn, LAYER_HUD);

        // Winner HUD
        winner = new JTextField("");
        winner.setFont(new Font("Lucida Sans", Font.BOLD, 20));
        winner.setOpaque(false);
        winner.setEditable(false);
        winner.setFocusable(false);
        winner.setBorder(null);
        winner.setBackground(null);
        winner.setHorizontalAlignment(SwingConstants.CENTER);
        winner.setVisible(false);
        lp.add(winner, LAYER_HUD);

        // Chat
        chat = new ChatPanel();
        lp.add(chat, LAYER_CHAT);

        // Scores
        scores = new ScorePanel(game);
        lp.add(scores, LAYER_OVERLAY);

        // Menu
        menu = new MenuPanel();
        lp.add(menu, LAYER_OVERLAY);
        menu.open();

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

    private void layoutGUI() {
        canvas.setBounds(0, 0, getRealWidth(), getRealHeight());

        int realWidth = getRealWidth();
        int realHeight = getRealHeight();

        // Chat
        final float chatWidth_relative = 0.50f;
        final float chatHeight_relative = 0.50f;
        final int chatWidth_absolute = (int) (chatWidth_relative * realWidth);
        final int chatHeight_absolute = (int) (chatHeight_relative * realHeight);
        chat.setBounds(0, realHeight - chatHeight_absolute, chatWidth_absolute, chatHeight_absolute);

        // Scores
        final int scoresWidth = 500;
        final int scoresHeight = 400;
        scores.setBounds((int) ((realWidth - scoresWidth) / 2f), (int) ((realHeight - scoresHeight) / 2f), scoresWidth, scoresHeight);

        // Pause
        final int menuWidth = 500;
        final int menuHeight = 300;
        menu.setBounds((int) ((realWidth - menuWidth) / 2f), (int) ((realHeight - menuHeight) / 2f), menuWidth, menuHeight);

        // HUD
        // Health
        final int hpWidth = 110;
        final int hpHeight = 60;
        health.setBounds(realWidth - hpWidth, realHeight - hpHeight, hpWidth, hpHeight);

        // Respawn timer
        final int respawnWidth = 500;
        final int respawnHeight = 50;
        respawn.setBounds((int) ((realWidth - respawnWidth) / 2f), (int) ((realHeight - respawnHeight) / 4f), respawnWidth, respawnHeight);

        // Game winner
        final int winnerWidth = 500;
        final int winnerHeight = 50;
        winner.setBounds((int) ((realWidth - winnerWidth) / 2f), (int) ((realHeight - winnerHeight) / 3f), winnerWidth, winnerHeight);

        revalidate();
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

            // start a thread for handling server events
            Thread serverHandler = new Thread(new ServerHandler());
            serverHandler.setName("Client: Server handler (" + clientName + ")");
            serverHandler.start();

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

            if (game != null) {
                // Update model
                game.update(OPTIMAL_TIME / 1000000000f);
                updateHUD();
                inputState.xhair = new Point2D(canvas.xhair.x - canvas.cameraOffsetX, canvas.getHeight() - canvas.cameraOffsetY - canvas.xhair.y);
                repaint();
                /*try {
                    // InputState
                    out.writeObject(ObjectCloner.deepCopy(inputState));

                    // Chat
                    for (ChatMessage msg : chatQueue)
                        out.writeObject(msg);
                    chatQueue = new ArrayList();

                    // Spawn params
                    if (spawnParams != null) {
                        out.writeObject(spawnParams);
                        spawnParams = null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }*/
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
                if (messageMode != 0 || menu.open) return;
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
                if (messageMode != 0 || menu.open) return;
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
                if (messageMode != 0 || menu.open) return;
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
                if (messageMode != 0 || menu.open) return;
                inputState.movingDown = true;
            }
        };
        am.put("downPressed", downPressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, false), "downPressed");

        // DOWN released
        Action downReleased = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (messageMode != 0 || menu.open) return;
                inputState.movingDown = false;
            }
        };
        am.put("downReleased", downReleased);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_S, 0, true), "downReleased");

        // ENTER pressed
        Action enterPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (menu.open) return;
                if (messageMode != 0)
                    sendChat();
                else
                    showChat();
            }
        };
        am.put("enterPressed", enterPressed);
//        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "enterPressed");

        // TAB pressed
        Action tabPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (messageMode != 0 || menu.open) return;
                scores.open();
                am.remove("tabPressed");
            }
        };
        am.put("tabPressed", tabPressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0, false), "tabPressed");

        // TAB released
        Action tabReleased = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (messageMode != 0 || menu.open) return;
                scores.close();
                am.put("tabPressed", tabPressed);
            }
        };
        am.put("tabReleased", tabReleased);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0, true), "tabReleased");


        // ESC pressed
        Action escPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (messageMode != 0)
                    hideChat();
                else if (menu.open) {
                    menu.close();
                    final Player player = game.getPlayer(clientName);
                    Character character = player.character;
//                    System.out.println(player.team+", "+menu.teamSelector.selectedTeam);
                    if (player.team != menu.teamSelector.selectedTeam || player.charClass != menu.classSelector.selectedClass)
                        spawnParams = new SpawnParams(menu.teamSelector.selectedTeam, menu.classSelector.selectedClass);
                } else {
                    if (scores.open)
                        scores.close();
                    menu.open();
                }
            }
        };
        am.put("escPressed", escPressed);
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0, false), "escPressed");

        canvas.addMouseListener(new MouseListener() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (messageMode != 0 || menu.open) return;
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
                if (messageMode != 0 || menu.open) return;
                Point xhair = e.getPoint();
                canvas.xhair = new Point2D(xhair.x, xhair.y);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (messageMode != 0 || menu.open) return;
                Point xhair = e.getPoint();
                canvas.xhair = new Point2D(xhair.x, xhair.y);
            }
        });

    }

    private void sendChat() {
        String message = chat.textField.getText();
        if (!message.equals(""))
            chatQueue.add(new ChatMessage(clientName, message, game.getPlayer(clientName).team, messageMode == 2));
        hideChat();
    }

    private void hideChat() {
        messageMode = 0;
        chat.textField.setText("");
        chat.textField.setVisible(false);
        chat.revalidate();
    }

    private void showChat() {
        messageMode = 1;
        chat.textField.setVisible(true);
        chat.textField.grabFocus();
        chat.revalidate();
    }

    private void refreshChat() {
        String chatText = "";
        for (ChatMessage msg : game.chat)
            chatText += "\n" + msg.player + ": " + msg.content;
        chat.textArea.setText(chatText);
    }

    private void updateHUD() {
        Player player = game.getPlayer(clientName);

        // Health
        int hp;
        try {
            hp = player.character.health;
        } catch (NullPointerException e) {
            hp = 0;
        }
        if (hp > 150)
            health.setForeground(Color.GREEN);
        else if (hp > 100)
            health.setForeground(Color.YELLOW);
        else if (hp > 50)
            health.setForeground(Color.ORANGE);
        else
            health.setForeground(Color.RED);
        health.setText(hp + "");

        // Respawn
        if (game.countdown > 0) {
            respawn.setText("Game starts in " + (int) Math.ceil(game.countdown) + " seconds");
            respawn.setVisible(true);
        } else if (player != null && player.character == null && player.team != null && player.charClass != null) {
            respawn.setText("Respawning in " + (int) Math.ceil(player.respawnTime) + " seconds");
            respawn.setVisible(true);
        } else {
            respawn.setVisible(false);
        }

        // Winner
        if (game.winner == Team.BLUE) {
            winner.setText("Blue wins the game!");
            winner.setForeground(Color.BLUE);
            winner.setVisible(true);
        } else if (game.winner == Team.RED) {
            winner.setText("Red wins the game!");
            winner.setForeground(Color.RED);
            winner.setVisible(true);
        } else {
            winner.setVisible(false);
        }

        // Scoreboard
        if (scores.open && System.currentTimeMillis() % 60 == 0) // TODO ghetto af
            scores.update();
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
                    // Part 1: Receive from server
                    Object received = in.readObject();
                    if (received instanceof Game) {
                        if (game == null) { // First time game received
                            initGame((Game) received);
                            // DEBUG OnlY
                            out.writeObject(new SpawnParams(Team.BLUE, Rocketman.class));
                        } else
                            game.importGame((Game) received);
                    } else if (received instanceof ChatMessage) {
                        game.chat.add((ChatMessage) received);
                        refreshChat();
                    } else
                        System.out.println(received);

                    // Part 2: Send back to server
                    out.reset();

                    // InputState
                    out.writeObject(inputState);

                    // Chat
                    for (ChatMessage msg : chatQueue)
                        out.writeObject(msg);
                    chatQueue = new ArrayList();

                    // Spawn params
                    if (spawnParams != null) {
                        out.writeObject(spawnParams);
                        spawnParams = null;

                    }
                }
            } catch (SocketException | EOFException e) {
                JOptionPane.showMessageDialog(Client.this, "Server disconnected", "Error", JOptionPane.ERROR_MESSAGE);
                try {
                    out.close();
                    in.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                System.exit(0);
                return; // "gracefully" terminate after disconnect
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    public static void main(String[] args) {
        new Client("192.168.1.119", 9001, JOptionPane.showInputDialog("Username:"));
//        new Client("192.168.1.119", 9001, new Random().nextInt(1000) + "");
    }
}
