package Players;

import Board.Board;
import Moves.Move;
import Pieces.Piece;
import Pieces.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class WhitePlayer extends Player {
    public WhitePlayer(Board board, Collection<Move> whiteLegalMoves, Collection<Move> blackLegalMoves) {
        super(board, whiteLegalMoves, blackLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getWhites();
    }

    @Override
    public Team getTeam() {
        return Team.WHITE;
    }

    @Override
    public Player getOpponent() {
        return this.board.blackPlayer();
    }

    @Override
    public Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
                                                 final Collection<Move> opponentLegals) {

        if (this.isInCheck()) {
            return Collections.emptyList();
        }

        final List<Move> kingCastles = new ArrayList();

        if (/*board.currentPlayer().king.isFirstMove && */this.king.getPiecePosition() == 60 && !this.isInCheck) {
            //blacks king side castle
            if (this.board.getTile(61).getPiece() == null && board.getTile(62).getPiece() == null) {
                final Piece kingSideRook = this.board.getTile(63).getPiece();
                if (kingSideRook != null && kingSideRook.firstMove() &&
                        Player.calculateAttacksOnTile(61, opponentLegals).isEmpty() &&
                        Player.calculateAttacksOnTile(62, opponentLegals).isEmpty() &&
                        kingSideRook.getPieceType().isRook()) {
                    kingCastles.add(new Move.KingSideCastleMove(this.board, this.king, 62, (Rook) kingSideRook, kingSideRook.getPiecePosition(), 61));
                }
            }
            //blacks queen side castle
            if (this.board.getTile(59).getPiece() == null && this.board.getTile(58).getPiece() == null &&
                    this.board.getTile(57).getPiece() == null) {
                final Piece queenSideRook = this.board.getTile(56).getPiece();
                if (queenSideRook != null && queenSideRook.firstMove() &&
                        Player.calculateAttacksOnTile(58, opponentLegals).isEmpty() &&
                        Player.calculateAttacksOnTile(59, opponentLegals).isEmpty() &&
                        queenSideRook.getPieceType().isRook()) {
                    kingCastles.add(new Move.QueenSideCastleMove(this.board, this.king, 58, (Rook) queenSideRook, queenSideRook.getPiecePosition(), 59));

                }
            }
        }
        return kingCastles;
    }

    @Override
    public String toString() {
        return "white";
    }
}

