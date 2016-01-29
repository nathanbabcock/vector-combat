package model.kryo;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;
import network.ChatMessage;
import network.InputState;
import network.SpawnParams;
import view.Canvas;
import view.ChatPanel;
import view.MenuPanel;
import view.ScorePanel;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Nathan on 1/10/2016.
 */
public class KryoClient extends JFrame {
    Client client;
    String clientName;
    InputState inputState;
    ArrayList<ChatMessage> chatQueue;
    SpawnParams spawnParams;

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

    public KryoClient() {
        initNetwork();
        initGUI();
    }

    private void initNetwork() {
        client = new Client();

        Network.register(client);

        client.addListener(new Listener.ThreadedListener(new Listener() {
            public void received(Connection connection, Object object) {
//                System.out.println("Received object: " + object);
//                if (object instanceof Game) {
//                    System.out.println(object);
//                }
            }
        }));

        client.start();

        try {
            client.connect(5000, "localhost", Network.PORT1, Network.PORT2);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }

        client.sendUDP("excalo");
    }

    private void initGUI() {
        setSize(PREF_WIDTH, PREF_HEIGHT);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
        setFocusTraversalKeysEnabled(false);
    }

    public static void main(String[] args) {
        Log.set(Log.LEVEL_DEBUG);
        new KryoClient();
    }
}