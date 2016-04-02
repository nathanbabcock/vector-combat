package network;

import ai.AI;
import ai.PathNode;
import characters.CharClass;
import characters.Character;
import characters.Team;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import core.Game;
import core.Player;
import geometry.Point2f;
import view.Canvas;
import view.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nathan on 1/10/2016.
 */
public class GameClient extends JFrame {
    public Game game;
    private com.esotericsoftware.kryonet.Client client;
    public String clientName;
    private InputState inputState;
    private ArrayList<ChatMessage> chatQueue;
    public SpawnParams spawnParams;

    private static final int PREF_WIDTH = 1024;
    private static final int PREF_HEIGHT = 768;
    private static final int TIMEOUT = 5000;
    private final int VID_FPS = 60; // Number of times per second both GAME LOGIC and RENDERING occur
    private final int NET_FPS = 20; // Number of times per second input is sent to the server
    private float TIMESCALE = Network.TIMESCALE;

    private static final Integer LAYER_CANVAS = 0;
    private static final Integer LAYER_HUD = 1;
    private static final Integer LAYER_CHAT = 2;
    private static final Integer LAYER_OVERLAY = 3;

    private Canvas canvas;
    private int messageMode;
    private Insets insets;
    private JLayeredPane lp;
    private ChatPanel chat;
    private ScorePanel scores;
    private MenuPanel menu;
    private JTextArea health;
    private JTextField respawn, winner;

    public static final boolean devmode = true;

    GameClient(String clientName, String server, int tcp_port, int udp_port) {
        this.clientName = clientName;
        initNetwork(server, tcp_port, udp_port);
    }

    private void initGame(Game game) {
        inputState = new InputState();
        messageMode = 0;
        chatQueue = new ArrayList<>();

        this.game = new Game();
        Sprite.initSprites();
        this.game.setMap(game.mapID);
        this.game.importGame(game);

        initGUI();
        setupListeners();

        if (devmode) {
            this.game.ai = new AI();
            this.game.ai.nodes = PathNode.readNodes("ctf_space.nodes");
            // TODO load nodes and things
        }

        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);
        scheduler.scheduleAtFixedRate(new GameTick(), 0, 1000 / VID_FPS, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(new NetworkTick(), 0, 1000 / NET_FPS, TimeUnit.MILLISECONDS);
    }

    private void initNetwork(String server, int tcp_port, int udp_port) {
        client = new com.esotericsoftware.kryonet.Client();

        Network.register(client);

        client.addListener(new Listener.ThreadedListener(new Listener() {
            public void received(Connection connection, Object object) {
//                System.out.println("Received object: " + object);
                try {
                    if (object instanceof Game) {
                        if (game == null) { // First time game received
                            initGame((Game) object);
                            { // DEBUG
                                //inputState.xhair = new Point2f(1000, 30);
                                //inputState.attacking = true;
                                client.sendTCP(new SpawnParams(Team.RED, CharClass.ROCKETMAN));
                            }
                        } else {
                            if (devmode && !game.ai.replay.isEmpty()) {
                                Character character = game.getPlayer(clientName).character;
                                if (character != null)
                                    character.importState(game.ai.replay.remove());
                                else
                                    game.ai.replay = new LinkedList<>();
                            } else {
                                game.importGame((Game) object);
                                inputState.lastTick = ((Game) object).net_tick;
                            }
                        }
                    } else if (object instanceof ChatMessage) {
                        //System.out.println("RECEIVED CHAT");
                        game.chat.add((ChatMessage) object);
                        refreshChat();
                    } else
                        System.out.println(object);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }));

        client.start();

        try {
            client.connect(TIMEOUT, server, tcp_port, udp_port);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }

        client.sendTCP(clientName);
    }

    private void initGUI() {
        // Window settings
        setSize(PREF_WIDTH, PREF_HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
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
        health.setFont(GUI.FONT_HEADING.deriveFont(50f));
        health.setOpaque(false);
        health.setEditable(false);
        health.setFocusable(false);
        lp.add(health, LAYER_HUD);

        // Respawn HUD
        respawn = new JTextField();
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
        winner.setFont(GUI.FONT_HEADING.deriveFont(20f));
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
        menu = new MenuPanel(this);
        lp.add(menu, LAYER_OVERLAY);
        //menu.open();

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
        //final int menuWidth = 500;
        //final int menuHeight = 300;
        //menu.setBounds((int) ((realWidth - menuWidth) / 2f), (int) ((realHeight - menuHeight) / 2f), menuWidth, menuHeight);
        menu.setBounds(0, 0, realWidth, realHeight);
        menu.layoutGUI();

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

    private class GameTick implements Runnable {
        @Override
        public void run() {
            // Part 1: Update model
            if (game != null) {
//                System.out.println("game tick");
                game.update(TIMESCALE / VID_FPS);
                updateHUD();
                inputState.xhair = new Point2f(canvas.xhair.x - canvas.cameraOffsetX, canvas.getHeight() - canvas.cameraOffsetY - canvas.xhair.y);
                repaint();
            }
        }
    }

    private class NetworkTick implements Runnable {
        @Override
        public void run() {
            // InputState
            client.sendUDP(inputState);

            // Chat
            for (ChatMessage msg : chatQueue)
                client.sendTCP(msg);
            chatQueue = new ArrayList<>();

            // Spawn params
            if (spawnParams != null) {
                client.sendTCP(spawnParams);
                spawnParams = null;
            }

            // Devmode
            if (devmode) {
                Player player = game.getPlayer(clientName);
                if (player == null) return;
                game.ai.update(player.character, inputState);
            }
        }
    }

    private class GameUpdater extends Thread {
        //        final int VID_FPS = 60; // Number of times per second both GAME LOGIC and RENDERING occur
//        final int NET_FPS = 20; // Number of times per second input is sent to the server
        final int VID_NET_RATIO = VID_FPS / NET_FPS;
        final int FRAME_TIME = 1000 / VID_FPS; // Expected time for each frame from in milliseconds

        public GameUpdater() {
            setName("Client: Game updater (" + clientName + ")");
        }

        @Override
        public void run() {
            long startTime;
            short frameNo = 0;
            int overflow = 0; // If a frame takes than usual, the next frame will compensate

            while (client.isConnected()) {
                startTime = System.currentTimeMillis();
//                System.out.println("Frame " +   frameNo);

                // Part 1: Update core
                if (game != null) {
//                    System.out.println("game tick");
                    game.update(1f / VID_FPS);
                    updateHUD();
                    inputState.xhair = new Point2f(canvas.xhair.x - canvas.cameraOffsetX, canvas.getHeight() - canvas.cameraOffsetY - canvas.xhair.y);
                    repaint();
                }

                // Part 2: Send back to server
                if (frameNo % VID_NET_RATIO == 0) {
//                    System.out.println("network tick");
                    // InputState
//                    inputState.sent = System.currentTimeMillis();
                    client.sendUDP(inputState);

                    // Chat
                    for (ChatMessage msg : chatQueue)
                        client.sendTCP(msg);
                    chatQueue = new ArrayList<>();

                    // Spawn params
                    if (spawnParams != null) {
                        client.sendTCP(spawnParams);
                        spawnParams = null;
                    }
                }

                // Increment frame number
                if (frameNo++ >= VID_FPS) {
                    frameNo = 0;
                }

                // Wait until next frame
                long frameTime = System.currentTimeMillis() - startTime;
//                System.out.println("Frame took " + frameTime + "ms");
                int sleepTime = (int) (FRAME_TIME - frameTime) + overflow;
                overflow = 0;
                if (sleepTime < 0) {
//                    System.out.println("Error: frame took " + frameTime + "/" + FRAME_TIME + "ms");
                    overflow = sleepTime;
                } else {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
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
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), "enterPressed");

        // TAB pressed
        Action tabPressed = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (messageMode != 0 || menu.open) return;
                //scores.open();
                canvas.scoreboard.open = true;
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
                //scores.close();
                canvas.scoreboard.open = false;
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
                } else {
                    if (canvas.scoreboard.open)
                        canvas.scoreboard.open = false;
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
                canvas.xhair = new Point2f(xhair.x, xhair.y);
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                if (messageMode != 0 || menu.open) return;
                Point xhair = e.getPoint();
                canvas.xhair = new Point2f(xhair.x, xhair.y);
            }
        });

    }

    private void sendChat() {
        String message = chat.textField.getText();
        if (!message.equals("") && !consoleCmd(message))
            chatQueue.add(new ChatMessage(clientName, message, game.getPlayer(clientName).team, messageMode == 2));
        hideChat();
    }

    private boolean consoleCmd(String msg) {
        Scanner scanner = new Scanner(msg);
        if (!scanner.hasNext()) return false;
        String command = scanner.next();
        switch (command) {
            case "!startRecord":
            case "!start":
                game.ai.recording = true;
                break;
            case "!stopRecord":
            case "!stop":
                game.ai.recording = false;
                break;
            case "!saveEdges":
            case "!save":
                game.ai.writeEdges(scanner.next());
                break;
            case "!removeEdge":
            case "!remove":
                if (!game.ai.edges.isEmpty())
                    game.ai.edges.remove(game.ai.edges.size() - 1);
                break;
            case "!clearEdges":
            case "!clear":
                game.ai.edges = new ArrayList<>();
                break;
            case "!loadEdges":
            case "!load":
                game.ai.readEdges(scanner.next(), Character.getCharClass(scanner.next()));
                break;
            case "!replayEdges":
            case "!replay":
                if (!game.ai.replay.isEmpty()) {
                    System.out.println("Please wait until the current replay is over first.");
                    return true;
                }
                Player player = game.getPlayer(clientName);
                if (player == null || player.character == null || game.ai.edges.isEmpty()) return true;
                player.character.setPosition(game.ai.edges.get(game.ai.edges.size() - 1).fromPos);
                player.character.velocity = game.ai.edges.get(game.ai.edges.size() - 1).fromVel;
                game.ai.replay.addAll(game.ai.edges.get(game.ai.edges.size() - 1).frames);
                System.out.println("Replaying edge");
                break;
            case "!pathTest":
            case "!path":
                PathNode dest = game.ai.closestNode(game.getPlayer(clientName).character.getPosition());
                System.out.println("Pathing from node 0 to node " + dest.index);
                System.out.println(game.ai.getPath(game.ai.nodes.get(0), dest));
                break;
            default:
                return false;
        }
        return true;
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


    public static void main(String[] args) {
/*        try {
            PrintStream out = new PrintStream(new FileOutputStream("log.txt"));
            System.setOut(out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/

        new GameClient(JOptionPane.showInputDialog("Username:"), "68.230.58.93", Network.TCP_PORT, Network.UDP_PORT);
//        new KryoClient(JOptionPane.showInputDialog("Username:"), "68.230.58.93", Network.TCP_PORT, Network.UDP_PORT);
    }
}