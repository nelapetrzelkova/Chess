package Players;

import Board.Board;
import Moves.Move;
import Pieces.Piece;
import Pieces.Rook;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class BlackPlayer extends Player {
    public BlackPlayer(Board board, Collection<Move> blackLegalMoves, Collection<Move> whiteLegalMoves) {
        super(board, blackLegalMoves, whiteLegalMoves);
    }

    @Override
    public Collection<Piece> getActivePieces() {
        return this.board.getBlacks();
    }

    @Override
    public Team getTeam() {
        return Team.BLACK;
    }

    @Override
    public Player getOpponent() {
        return this.board.whitePlayer();
    }

    @Override
    public Collection<Move> calculateKingCastles(final Collection<Move> playerLegals,
                                                 final Collection<Move> opponentLegals) {

        if (this.isInCheck()) {
            return Collections.emptyList();
        }

        final List<Move> kingCastles = new ArrayList();

        if (this.king.getPiecePosition() == 4 && !this.isInCheck) {
            //blacks king side castle
            if (this.board.getTile(5).getPiece() == null && board.getTile(6).getPiece() == null) {
                final Piece kingSideRook = this.board.getTile(7).getPiece();
                if (this.king.isFirstMove && kingSideRook != null && kingSideRook.firstMove() &&
                        Player.calculateAttacksOnTile(5, opponentLegals).isEmpty() &&
                        Player.calculateAttacksOnTile(6, opponentLegals).isEmpty() &&
                        kingSideRook.getPieceType().isRook()) {
                    kingCastles.add(new Move.KingSideCastleMove(this.board, this.king, 6, (Rook) kingSideRook, kingSideRook.getPiecePosition(), 5));
                }
            }
            //blacks queen side castle
            if (this.board.getTile(1).getPiece() == null && this.board.getTile(2).getPiece() == null &&
                    this.board.getTile(3).getPiece() == null) {
                final Piece queenSideRook = this.board.getTile(0).getPiece();
                if (queenSideRook != null && queenSideRook.firstMove() &&
                        Player.calculateAttacksOnTile(2, opponentLegals).isEmpty() &&
                        Player.calculateAttacksOnTile(3, opponentLegals).isEmpty() &&
                        queenSideRook.getPieceType().isRook()) {
                    kingCastles.add(new Move.QueenSideCastleMove(this.board, this.king, 2, (Rook) queenSideRook, queenSideRook.getPiecePosition(), 3));

                }
            }
        }
        return kingCastles;
    }

    @Override
    public String toString() {
        return "black";
    }
}

