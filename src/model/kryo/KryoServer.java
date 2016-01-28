package model.kryo;

import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import com.esotericsoftware.minlog.Log;
import model.Game;
import model.Player;
import network.ChatMessage;
import network.InputState;
import network.SpawnParams;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Nathan on 1/10/2016.
 */
public class KryoServer {
    Server server;
    Game game;
    List<Connection> connections;
    List<ChatMessage> newMsgs;

    long lastFpsTime;
    int fps;
    final int TARGET_FPS = 60;

    public KryoServer() {
        connections = new CopyOnWriteArrayList<>();
        newMsgs = new ArrayList();
        game = new Game();

        server = new Server() {
            @Override
            protected Connection newConnection() {
                return new PlayerConnection();
            }
        };

        Network.register(server);
        server.start();

        try {
            server.bind(Network.PORT1, Network.PORT2);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(2);
        }

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
                System.out.println(((PlayerConnection) connection).player.clientName + " disconnected");
            }
        });

        new GameUpdater().run();
    }

    private class ClientListener extends Listener {

        @Override
        public void received(Connection connection, Object o) {
            super.received(connection, o);

            // Need to receive clientName
            PlayerConnection con = (PlayerConnection) connection;
            if (con.player == null) {
                if (o instanceof String) {
                    con.player = new Player(game, (String) o);
                    game.players.add(con.player);
                    System.out.println(o + " connected.");
                    connections.add(connection);
                } else
                    System.out.println("Unexpected object: " + o);
            }

            // Already have clientName
            else {
                if (o instanceof InputState) {
                    if (con.player.character != null) con.player.character.importState((InputState) o);
                } else if (o instanceof SpawnParams) {
                    con.player.importSpawnParams((SpawnParams) o);
                    if (con.player.character != null) con.player.kill();
                } else if (o instanceof ChatMessage) {
                    ChatMessage msg = (ChatMessage) o;
                    System.out.println(msg.player + ": " + msg.content);
                    game.chat.add(msg);
                    newMsgs.add(msg);
                } else
                    System.out.println("Unexpected object: " + o);
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

    private class PlayerConnection extends Connection {
        Player player;
    }

    public static void main(String[] args) {
        Log.set(Log.LEVEL_DEBUG);
        new KryoServer();
    }
}

