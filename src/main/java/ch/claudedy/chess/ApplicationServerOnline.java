package ch.claudedy.chess;

import ch.claudedy.chess.model.Color;
import ch.claudedy.chess.network.command.client.DisconnectClientCommand;
import ch.claudedy.chess.network.command.client.MoveClientCommand;
import ch.claudedy.chess.network.command.client.SearchingGameCommand;
import ch.claudedy.chess.network.command.server.MoveServerCommand;
import ch.claudedy.chess.network.command.server.StartGameCommand;
import ch.claudedy.chess.ui.screen.model.InfoPlayer;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class ApplicationServerOnline {
    private static final List<ServiceThread> playersSearching = new ArrayList<>();
    private static final Map<String, GameInfo> playersInGame = new HashMap<>();

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

                ServiceThread client = new ServiceThread(socketForClient, is, os);
                client.start();
            }
        } finally {
            serverListener.close();
        }
    }

    private static void log(String message) {
        System.out.println(message);
    }

    @Accessors(fluent = true)
    public static class ServiceThread extends Thread {

        private final Socket socket;
        private final ObjectInputStream is;
        private final ObjectOutputStream os;
        @Getter
        @Setter
        private String uuidGame = null;
        @Setter
        @Getter
        private InfoPlayer infoPlayerNetwork = null;

        public ServiceThread(Socket socket, ObjectInputStream input, ObjectOutputStream output) {
            this.is = input;
            this.os = output;
            this.socket = socket;

            // Log
            log("New connection with client at " + socket.getLocalAddress());
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // Read data to the server (sent from client).
                    Object command = is.readObject();

                    if (command instanceof MoveClientCommand) {
                        GameInfo gameInfo = playersInGame.get(uuidGame());
                        gameInfo.getOpponent(infoPlayerNetwork.color()).os.writeObject(new MoveServerCommand().move(((MoveClientCommand) command).move()));
                        os.flush();
                    } else if (command instanceof DisconnectClientCommand) {
                        log("Client disconnected");
                        os.flush();
                        break;
                    } else if (command instanceof SearchingGameCommand) {
                        infoPlayerNetwork = ((SearchingGameCommand) command).infoPlayer();

                        if (playersSearching.size() >= 1) {
                            String uuidGame = UUID.randomUUID().toString();

                            ServiceThread opponent = playersSearching.get(0);

                            infoPlayerNetwork().color(Color.WHITE);
                            uuidGame(uuidGame);
                            opponent.infoPlayerNetwork().color(Color.BLACK);
                            opponent.uuidGame(uuidGame);

                            playersInGame.put(uuidGame, new GameInfo().whitePlayer(this).blackPlayer(opponent));

                            os.writeObject(new StartGameCommand().player(infoPlayerNetwork).playerOpponent(opponent.infoPlayerNetwork()));
                            opponent.os.writeObject(new StartGameCommand().player(opponent.infoPlayerNetwork).playerOpponent(infoPlayerNetwork()));
                            os.flush();
                            opponent.os.flush();

                            playersSearching.remove(opponent);
                        } else {
                            playersSearching.add(this);
                        }
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

    @Accessors(fluent = true)
    @Data
    static class GameInfo {
        private ApplicationServerOnline.ServiceThread whitePlayer;
        private ApplicationServerOnline.ServiceThread blackPlayer;

        public ApplicationServerOnline.ServiceThread getOpponent(Color colorPlayer) {
            return colorPlayer.isWhite() ? blackPlayer : whitePlayer;
        }
    }
}
