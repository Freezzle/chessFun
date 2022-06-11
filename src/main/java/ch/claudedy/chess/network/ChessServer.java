package ch.claudedy.chess.network;

import ch.claudedy.chess.basis.Color;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class ChessServer {

    private static ServiceThread whitePlayer;
    private static ServiceThread blackPlayer;

    public static void main(String[] args) throws IOException {

        ServerSocket serverListener = null;

        System.out.println("Server is waiting to accept user...");
        int clientNumber = 0;

        // Try to open a server socket on port 7777
        // Note that we can't choose a port less than 1023 if we are not
        // privileged users (root)

        try {
            serverListener = new ServerSocket(8888);
        } catch (IOException e) {
            System.out.println(e);
            System.exit(1);
        }

        try {
            while (true) {
                // Accept client connection request
                // Get new Socket at Server.
                Socket socketForClient = serverListener.accept();

                ObjectOutputStream os = new ObjectOutputStream(socketForClient.getOutputStream());
                ObjectInputStream is = new ObjectInputStream(socketForClient.getInputStream());

                if (clientNumber == 0) {
                    whitePlayer = new ServiceThread(socketForClient, Color.WHITE, is, os);
                    whitePlayer.start();
                } else {
                    blackPlayer = new ServiceThread(socketForClient, Color.BLACK, is, os);
                    blackPlayer.start();

                    whitePlayer.os.writeObject(new StartGameCommand().colorPlayer(Color.WHITE));
                    blackPlayer.os.writeObject(new StartGameCommand().colorPlayer(Color.BLACK));
                    whitePlayer.os.flush();
                    blackPlayer.os.flush();

                }
                clientNumber++;
            }
        } finally {
            serverListener.close();
        }
    }

    private static void log(String message) {
        System.out.println(message);
    }

    private static class ServiceThread extends Thread {

        private final Color colorPlayer;
        private final Socket socket;
        private final ObjectInputStream is;
        private final ObjectOutputStream os;

        public ServiceThread(Socket socket, Color colorPlayer, ObjectInputStream input, ObjectOutputStream output) {
            this.is = input;
            this.os = output;
            this.colorPlayer = colorPlayer;
            this.socket = socket;

            // Log
            log("New connection with client# " + this.colorPlayer + " at " + socket.getLocalAddress());
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // Read data to the server (sent from client).
                    Object command = is.readObject();

                    if (command instanceof MoveClientCommand) {
                        if (colorPlayer.isWhite()) {
                            blackPlayer.os.writeObject(new MoveServerCommand().move(((MoveClientCommand) command).move()));
                        } else {
                            whitePlayer.os.writeObject(new MoveServerCommand().move(((MoveClientCommand) command).move()));
                        }
                        os.flush();
                    } else if (command instanceof DisconnectClientCommand) {
                        log("Client disconnected");
                        os.flush();
                        break;
                    }
                }

                is.close();
                os.close();
                socket.close();
            } catch (IOException e) {
                System.out.println(e);
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
