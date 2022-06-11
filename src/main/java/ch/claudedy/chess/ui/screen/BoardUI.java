package ch.claudedy.chess.ui.screen;

import ch.claudedy.chess.basis.Color;
import ch.claudedy.chess.basis.*;
import ch.claudedy.chess.ui.delegate.AIDelegate;
import ch.claudedy.chess.ui.delegate.ChessDelegate;
import ch.claudedy.chess.ui.delegate.NetworkDelegate;
import ch.claudedy.chess.ui.listener.MoveDoneListener;
import ch.claudedy.chess.ui.listener.MoveFailedListener;
import lombok.experimental.Accessors;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.claudedy.chess.ui.ColorationUI.*;


@Accessors(fluent = true)
public class BoardUI extends JPanel {
    private static final int LEFT_CLICK = 1;
    private static final int RIGHT_CLICK = 3;

    private final Map<String, Integer> squaresBoardUI = new HashMap<>();
    private final List<MoveDoneListener> moveDoneListeners = new ArrayList<>();
    private final List<MoveFailedListener> moveFailedListeners = new ArrayList<>();

    private Tile selectedPieceTile;

    public BoardUI(boolean whiteView) {

        this.setName("BOARD");
        this.setLayout(new GridLayout(8, 8));
        this.setPreferredSize(new Dimension(600, 600));
        this.setBounds(new Rectangle(0, 50, 600, 600));
        this.addMouseListener(new MouseListener() {

            // MOUSE CLICKED
            public void mouseClicked(MouseEvent e) {
                if (ChessDelegate.chess().gameStatus().isGameOver()) {
                    return;
                }

                // Search the clicked tile on the board itself
                Component tileClicked = getTileUI(e.getX(), e.getY());
                if (tileClicked != null) {

                    int buttonClicked = e.getButton();
                    if (selectedPieceTile == null && buttonClicked == LEFT_CLICK && !AIDelegate.isComputerThinking()) { // There is no selected piece and we click left on board (select a piece)
                        // If the click was on a empty tile (cause piece is a JLabel)
                        if (tileClicked instanceof JPanel) {
                            resetBackgroundTiles();
                            return;
                        }

                        // We take the parent name (the tile of the piece)
                        selectedPieceTile = Tile.getEnum(tileClicked.getParent().getName());

                        if (selectedPieceTile == null) {
                            selectedPieceTile = Tile.getEnum(tileClicked.getName());
                        }

                        // Color the tile of the piece
                        getComponentUI(selectedPieceTile).changeBackground(getColorTileForSelectedPiece(selectedPieceTile.color()));

                        // Show the legal moves for the selected piece
                        colorizeLegalMoves(ChessDelegate.chess().getLegalMoves(selectedPieceTile));
                    } else if (selectedPieceTile != null && buttonClicked == LEFT_CLICK && !AIDelegate.isComputerThinking()) { // There is already a selected piece and we click left on board again (make move)
                        Tile destination = Tile.getEnum(tileClicked.getName());

                        if (destination == null) {
                            destination = Tile.getEnum(tileClicked.getParent().getName());
                        }

                        makeMoveUI(selectedPieceTile, destination, true);
                    } else if (e.getButton() == RIGHT_CLICK) { // There is no selected piece and we click right (print square to analyse board)
                        Tile tileSelected = Tile.getEnum(tileClicked.getName());
                        if (tileSelected == null) {
                            tileSelected = Tile.getEnum(tileClicked.getParent().getName());
                        }

                        // Let's manage the click on the tile (to set a background etc..)
                        getComponentUI(tileSelected).clickForAnalyse();
                    } else { // Reset the board
                        selectedPieceTile = null;
                        resetBackgroundTiles();
                        showKingChecked();
                    }
                }
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });

        Board currentBoard = ChessDelegate.currentBoard();
        if (whiteView) {
            int counter = 0;
            for (int y = 7; y >= 0; y--) {
                for (int x = 0; x <= 7; x++) {
                    createSquare(ChessDelegate.currentBoard().squares()[x][y], counter);
                    counter++;
                }
            }
        } else {
            int counter = 0;
            for (int y = 0; y <= 7; y++) {
                for (int x = 7; x >= 0; x--) {
                    createSquare(currentBoard.squares()[x][y], counter);
                    counter++;
                }
            }
        }
    }

    public void makeMoveUI(Tile start, Tile destination, boolean fromLocalCommand) {
        if (NetworkDelegate.getInstance().isModeOnline() && fromLocalCommand && !ChessDelegate.currentBoard().currentPlayer().isSameColor(NetworkDelegate.getInstance().colorPlayer())) {
            moveFailedListeners.forEach(listener -> listener.onMoveFailedListener(MoveStatus.CANT_MOVE_DURING_ANOTHER_MOVE));
            return;
        }

        // Make the move
        MoveCommand moveCommand = new MoveCommand(start, destination, null);
        MoveStatus status = ChessDelegate.chess().makeMove(new MoveCommand(start, destination, null));
        if (status.isOk()) {
            moveDoneListeners.forEach(listener -> listener.onMoveDoneListener(moveCommand, fromLocalCommand));
        } else {
            moveFailedListeners.forEach(listener -> listener.onMoveFailedListener(status));
        }
    }

    public void resetBoard() {
        initSelectedPieceTile();
        resetBackgroundTiles();
        printPieces();
        showKingChecked();

        repaint();
    }

    private void showKingChecked() {
        // Search the WHITE KING and if he is in check, we color his tile
        Tile tileWhiteKing = ChessDelegate.currentBoard().getTileKing(Color.WHITE);
        if (ChessDelegate.currentBoard().isTileChecked(Color.WHITE, tileWhiteKing)) {
            java.awt.Color color = tileWhiteKing.isWhiteTile() ? THREATED_KING_WHITE_SQUARE : THREATED_KING_BLACK_SQUARE;
            getComponentUI(tileWhiteKing).changeBackground(color);
        }

        // Search the BLACK KING and if he is in check, we color his tile
        Tile tileBlackKing = ChessDelegate.currentBoard().getTileKing(Color.BLACK);
        if (ChessDelegate.currentBoard().isTileChecked(Color.BLACK, tileBlackKing)) {

            java.awt.Color color = tileBlackKing.isWhiteTile() ? THREATED_KING_WHITE_SQUARE : THREATED_KING_BLACK_SQUARE;
            getComponentUI(tileBlackKing).changeBackground(color);
        }
    }

    public void createSquare(Square squareChess, Integer counter) {
        SquareUI squareUI = new SquareUI(squareChess.tile());
        this.add(squareUI, counter);
        squaresBoardUI.put(squareUI.getName(), counter);
    }


    private synchronized void initSelectedPieceTile() {
        if (selectedPieceTile != null) {
            // Reset color for the selected piece tile
            getComponentUI(selectedPieceTile).resetColor();
        }

        selectedPieceTile = null;
    }

    private synchronized void resetBackgroundTiles() {
        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x <= 7; x++) {
                getComponentUI(ChessDelegate.currentBoard().squares()[x][y].tile()).resetColor();
            }
        }

        this.printPreviousMove(ChessDelegate.chess().actualMove());
    }


    private synchronized void printPreviousMove(MoveCommand move) {

        // Reset background previous move
        if (ChessDelegate.chess().actualMove() != null) {
            MoveCommand actualMove = ChessDelegate.chess().actualMove();

            getComponentUI(actualMove.startPosition()).resetColor();
            getComponentUI(actualMove.endPosition()).resetColor();
        }

        // Colorize the new move backgrounds
        if (move != null) {
            Tile start = move.startPosition();
            Tile end = move.endPosition();

            getComponentUI(start).changeBackground(getColorTileForPreviousMove(start.color()));
            getComponentUI(end).changeBackground(getColorTileForPreviousMove(end.color()));
        }
    }

    private synchronized void printPieces() {
        Square[][] squares = ChessDelegate.currentBoard().squares();

        for (int y = 7; y >= 0; y--) {
            for (int x = 0; x <= 7; x++) {
                Square currentSquare = squares[x][y];
                getComponentUI(currentSquare.tile()).updatePiece(currentSquare.piece());
            }
        }
    }

    private Component getTileUI(int x, int y) {
        return findComponentAt(x, y);
    }

    private SquareUI getComponentUI(Tile tile) {
        return (SquareUI) getComponent(squaresBoardUI.get(tile.name()));
    }

    private void colorizeLegalMoves(List<PossibleMove> possibleMoves) {
        possibleMoves.forEach(move -> {
            Tile destination = move.destination();

            java.awt.Color colorTile;

            if (move.type() == MoveType.THREAT || move.type() == MoveType.EN_PASSANT) {
                colorTile = destination.isWhiteTile() ? THREATED_WHITE_SQUARE : THREATED_BLACK_SQUARE;
            } else {
                colorTile = destination.isWhiteTile() ? LEGAL_MOVE_WHITE_SQUARE : LEGAL_MOVE_BLACK_SQUARE;
            }

            getComponentUI(destination).changeBackground(colorTile);
        });
    }

    private java.awt.Color getColorTileForSelectedPiece(Color colorTile) {
        return colorTile.isWhite() ? SELECTED_PIECE_WHITE_SQUARE : SELECTED_PIECE_BLACK_SQUARE;
    }

    private java.awt.Color getColorTileForPreviousMove(Color colorTile) {
        return colorTile.isWhite() ? PREVIOUS_MOVE_WHITE_SQUARE : PREVIOUS_MOVE_BLACK_SQUARE;
    }

    public void addMoveDoneListener(MoveDoneListener listener) {
        this.moveDoneListeners.add(listener);
    }

    public void addMoveFailedListener(MoveFailedListener listener) {
        this.moveFailedListeners.add(listener);
    }
}
