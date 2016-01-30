package model.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import model.Game;
import model.Player;
import network.ChatMessage;
import network.InputState;
import network.SpawnParams;

import java.io.ByteArrayOutputStream;
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
        init_network();
        init_game();
    }


    private int sizeof(Object obj) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        Kryo kryo = new Kryo();
        Output out = new Output(bos);
        kryo.writeObject(out, obj);
        out.flush();
        out.close();

        return bos.toByteArray().length;
    }

    private void init_network() {
        connections = new CopyOnWriteArrayList<>();
        newMsgs = new ArrayList();

        server = new Server() {
            @Override
            protected Connection newConnection() {
                return new PlayerConnection();
            }
        };

        Network.register(server);
        server.start();

        try {
            server.bind(Network.TCP_PORT, Network.UDP_PORT);
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
                game.players.remove(((PlayerConnection) connection).player);
                System.out.println(((PlayerConnection) connection).player.clientName + " disconnected");
            }
        });
    }

    private void init_game() {
        game = new Game();
        game.setMap("Map2");
        new GameUpdater().start();
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

    private class GameUpdater extends Thread {
        public GameUpdater() {
            setName("Server: Game Updater");
        }

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

                // DEBUG
                try {
                    System.out.println("Server gamestate snapshot size = " + sizeof(game));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Send to clients
                for (Connection con : connections) {
                    // Gamestate
                    game.sent = System.currentTimeMillis();
                    con.sendUDP(game);

                    // Chat
                    for (ChatMessage msg : newMsgs) {
//                        System.out.println("Sending new message to client");
                        con.sendTCP(msg);
                    }
                    newMsgs = new ArrayList();
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
        new KryoServer();
    }
}

