package network;

import model.Game;
import model.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Nathan on 9/12/2015.
 */
public class OldServer {
    ServerSocket socket;
    private HashMap<String, ObjectOutputStream> outputs;
    Game game;
    long lastFpsTime;
    int fps;
    final int TARGET_FPS = 60;
    List<ChatMessage> newMsgs;

    public OldServer(int port) {
        outputs = new HashMap();
        newMsgs = new ArrayList();

        try {
            // start a new server on port 9001
            socket = new ServerSocket(port);
            System.out.println("OldServer started on port " + port);

            // spawn a client accepter thread
            Thread clientAccepter = new Thread(new ClientAccepter());
            clientAccepter.setName("OldServer: OldClient accepter");
            clientAccepter.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        game = new Game();
        game.setMap("Map2");
        Thread gameUpdater = new Thread(new GameUpdater());
        gameUpdater.setName("OldServer: Game updater");
        gameUpdater.start();
    }

    /**
     * This thread listens for and sets up connections to new clients
     */
    private class ClientAccepter implements Runnable {
        public void run() {
            try {
                while (true) {
                    // accept a new client, get output & input streams
                    Socket s = socket.accept();
                    ObjectOutputStream output = new ObjectOutputStream(s.getOutputStream());
                    ObjectInputStream input = new ObjectInputStream(s.getInputStream());

                    // Handle duplicate client names
                    String clientName;
                    while (true) {
                        clientName = (String) input.readObject();
                        if (outputs.containsKey(clientName)) {
                            System.out.println("Refused connection from client with duplicate username " + clientName);
                            output.writeObject(false);
                        } else {
                            //							System.out.println(clientName + " has connected");
                            output.writeObject(true);
                            break;
                        }
                    }

                    // map client name to output stream
                    outputs.put(clientName, output);

                    // spawn a thread to handle communication with this client
                    Thread clientHandler = new Thread(new ClientHandler(input, output, clientName));
                    clientHandler.setName("OldServer: OldClient handler (" + clientName + ")");
                    clientHandler.start();

                    // init player
                    Player player = new Player(game, clientName);
                    game.players.add(player);

                    // print message
                    // TODO add to chat
                    System.out.println(clientName + " connected");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This thread reads and executes commands sent by a client
     */
    private class ClientHandler implements Runnable {
        private ObjectInputStream input;
        private ObjectOutputStream output;
        private String clientName;
        int chatSent;

        public ClientHandler(ObjectInputStream input, ObjectOutputStream output, String clientName) {
            this.input = input;
            this.output = output;
            this.clientName = clientName;
            chatSent = 0;
        }

        @SuppressWarnings("unchecked")
        public void run() {
            long startTime;
            try {
                while (true) {
                    startTime = System.currentTimeMillis();

                    // Part 1: Send to client
                    output.reset();
                    output.writeObject(game); // Gamestate
                    while (chatSent < game.chat.size())
                        output.writeObject(game.chat.get(chatSent++)); // Chat

                    // Part 2: Receieve from client
                    Object received = input.readObject();
                    if (received instanceof InputState) {
                        if (game.getPlayer(clientName) == null)
                            continue;
                        if (game.getPlayer(clientName).character != null)
                            game.getPlayer(clientName).character.importState((InputState) received);
                    } else if (received instanceof SpawnParams) {
                        Player player = game.getPlayer(clientName);
                        player.importSpawnParams((SpawnParams) received);
                        if (player.character != null) player.kill();
                    } else if (received instanceof ChatMessage) {
                        ChatMessage msg = (ChatMessage) received;
                        System.out.println(msg.player + ": " + msg.content);
                        game.chat.add(msg);
                        newMsgs.add(msg);
                    } else
                        System.out.println(received);

                    // Record ping
                    int ping = (int) Math.min(System.currentTimeMillis() - startTime, 999);
                    final int MIN_PING = 1000 / TARGET_FPS; // Don't need to ping any faster than the server actually updates the gamestate
                    if (ping < MIN_PING)
                        Thread.sleep(MIN_PING - ping);
                    ping = (int) Math.min(System.currentTimeMillis() - startTime, 999);
                    game.getPlayer(clientName).ping = ping;

                }
            } catch (SocketException e) {
                // Disconnect client, suppress errors
                System.out.println(clientName + " disconnected"); // TODO add to chat
                game.players.remove(game.getPlayer(clientName));
                outputs.remove(clientName);
                try {
                    input.close();
                    output.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
                return;
            } catch (Exception e) {
                e.printStackTrace();
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
//                game.lock();
                game.update(OPTIMAL_TIME / 1000000000f);

                // send state to all clients
                /*for (ObjectOutputStream output : outputs.values()) {
                    try {
                        output.writeObject(ObjectCloner.deepCopy(game));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    for (ChatMessage msg : newMsgs) {
                        try {
                            output.writeObject(msg);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                newMsgs = new ArrayList();*/

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

    public static void main(String[] args) {
        new OldServer(9001);
        OldClient cl = new OldClient("localhost", 9001, "excalo");
//        try {
//            Thread.sleep(15);
//        } catch (Exception e) {
//            ;
//        }
//        cl.setVisible(true);
//        new OldClient("localhost", 9001, "nathansbrother");
//        new OldClient("localhost", 9001, "3");
//        new OldClient("localhost", 9001, "4");
//        new OldClient("localhost", 9001, "5");
//        new OldClient("localhost", 9001, "6");
//        new OldClient("localhost", 9001, "7");
//        new OldClient("localhost", 9001, "8");
//        new OldClient("localhost", 9001, "9");
//        new OldClient("localhost", 9001, "10");
    }
}
