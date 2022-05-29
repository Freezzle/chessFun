package model.chess.basis;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;
import model.chess.feedbacks.MoveFeedBack;
import model.chess.actions.MoveCommand;
import model.chess.systems.ConsolePrint;
import model.chess.utils.FenUtils;

import java.util.ArrayList;
import java.util.List;

@Accessors(fluent = true)
@Getter
@Setter(AccessLevel.PRIVATE)
public class Chess {

    private List<HistoricalBoardFen> historicalBoards;
    private MoveCommand actualMove;
    private Board currentBoard;

    public Chess(String fen, MoveCommand previousMove) {
        this.currentBoard = FenUtils.fenToBoard(fen);
        this.historicalBoards = new ArrayList<>();
        this.actualMove = previousMove;
    }

    public MoveFeedBack makeMove(MoveCommand move) {
        MoveFeedBack thatMoveLegal = this.isThatMoveLegal(move);

        if (thatMoveLegal == MoveFeedBack.AUTHORIZED) {
            // Create a copy of the board before to make action
            this.historicalBoards.add(new HistoricalBoardFen().fen(FenUtils.boardToFen(this.currentBoard)).previousMove(this.actualMove));

            // The move action become the actual move
            this.actualMove = move;

            // Move the piece
            this.currentBoard.movePiece(move.startPosition(), move.endPosition());

            // Switch the player
            this.currentBoard.switchPlayer();
        }

        System.out.println(FenUtils.boardToFen(currentBoard()));

        return thatMoveLegal;
    }

    public void rollbackPreviousState() {
        if (this.historicalBoards.isEmpty()) {
            return;
        }

        HistoricalBoardFen previous = this.historicalBoards.get(this.historicalBoards.size() - 1);

        currentBoard = FenUtils.fenToBoard(previous.fen());
        actualMove = previous.previousMove();
        this.historicalBoards.remove(this.historicalBoards.size() - 1);

        System.out.println(FenUtils.boardToFen(currentBoard()));
    }

    public List<Tile> getLegalMoves(Tile start) {
        // Check if the start position contains a piece
        Piece piece = currentBoard.get(start).piece();
        if (piece == null) {
            return new ArrayList<>();
        }

        List<Tile> moves = piece.getMoves(this.currentBoard, start);
        List<Tile> movesLegal = new ArrayList<>();

        moves.forEach(move -> {
            Chess chessFake = new Chess(FenUtils.boardToFen(currentBoard), actualMove);
            chessFake.currentBoard.movePiece(start, move);
            boolean kingChecked = this.isKingChecked(chessFake.currentBoard(), piece.color());
            if(!kingChecked) {
                movesLegal.add(move);
            }
        });

        return movesLegal;
    }

    private MoveFeedBack isThatMoveLegal(MoveCommand move) {
        Square startSquare = this.currentBoard.get(move.startPosition());

        // Check if the start position contains a piece
        if (startSquare.piece() == null) {
            return MoveFeedBack.NO_PIECE_SELECTED;
        }

        // Is it an ally piece ?
        if (startSquare.piece().color() != this.currentBoard.currentPlayer()) {
            return MoveFeedBack.ENNEMY_PIECE_SELECTED;
        }

        Square endSquare = this.currentBoard.get(move.endPosition());

        // Is that piece want to go on an ally piece square ?
        if (endSquare.piece() != null && endSquare.piece().color() == this.currentBoard.currentPlayer()) {
            return MoveFeedBack.PIECE_CANT_EAT_ALLY_PIECE;
        }

        // Is that piece want to go on the enemy king square ?
        if (endSquare.piece() != null && PieceType.KING == endSquare.piece().type() && endSquare.piece().color() != this.currentBoard.currentPlayer()) {
            return MoveFeedBack.CANT_EAT_ENEMY_KING;
        }

        List<Tile> allMoves = startSquare.piece().getMoves(currentBoard, startSquare.tile());
        if (!allMoves.contains(endSquare.tile())) {
            return MoveFeedBack.PIECE_ILLEGAL_MOVE;
        }

        Chess chessFake = new Chess(FenUtils.boardToFen(currentBoard), actualMove);
        chessFake.currentBoard.movePiece(move.startPosition(), move.endPosition());
        boolean kingChecked = this.isKingChecked(chessFake.currentBoard(), startSquare.piece().color());

        if (kingChecked) {
            return MoveFeedBack.PIECE_BLOCKED;
        }

        return MoveFeedBack.AUTHORIZED;
    }

    private boolean isKingChecked(Board board, Color colorKing) {

        Tile kingTile = null;
        for (int x = 0; x <= 7; x++) {
            for (int y = 0; y <= 7; y++) {
                Piece piece = board.squares()[x][y].piece();
                if(piece != null && piece.type() == PieceType.KING && piece.color() == colorKing) {
                    kingTile = board.squares()[x][y].tile();
                }
            }
        }

        boolean isKingChecked = false;
        for (int x = 0; x <= 7; x++) {
            for (int y = 0; y <= 7; y++) {
                Piece piece = board.squares()[x][y].piece();
                if (piece != null && piece.color() != colorKing) {
                    List<Tile> threatens = piece.getThreatens(board, board.squares()[x][y].tile());
                    if(threatens.contains(kingTile)) {
                        isKingChecked = true;
                        break;
                    }
                }
            }
        }

        return isKingChecked;
    }
}
