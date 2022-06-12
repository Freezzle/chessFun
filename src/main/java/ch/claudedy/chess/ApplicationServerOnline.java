package ch.claudedy.chess;

import ch.claudedy.chess.model.enumeration.Color;
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
import java.util.logging.Level;
import java.util.logging.Logger;

public class ApplicationServerOnline {
    private static final List<ServiceThread> playersSearching = new ArrayList<>();
    private static final Map<String, GameInfo> playersInGame = new HashMap<>();
    private static final Logger LOG = Logger.getLogger(ApplicationServerOnline.class.getName());

    public static void main(String[] args) throws IOException {
        LOG.log(Level.INFO, "Launching the server");
        ServerSocket serverListener = null;

        try {
            serverListener = new ServerSocket(8888);
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Cannot get the server socket", e);
            System.exit(1);
        }

        try {
            while (true) {
                LOG.log(Level.INFO, "Waiting to accept new client");
                Socket socketForClient = serverListener.accept();
                String uuidClient = UUID.randomUUID().toString();

                LOG.log(Level.INFO, "New client connected -> " + uuidClient);

                ObjectOutputStream os = new ObjectOutputStream(socketForClient.getOutputStream());
                ObjectInputStream is = new ObjectInputStream(socketForClient.getInputStream());

                ServiceThread client = new ServiceThread(socketForClient, is, os, uuidClient);
                client.start();
            }
        } finally {
            serverListener.close();
        }
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

        @Getter
        private final String uuidClient;

        public ServiceThread(Socket socket, ObjectInputStream input, ObjectOutputStream output, String uuidClient) {
            this.is = input;
            this.os = output;
            this.socket = socket;
            this.uuidClient = uuidClient;

            LOG.log(Level.INFO, "Configuration client done -> " + uuidClient);
        }

        @Override
        public void run() {
            try {
                LOG.log(Level.INFO, "Running client -> " + uuidClient);
                while (true) {
                    Object command = is.readObject();

                    if (command instanceof MoveClientCommand) {
                        LOG.log(Level.INFO, "Command MoveClientCommand from client -> " + uuidClient);

                        GameInfo gameInfo = playersInGame.get(uuidGame());
                        gameInfo.getOpponent(infoPlayerNetwork.color()).os.writeObject(new MoveServerCommand().move(((MoveClientCommand) command).move()));
                        os.flush();
                    } else if (command instanceof DisconnectClientCommand) {
                        LOG.log(Level.INFO, "Client disconnected -> " + uuidClient);
                        os.flush();
                        break;
                    } else if (command instanceof SearchingGameCommand) {
                        LOG.log(Level.INFO, "Command SearhcingGameCommand from client -> " + uuidClient);
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
                            LOG.log(Level.INFO, "Game created for clients -> " + opponent.uuidClient + " AND " + uuidClient);
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
