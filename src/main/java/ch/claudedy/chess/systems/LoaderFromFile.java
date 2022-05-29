package ch.claudedy.chess.systems;

import ch.claudedy.chess.basis.MoveCommand;
import ch.claudedy.chess.basis.Tile;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

public class LoaderFromFile {
    public static DataForLoadingBoard readFile(String fileName) {
        DataForLoadingBoard fileWrapper = null;
        try (BufferedReader br = new BufferedReader(new FileReader(getFileFromResource(fileName)))) {
            String line = br.readLine();
            String[] values = line.split(";");

            MoveCommand previousMove = null;
            if(!values[1].equals("-")) {
                String[] positions = values[1].split("-");
                previousMove = new MoveCommand(Tile.valueOf(positions[0]), Tile.valueOf(positions[1]), null);
            }

            fileWrapper = new DataForLoadingBoard(values[0], previousMove);

        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }

        return fileWrapper;
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
