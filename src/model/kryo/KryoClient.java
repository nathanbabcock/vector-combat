package model.kryo;

import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.minlog.Log;

import javax.swing.*;
import java.io.IOException;

/**
 * Created by Nathan on 1/10/2016.
 */
public class KryoClient extends JFrame {
    Client client;

    static final int PREF_WIDTH = 1024;
    static final int PREF_HEIGHT = 768;

    public KryoClient() {
        initNetwork();
        initGUI();
    }

    private void initNetwork() {
        client = new Client();

        Network.register(client);

        client.addListener(new Listener.ThreadedListener(new Listener() {
            public void received(Connection connection, Object object) {
                System.out.println("Received object: " + object);
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