package ch.claudedy.chess.model;

import ch.claudedy.chess.model.enumeration.*;
import ch.claudedy.chess.system.ConsolePrint;
import ch.claudedy.chess.system.SystemSettings;
import ch.claudedy.chess.util.FenConverter;
import lombok.Getter;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Accessors(fluent = true)
@Getter
public class Chess implements Serializable {

    private final List<HistoricalBoardFen> historicalBoards;
    private MoveCommand actualMove;
    private final Board currentBoard;
    private GameStatus gameStatus;

    public Chess(String fen, MoveCommand previousMove) {
        this.currentBoard = FenConverter.fenToBoard(fen);
        this.historicalBoards = new ArrayList<>();
        this.actualMove = previousMove;
        this.gameStatus = GameStatus.WAITING_MOVE;
    }

    public MoveStatus makeMove(MoveCommand move) {
        if (this.gameStatus.isGameExecuting()) return MoveStatus.CANT_MOVE_DURING_ANOTHER_MOVE;
        if (this.gameStatus.isGameOver()) return MoveStatus.GAME_ALREADY_OVER;

        this.gameStatus = GameStatus.EXECUTING; // Change the status to notify we are executing the new move

        var statusMove = this.isThatMoveLegal(move); // Check if the move is authorized

        if (!statusMove.isOk()) {
            this.gameStatus = GameStatus.WAITING_MOVE;
            return statusMove;
        }

        this.addAHistoricBoard(); // Create a copy of the current board before to make action on it

        this.actualMove = move;

        this.currentBoard.movePiece(move.startPosition(), move.endPosition(), move.promote());
        this.currentBoard.switchPlayer(); // Switch the player

        this.updateGameStatus();

        if (SystemSettings.PRINT_CONSOLE) ConsolePrint.execute(this); // DEV MODE to see a board in the console

        return statusMove;
    }

    private void addAHistoricBoard() {
        this.historicalBoards.add(new HistoricalBoardFen().fen(FenConverter.boardToFen(this.currentBoard)).previousMove(this.actualMove));
    }

    private void updateGameStatus() {

        if (this.currentBoard.fiftyRules() == 50) {
            this.gameStatus = GameStatus.RULES_50;
            return;
        }

        // Threefold repetition rule
        if (nbTimesFenCurrentBoardHasBeenDoneBefore() >= 3) {
            this.gameStatus = GameStatus.REPETITION_RULE;
            return;
        }

        var tileKing = this.currentBoard.getTileKing(this.currentBoard.currentPlayer());
        boolean kingCantMove = getLegalMoves(tileKing).isEmpty();

        if (kingCantMove) {
            boolean kingChecked = currentBoard.isTileChecked(currentBoard.currentPlayer(), tileKing);
            boolean aPieceCanMove = isAAllyPieceCanMove();

            if (!aPieceCanMove && kingChecked) gameStatus = GameStatus.CHECKMATED;
            if (!aPieceCanMove && !kingChecked) gameStatus = GameStatus.STALEMATED;
        } else {
            // Check for all situations that we are sure, we can't do any checkmates with remaining pieces
            var allAlivePieces = getAllAlivePieces();

            if (allAlivePieces.size() == 2) gameStatus = GameStatus.IMPOSSIBILITY_CHECKMATE; // KING vs KING
            if (allAlivePieces.size() == 3 && (this.existThisPiece(allAlivePieces, PieceType.BISHOP) || this.existThisPiece(allAlivePieces, PieceType.KNIGHT)))
                gameStatus = GameStatus.IMPOSSIBILITY_CHECKMATE; // KING + (BISHOP | KNIGHT) vs KING

            if (allAlivePieces.size() == 4) {
                List<Square> bishopsSquares = this.currentBoard.getAllPiecesSquares().stream().filter(s -> s.piece().type() == PieceType.BISHOP).collect(Collectors.toList());

                if (bishopsSquares.size() == 2 && bishopsSquares.get(0).tile().color().isSameColor(bishopsSquares.get(1).tile().color())) {
                    // king + bishop vs king + bishop (bishop same color)
                    gameStatus = GameStatus.IMPOSSIBILITY_CHECKMATE;
                }
            }
        }

        if (!this.gameStatus.isGameOver()) this.gameStatus = GameStatus.WAITING_MOVE;
    }

    private boolean existThisPiece(List<Piece> allPieces, PieceType piece) {
        return allPieces.stream().anyMatch(p -> p.type() == piece);
    }

    private List<Piece> getAllAlivePieces() {
        List<Piece> allPieces = new ArrayList<>();
        allPieces.addAll(currentBoard.getAlivePieces(Color.WHITE));
        allPieces.addAll(currentBoard.getAlivePieces(Color.BLACK));
        return allPieces;
    }

    private boolean isAAllyPieceCanMove() {
        boolean aPieceCanMove = false;
        for (int x = 0; x <= 7; x++) {
            for (int y = 0; y <= 7; y++) {
                Piece piece = currentBoard.getSquare(x, y).piece();
                if (piece == null || piece.color() != this.currentBoard.currentPlayer()) continue;

                aPieceCanMove = !this.getLegalMoves(currentBoard.getSquare(x, y).tile()).isEmpty();

                if (aPieceCanMove) break;
            }
            if (aPieceCanMove) break;
        }

        return aPieceCanMove;
    }

    private int nbTimesFenCurrentBoardHasBeenDoneBefore() {
        var fen = FenConverter.boardToFen(this.currentBoard);
        var actualMove = this.actualMove;
        var fenCurrentBoard = new HistoricalBoardFen().fen(fen).previousMove(actualMove);

        return Collections.frequency(this.historicalBoards.stream().map(HistoricalBoardFen::onlyBoardFen).collect(Collectors.toList()), fenCurrentBoard.onlyBoardFen());
    }

    public List<PossibleMove> getLegalMoves(Tile start) {
        // Check if the start position contains a piece
        var piece = currentBoard.getSquare(start).piece();
        if (piece == null) {
            return new ArrayList<>();
        }

        // Get all the moves from the piece
        var moves = piece.getMoves(this.currentBoard, start);

        moves = moves.stream()
                .filter(move -> move.type() != MoveType.THREAT_ENEMY_KING) // Remove all move to go on the enemy king (can't eat him)
                .filter(move -> move.type() != MoveType.ALLY_PROTECTION) // Remove all move that protect our ally pieces
                .filter(move -> move.type() != MoveType.ONLY_THREAT)// Remove all only threat cause it's not a move, just a threat
                .collect(Collectors.toList());

        // Remove possibilities to castle when the piece is the king and is checked
        if (piece.type() == PieceType.KING && currentBoard.isKingChecked(piece.color())) {
            moves = moves.stream().filter(move -> move.type() != MoveType.CASTLE).collect(Collectors.toList());
        }

        List<PossibleMove> legalMoves = new ArrayList<>();
        var fen = FenConverter.boardToFen(this.currentBoard);

        // Filter the moves to get only those who always make our king in safe mode
        for (PossibleMove currentMove : moves) {
            Chess chessFake = new Chess(fen, this.actualMove);
            chessFake.currentBoard().movePiece(start, currentMove.destination(), null);

            boolean isOurKingChecked = chessFake.currentBoard().isKingChecked(piece.color());
            if (!isOurKingChecked) legalMoves.add(currentMove);
        }

        return legalMoves;
    }

    public List<Tile> getPieceWithoutProtection(Color colorPiece) {
        var squareAlivePieces = currentBoard.getSquaresAlivePieces(colorPiece);

        var allTilesByProtection = squareAlivePieces.stream()
                .map(squareAllyPiece -> squareAllyPiece.piece().getMoves(this.currentBoard, squareAllyPiece.tile()))
                .flatMap(Collection::stream)
                .filter(possibleMove -> possibleMove.type() == MoveType.ALLY_PROTECTION)
                .map(PossibleMove::destination)
                .collect(Collectors.toList());

        List<Tile> tileWithoutProtection = new ArrayList<>();

        squareAlivePieces.forEach(square -> {
            if (!allTilesByProtection.contains(square.tile())) tileWithoutProtection.add(square.tile());
        });

        return tileWithoutProtection;
    }

    private MoveStatus isThatMoveLegal(MoveCommand move) {
        if (move == null || move.startPosition() == null || move.endPosition() == null)
            return MoveStatus.BAD_MOVE_COMMAND;

        var startSquare = this.currentBoard.getSquare(move.startPosition());

        if (startSquare.piece() == null)
            return MoveStatus.NO_PIECE_SELECTED; // Check if the start position contains a piece
        if (!this.currentBoard.currentPlayer().isSameColor(startSquare.piece().color()))
            return MoveStatus.ENNEMY_PIECE_SELECTED; // Is it an ally piece ?

        var endSquare = this.currentBoard.getSquare(move.endPosition());

        if (endSquare.piece() != null && endSquare.piece().color() == this.currentBoard.currentPlayer())
            return MoveStatus.PIECE_CANT_EAT_ALLY_PIECE; // Is that piece want to go on an ally piece square ?
        if (endSquare.piece() != null && PieceType.KING == endSquare.piece().type() && endSquare.piece().color() != this.currentBoard.currentPlayer())
            return MoveStatus.CANT_EAT_ENEMY_KING; // Is that piece want to go on the enemy king square ?

        var allMoves = getLegalMoves(startSquare.tile());
        if (allMoves.stream().noneMatch(m -> m.destination() == endSquare.tile()))
            return MoveStatus.PIECE_ILLEGAL_MOVE; // The possible moves doesnt contains the given move

        // Is our king is not checked when we move that piece ?
        var chessFake = new Chess(FenConverter.boardToFen(currentBoard), actualMove);
        chessFake.currentBoard.movePiece(move.startPosition(), move.endPosition(), move.promote());
        if (chessFake.currentBoard().isKingChecked(startSquare.piece().color())) return MoveStatus.PIECE_BLOCKED;

        return MoveStatus.OK;
    }
}
