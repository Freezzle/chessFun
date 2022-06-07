package ch.claudedy.chess.systems;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Logger;

public class StockFish {

    private static final URL URL = ClassLoader.getSystemResource("engine/stockfish.exe");
    private static final Logger LOG = Logger.getLogger("StockFish");

    private Process engineProcess;
    private BufferedReader processReader;
    private OutputStreamWriter processWriter;

    public void startEngine() {
        try {
            engineProcess = Runtime.getRuntime().exec(URL.getFile());
            processReader = new BufferedReader(new InputStreamReader(engineProcess.getInputStream()));
            processWriter = new OutputStreamWriter(engineProcess.getOutputStream());
            command("uci", Function.identity(), (s) -> s.startsWith("uciok"), 2000l);
            command("setoption name UCI_LimitStrength value true", Function.identity(), (s) -> s.startsWith("readyok"), 100l);
            command("setoption name UCI_Elo value " + SystemConfig.ELO_COMPUTER, Function.identity(), s -> s.startsWith("readyok"), 100l);
        } catch (Exception e) {
            LOG.severe(e.getMessage());
        }

    }

    public String getBestMove(String fen, long waitTime) {
        try {
            command("position fen " + fen, Function.identity(), s -> s.startsWith("readyok"), 300l);
            return command("go movetime " + waitTime,
                    lines -> lines.stream().filter(s -> s.startsWith("bestmove")).findFirst().get(),
                    line -> line.startsWith("bestmove"), waitTime + 300l).split(" ")[1];
        } catch (Exception e) {
            LOG.severe(e.getMessage());
        }

        return "";
    }

    public void stopEngine() {
        try {
            command("quit", Function.identity(), null, 100l);
            processReader.close();
            processWriter.close();
        } catch (Exception e) {
            LOG.severe(e.getMessage());
        }
    }

    public <T> T command(String cmd, Function<List<String>, T> commandProcessor, Predicate<String> breakCondition, long timeout)
            throws InterruptedException, ExecutionException, TimeoutException {

        // This completable future will send a command to the process
        // And gather all the output of the engine in the List<String>
        // At the end, the List<String> is translated to T through the
        // commandProcessor Function
        CompletableFuture<T> command = CompletableFuture.supplyAsync(() -> {
            final List<String> output = new ArrayList<>();
            try {
                processWriter.flush();
                processWriter.write(cmd + "\n");
                processWriter.write("isready\n");
                processWriter.flush();
                String line = "";
                while ((line = processReader.readLine()) != null) {
                    if (line.contains("Unknown command")) {
                        throw new RuntimeException(line);
                    }
                    if (line.contains("Unexpected token")) {
                        throw new RuntimeException("Unexpected token: " + line);
                    }
                    output.add(line);
                    if (breakCondition == null || breakCondition.test(line)) {
                        // At this point we are no longer interested to read any more
                        // output from the engine, we consider that the engine responded
                        break;
                    }
                }
            } catch (IOException e) {
                LOG.severe(e.getMessage());
            }
            return commandProcessor.apply(output);
        });

        return command.get(timeout, TimeUnit.MILLISECONDS);
    }
}
