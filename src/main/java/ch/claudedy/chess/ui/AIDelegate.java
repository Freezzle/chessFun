package ch.claudedy.chess.ui;

import ch.claudedy.chess.basis.MoveCommand;
import ch.claudedy.chess.systems.StockFish;
import ch.claudedy.chess.systems.SystemConfig;
import ch.claudedy.chess.utils.FenUtils;
import lombok.experimental.Accessors;

import javax.swing.*;

@Accessors(fluent = true)
public class AIDelegate extends JFrame {

    private static AIDelegate instance;
    private StockFish stockFish;
    private boolean isThinking = false;

    private AIDelegate() {
    }

    public static StockFish stockFish() {
        if (getInstance().stockFish == null) {
            getInstance().stockFish = new StockFish();
            getInstance().stockFish.startEngine();
        }

        return getInstance().stockFish;
    }

    public static MoveCommand getMove() {
        return MoveCommand.convert(stockFish().getBestMove(FenUtils.boardToFen(ChessDelegate.chess().currentBoard()), SystemConfig.MOVETIME_STOCKFISH));
    }

    public static boolean isComputerThinking() {
        if (SystemConfig.GAME_TYPE.containsInLessAComputer()) {
            return getInstance().isThinking;
        }
        return false;
    }

    public static void computerIsThinking(boolean isThinking) {
        getInstance().isThinking = isThinking;
    }

    private static AIDelegate getInstance() {
        if (instance == null) {
            instance = new AIDelegate();
        }

        return instance;
    }
}
