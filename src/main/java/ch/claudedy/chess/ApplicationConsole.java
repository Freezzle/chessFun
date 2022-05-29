package ch.claudedy.chess;

import ch.claudedy.chess.basis.*;
import ch.claudedy.chess.systems.ConsolePrint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ApplicationConsole {

    public static void main(String[] args) throws IOException {
        Chess chess = new Chess("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1", null);
        ConsolePrint.execute(chess);

        while (true) {
            System.out.println();
            System.out.print("start: ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String start = reader.readLine();

            System.out.print("end: ");
            String end = reader.readLine();
            System.out.println();

            MoveFeedBack status = chess.makeMove(new MoveCommand(Tile.valueOf(start), Tile.valueOf(end), null));
            if (status == MoveFeedBack.AUTHORIZED) {
                ConsolePrint.execute(chess);
            } else {
                System.out.println(status);
            }
        }
    }
}
