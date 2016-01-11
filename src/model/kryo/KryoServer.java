package model.kryo;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;

/**
 * Created by Nathan on 1/10/2016.
 */
public class KryoServer {
    Server server;

    public KryoServer() throws IOException {
        server = new Server();
        server.start();
        server.bind(54555, 54777);
        System.out.println("Server started");

        Network.register(server);

        server.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof FromClient) {
                    FromClient request = (FromClient) object;
                    System.out.println("Server received: " + request.msg);

                    FromServer response = new FromServer();
                    response.msg = "Thanks";
                    connection.sendTCP(response);
                }
            }
        });
    }

    public static void main(String[] args) {
        try {
            new KryoServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
