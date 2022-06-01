package ch.claudedy.chess;

import ch.claudedy.chess.basis.*;
import ch.claudedy.chess.systems.ConsolePrint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ApplicationConsole {

    public static void main(String[] args) throws IOException {
        Chess chess = new Chess("r1b2b1r/pp3Qp1/2nkn2p/3ppP1p/P1p5/1NP1NB2/1PP1PPR1/1K1R3q w - - 0 1", null);
        ConsolePrint.execute(chess);

        while (true) {
            System.out.println();
            System.out.print("Move (E2E4, E7E8R (promote)): ");
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String move = reader.readLine();

            MoveFeedBack status = chess.makeMove(MoveCommand.convert(move));
            if (status != MoveFeedBack.RUNNING) {
                System.out.println(status);
            }
        }
    }
}
