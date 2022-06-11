package ch.claudedy.chess.ui.screen;

import ch.claudedy.chess.basis.Color;
import ch.claudedy.chess.basis.Piece;
import ch.claudedy.chess.basis.Tile;
import lombok.experimental.Accessors;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;


@Accessors(fluent = true)
public class SquareUI extends JPanel {
    private static final java.awt.Color BLACK_SQUARE = new java.awt.Color(120, 144, 180, 255);
    private static final java.awt.Color WHITE_SQUARE = new java.awt.Color(255, 255, 255);

    private static final java.awt.Color ANALYSE_CLICKED_BLACK_SQUARE = new java.awt.Color(200, 100, 90);
    private static final java.awt.Color ANALYSE_CLICKED_WHITE_SQUARE = new java.awt.Color(215, 105, 95);

    private final Map<String, ImageIcon> piecesImages = new HashMap<>();

    private final Tile tile;
    private final java.awt.Color defaultColor;
    private boolean isAnalysed = false;

    private boolean isPainted = false;

    public SquareUI(Tile tile) {
        super(new BorderLayout());
        this.tile = tile;
        this.setName(tile.name());

        if (tile.color() == Color.BLACK) {
            defaultColor = BLACK_SQUARE;
        } else {
            defaultColor = WHITE_SQUARE;
        }
        this.setBackground(defaultColor);
    }

    public void updatePiece(Piece piece) {
        if (piece != null) {
            String namePiece = piece.type().abrevUniversal() + "_" + piece.color();

            if (getComponentCount() != 0 && namePiece.equals(getComponent(0).getName())) {
                return;
            }

            if (getComponentCount() != 0) {
                remove(0);
                doLayout();
            }

            ImageIcon imagePiece = piecesImages.get(namePiece);
            if (imagePiece == null) {
                imagePiece = new ImageIcon(ClassLoader.getSystemResource("images/" + namePiece + ".png"));
                piecesImages.put(namePiece, imagePiece);
            }

            JLabel pieceLabel = new JLabel(imagePiece);
            pieceLabel.setName(namePiece);
            setFocusable(false);
            add(pieceLabel, 0);
            doLayout();
        } else {
            if (getComponentCount() != 0) {
                remove(0);
                doLayout();
            }

            setFocusable(true);
        }
    }

    public void changeBackground(java.awt.Color color) {
        setBackground(color);
        this.isPainted = true;
        this.isAnalysed = false;
    }

    public void resetColor() {
        if (!this.isPainted) {
            return;
        }

        this.setBackground(defaultColor);
        this.isPainted = false;
        this.isAnalysed = false;
    }

    public void clickForAnalyse() {
        if (!this.isAnalysed) {
            if (tile.color() == Color.BLACK) {
                this.changeBackground(ANALYSE_CLICKED_BLACK_SQUARE);
            } else {
                this.changeBackground(ANALYSE_CLICKED_WHITE_SQUARE);
            }
            this.isAnalysed = true;
        } else {
            this.resetColor();
            this.isAnalysed = false;
        }
    }
}
