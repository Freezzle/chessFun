package ch.claudedy.chess.network;

import ch.claudedy.chess.basis.MoveCommand;
import ch.claudedy.chess.ui.delegate.NetworkDelegate;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class ChessClient {

    private ObjectInputStream input;
    private ObjectOutputStream output;

    public ChessClient() {
        try {
            InetAddress ip = InetAddress.getByName("localhost");
            Socket socket = new Socket(ip, 8888);

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
                                NetworkDelegate.getInstance().colorPlayer(((StartGameCommand) response).colorPlayer());
                                NetworkDelegate.getInstance().setGameStarted(true);
                            } else if (response instanceof MoveServerCommand) {
                                MoveCommand move = ((MoveServerCommand) response).move();
                                NetworkDelegate.getInstance().game().boardUI().makeMoveUI(move.startPosition(), move.endPosition(), false);
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
