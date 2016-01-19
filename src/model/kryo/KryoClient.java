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

    public KryoClient() throws IOException {
        client = new Client();
        client.start();
        Network.register(client);

        client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof FromServer) {
                    FromServer response = (FromServer) object;
                    System.out.println("Client received: " + response.msg);
                }
            }
        });

        client.connect(5000, "localhost", 54555, 54777);

        FromClient request = new FromClient();
        request.msg = "Here is the request";
        client.sendUDP(request);
    }

    public static void main(String[] args) {
        Log.set(Log.LEVEL_DEBUG);
        try {
            new KryoClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
