package ch.claudedy.chess.network;

import ch.claudedy.chess.model.MoveCommand;
import ch.claudedy.chess.network.command.client.CommandClient;
import ch.claudedy.chess.network.command.server.CommandServer;
import ch.claudedy.chess.network.command.server.MoveServerCommand;
import ch.claudedy.chess.network.command.server.StartGameCommand;
import ch.claudedy.chess.ui.manager.GameManager;
import ch.claudedy.chess.ui.manager.NetworkManager;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ChessClient {

    private ObjectInputStream input;
    private ObjectOutputStream output;

    public ChessClient() throws IOException {
        InetAddress ip = InetAddress.getByName("localhost");
        Socket socket = new Socket(ip, 8888);

        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());

            new Thread(new Runnable() {
                @Override
                public void run() {

                    while (true) {
                        try {
                            // read the message sent to this client
                            CommandServer response = (CommandServer) input.readObject();

                            if (response instanceof StartGameCommand) {
                                NetworkManager.instance().infoPlayer(((StartGameCommand) response).player()).infoOpponent(((StartGameCommand) response).playerOpponent());
                                GameManager.instance().setGameStarted(true);
                            } else if (response instanceof MoveServerCommand) {
                                MoveCommand move = ((MoveServerCommand) response).move();
                                NetworkManager.instance().game().boardUI().makeMoveUI(move.startPosition(), move.endPosition(), false);
                            }

                            System.out.println(response);
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void send(CommandClient command) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    output.writeObject(command);
                    output.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
