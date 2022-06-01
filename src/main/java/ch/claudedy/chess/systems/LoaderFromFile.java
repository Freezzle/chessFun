package ch.claudedy.chess.systems;

import ch.claudedy.chess.basis.Chess;
import ch.claudedy.chess.basis.MoveCommand;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.logging.Logger;

public class LoaderFromFile {
    private static final Logger LOG = Logger.getLogger("LoaderFromFile");

    public static Chess readFile(String fileName) {
        String fen = null;
        MoveCommand previousMove = null;

        try (BufferedReader br = new BufferedReader(new FileReader(getFileFromResource(fileName)))) {
            String line = br.readLine();
            String[] values = line.split(";");

            if(values.length > 0) {
                fen = values[0];
            }

            if(values.length > 1 && !values[1].equals("-")) {
                previousMove = MoveCommand.convert(values[1]);
            }
        } catch (Exception e) {
            LOG.severe(e.getMessage());
        }

        return new Chess(fen, previousMove);
    }

    private static File getFileFromResource(String fileName) throws URISyntaxException {

        ClassLoader classLoader = LoaderFromFile.class.getClassLoader();
        URL resource = classLoader.getResource(fileName);
        if (resource == null) {
            throw new IllegalArgumentException("file not found! " + fileName);
        } else {
            return new File(resource.toURI());
        }
    }
}
