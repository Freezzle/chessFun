package ch.claudedy.chess.ui.manager;

import ch.claudedy.chess.model.MoveCommand;
import ch.claudedy.chess.system.StockFish;
import ch.claudedy.chess.system.SystemSettings;
import ch.claudedy.chess.util.FenConverter;
import lombok.experimental.Accessors;

@Accessors(fluent = true)
public class AIManager {
    private static AIManager instance;

    private StockFish stockFish;
    private boolean isThinking = false;

    private AIManager() {
    }

    public static AIManager instance() {
        if (instance == null) {
            instance = new AIManager();
        }

        return instance;
    }

    public StockFish stockFish() {
        if (instance().stockFish == null) {
            instance().stockFish = new StockFish();
        }

        return instance().stockFish;
    }

    public void startStockfish() {
        stockFish().startEngine();
    }

    public static MoveCommand getMove() {
        return MoveCommand.convert(instance.stockFish().getBestMove(FenConverter.boardToFen(GameManager.instance().currentBoard()), SystemSettings.MOVETIME_STOCKFISH));
    }

    public boolean isComputerThinking() {
        if (GameManager.instance().gameTypeLocal().containsInLessAComputer()) {
            return instance().isThinking;
        }
        return false;
    }

    public void computerIsThinking(boolean isThinking) {
        instance().isThinking = isThinking;
    }
}
