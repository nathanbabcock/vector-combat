package network;

import javax.swing.*;
import java.io.EOFException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Nathan on 9/12/2015.
 */
public class Client {
    String clientName;
    Socket server;
    ObjectOutputStream out;
    ObjectInputStream in;

    public Client(String host, int port, String username) {
        this.clientName = username;

//        if (host == null || port == null || clientName == null)
//            return;

        try {
            // Open a connection to the server
            server = new Socket(host, port);
            out = new ObjectOutputStream(server.getOutputStream());
            in = new ObjectInputStream(server.getInputStream());

            // Handle duplicate client names
            while (true) {
                out.writeObject(clientName);
                if ((boolean) in.readObject() == true) {
                    System.out.println("Connection accepted by server " + host + " on port " + port + " with username " + clientName);
                    break;
                } else {
                    System.out.println("Server denied connection; duplicate username " + clientName);
                    clientName = JOptionPane.showInputDialog("A player with that username is already connected to the server.\nPlease choose a different user name:");
                }
            }

/*            // add a listener that sends a disconnect command to when closing
            this.addWindowListener(new WindowAdapter(){
                public void windowClosing(WindowEvent arg0) {
                    close();
                }
            });*/

            // start a thread for handling server events
            new Thread(new ServerHandler()).start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * This class reads and executes commands sent from the server
     *
     * @author Gabriel Kishi
     */
    private class ServerHandler implements Runnable {
        @SuppressWarnings("unchecked")
        public void run() {
            try {
                while (true) {
/*                    // read a command from server and execute it
                    Command<Multiplayer> c = (Command<Multiplayer>)in.readObject();
                    c.execute(Multiplayer.this);*/
                    System.out.println(in.readObject());
                }
            } catch (SocketException | EOFException e) {
                return; // "gracefully" terminate after disconnect
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Client("localhost", 9001, "excalo");
    }
}
