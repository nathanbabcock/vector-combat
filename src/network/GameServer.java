package network;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;
import model.Game;
import model.Player;
import model.Sprite;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Created by Nathan on 1/10/2016.
 */
public class GameServer {
    Server server;
    Game game;
    List<Connection> connections;
    List<ChatMessage> newMsgs;

    final int VID_FPS = 60;
    final int NET_FPS = 20;
    final int MAX_PING_HISTORY = 60; // Number of ping times to save per player
    float TIMESCALE = Network.TIMESCALE;

    public GameServer() {
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
        Sprite.initSprites();
        game.setMap("ctf_space");
//        new GameUpdater().start();

        final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
        scheduler.scheduleAtFixedRate(new GameTick(), 0, 1000 / VID_FPS, TimeUnit.MILLISECONDS);
        scheduler.scheduleAtFixedRate(new NetworkTick(), 0, 1000 / NET_FPS, TimeUnit.MILLISECONDS);
    }

    private class ClientListener extends Listener {
        @Override
        public void received(Connection connection, Object o) {
            super.received(connection, o);
            try {
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
                        InputState i = (InputState) o;

                        // Ping
                        if (!con.player.pings.isEmpty()) {
                            while (con.player.pings.peek().tick < i.lastTick) // ERROR
                                con.player.pings.remove();
                            con.player.ping = (int) (System.currentTimeMillis() - con.player.pings.remove().time);
                        }

                        // Input
                        if (con.player.character != null) con.player.character.importState(i);
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
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class GameTick implements Runnable {
        @Override
        public void run() {
//            System.out.println("game tick");
            game.update(TIMESCALE / VID_FPS);
        }
    }

    private class NetworkTick implements Runnable {
        @Override
        public void run() {
//            System.out.println("network tick");
            for (Connection con : connections) {
                Player player = ((PlayerConnection) con).player;
                player.pings.add(new Ping(game.net_tick, System.currentTimeMillis()));
                while (player.pings.size() >= MAX_PING_HISTORY)
                    player.pings.remove();
//                System.out.println(player.pings.size()); // DEBUG

                // Gamestate
                con.sendUDP(game);

                // Chat
                for (ChatMessage msg : newMsgs)
                    con.sendTCP(msg);
                newMsgs = new ArrayList();
            }
            game.net_tick++;
        }
    }

    private class GameUpdater extends Thread {
        //        final int VID_FPS = 60; // Number of times per second both GAME LOGIC and RENDERING occur
//        final int NET_FPS = 20; // Number of times per second input is sent to the server
        final int VID_NET_RATIO = VID_FPS / NET_FPS;
        final int FRAME_TIME = 1000 / VID_FPS; // Expected time for each frame from in milliseconds

        public GameUpdater() {
            setName("Server: Game updater");
        }

        @Override
        public void run() {
            long startTime;
            short frameNo = 0;
            int overflow = 0; // If a frame takes than usual, the next frame will compensate

            while (true) {
                startTime = System.currentTimeMillis();
//                System.out.println("Frame " + frameNo + " ========");

                // Part 1: Update model
//                System.out.println("game tick");


                // Part 2: Send snapshot to clients
                if (frameNo % VID_NET_RATIO == 0) {
//
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

    private class PlayerConnection extends Connection {
        Player player;
    }

    public static void main(String[] args) {
        new GameServer();
        GameClient client = new GameClient("excalo", "68.230.58.93", Network.TCP_PORT, Network.UDP_PORT);
//        new GameClient("asdf", "68.230.58.93", Config.TCP_PORT, Config.UDP_PORT);
//        try {
//            Thread.sleep(250);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        client.setVisible(true);
    }
}

