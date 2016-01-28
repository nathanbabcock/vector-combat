package model.kryo;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import model.Game;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Nathan on 1/10/2016.
 */
public class KryoServer {
    Server server;
    Game game;
    ArrayList<Connection> connections;

    long lastFpsTime;
    int fps;
    final int TARGET_FPS = 60;

    public KryoServer() throws IOException {
        connections = new ArrayList();
        game = new Game();

        server = new Server();
        Network.register(server);
        server.start();
        server.bind(Network.PORT1, Network.PORT2);
        System.out.println("Server started");

        server.addListener(new Listener() {
            @Override
            public void connected(Connection connection) {
                super.connected(connection);
                connection.addListener(new ThreadedListener(new ClientListener()));
            }

            @Override
            public void disconnected(Connection connection) {
                super.disconnected(connection);
                connections.remove(connection);
                System.out.println("Somebody disconnected");
            }
        });

        new GameUpdater().run();
    }

    private class ClientListener extends Listener {
        String clientName;

        @Override
        public void received(Connection connection, Object o) {
            super.received(connection, o);

            // Need to receive clientName
            if (clientName == null) {
                if (o instanceof String) {
                    clientName = (String) o;
                    System.out.println(clientName + " connected.");
                    connections.add(connection);
                }
            }

            // Already have clientName
            else {
                System.out.println("Received object " + o + " before clientName");
            }
        }
    }

    private class GameUpdater implements Runnable {
        public void run() {
            long lastLoopTime = System.nanoTime();
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
                    lastFpsTime = 0;
                    fps = 0;
                }

                // update the game logic
                game.update(OPTIMAL_TIME / 1000000000f);

                // Send to clients
                for (Connection con : connections) {
                    System.out.println("Sending gamestate");
                    con.sendUDP(game);
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
    }

/*    private class PlayerConnection extends Connection {
        Player player;

        public PlayerConnection(){
            player = new Player();
        }
    }*/

    public static void main(String[] args) {
        try {
            new KryoServer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

