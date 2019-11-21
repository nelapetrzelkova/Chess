package Pieces;

import Board.Board;
import Board.BoardUtils;
import Board.Tile;
import Moves.Move;
import Players.Team;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Pawn extends Piece {

    private static final int[] POSSIBLE_MOVES = {7, 8, 9, 16}; // 8 = normal move, 16 = jump, 7,9 = attack


    public Pawn(Integer position, Team pieceTeam) {
        super(position, pieceTeam, PieceType.PAWN, true);
    }

    public Pawn(Integer position, Team pieceTeam, boolean isFirstMove) {
        super(position, pieceTeam, PieceType.PAWN, isFirstMove);
    }

    @Override
    public Collection<Move> getPossibleMoves(Board board) {

        final List<Move> legalMoves = new ArrayList();

        for(final int offset : POSSIBLE_MOVES) {

            final int newCoord = this.position + (this.pieceTeam.getDirection()*offset); //new possible position for pawn
            //based od piece's team and .getDirection we decide whether we move down or up
            if (!BoardUtils.isValidCoord(newCoord)) { continue; }

            if (offset == 8 && board.getTile(newCoord).empty()) { //normal move forward
                if(this.pieceTeam.canBePromoted(newCoord)) {
                    legalMoves.add(new Move.PawnPromotion(new Move.PawnMove(board, this, newCoord)));
                } else {
                    legalMoves.add(new Move.PawnMove(board, this, newCoord));
                }
            } else if (offset == 16 && this.firstMove()
                    && ((BoardUtils.SECOND_ROW[this.position] && this.pieceTeam.isBlack()) || (BoardUtils.SEVENTH_ROW[this.position] && this.pieceTeam.isWhite()))) {
                final int between = this.position + (this.pieceTeam.getDirection()*8);
                if (board.getTile(between).empty() && board.getTile(newCoord).empty()) { //checks if new tile and the one in between is empty
                    legalMoves.add(new Move.PawnJump(board, this, newCoord));
                }
            } else if (offset == 7 && !((BoardUtils.EIGHTH_COLUMN[this.position] && this.pieceTeam.isWhite()) || //attack, pawn can't be at the end tile
                    (BoardUtils.FIRST_COLUMN[this.position] && this.pieceTeam.isBlack()))) {
                if (!board.getTile(newCoord).empty()) {
                    final Piece pieceOnNewTile = board.getTile(newCoord).getPiece();
                    if (this.pieceTeam != pieceOnNewTile.pieceTeam) { //chceck if you attack enemy piece, not yours
                        if(this.pieceTeam.canBePromoted(newCoord)) {
                            legalMoves.add(new Move.PawnPromotion(new Move.PawnMove(board, this, newCoord)));
                        } else {
                            legalMoves.add(new Move.PawnAttackMove(board, this, newCoord, pieceOnNewTile));
                        }
                    }
                } else if ((BoardUtils.FOURTH_ROW[this.position] && this.pieceTeam.isWhite())
                        || (BoardUtils.FIFTH_ROW[this.position] && this.pieceTeam.isBlack())){
                    int nextTile = this.position - this.pieceTeam.getDirection();
                    Piece nextTilePiece = board.getTile(nextTile).getPiece();
                    if (nextTilePiece != null) {
                        if (this.pieceTeam != nextTilePiece.pieceTeam && nextTilePiece.getPieceType().isPawn()) {
                            legalMoves.add(new Move.PawnEnPassantMove(board, this, newCoord, nextTilePiece));
                        }
                    }
                }

            } else if (offset == 9 && !((BoardUtils.FIRST_COLUMN[this.position] && this.pieceTeam.isWhite()) || //attack, pawn can't be at the end tile
                    (BoardUtils.EIGHTH_COLUMN[this.position] && this.pieceTeam.isBlack()))) {
                if (!board.getTile(newCoord).empty()) {
                    final Piece pieceOnNewTile = board.getTile(newCoord).getPiece();
                    if (this.pieceTeam != pieceOnNewTile.pieceTeam) { //chceck if you attack enemy piece, not yours
                        if(this.pieceTeam.canBePromoted(newCoord)) {
                            legalMoves.add(new Move.PawnPromotion(new Move.PawnMove(board, this, newCoord)));
                        } else {
                            legalMoves.add(new Move.PawnAttackMove(board, this, newCoord, pieceOnNewTile));
                        }
                    }
                } else if ((BoardUtils.FOURTH_ROW[this.position] && this.pieceTeam.isWhite())
                        || (BoardUtils.FIFTH_ROW[this.position] && this.pieceTeam.isBlack())){
                    int nextTile = this.position + this.pieceTeam.getDirection();
                    Piece nextTilePiece = board.getTile(nextTile).getPiece();
                    if (nextTilePiece != null) {
                        if (this.pieceTeam != nextTilePiece.pieceTeam && nextTilePiece.getPieceType().isPawn()) {
                            legalMoves.add(new Move.PawnEnPassantMove(board, this, newCoord, nextTilePiece));
                        }
                    }
                }
            }
        }
        return Collections.unmodifiableCollection(legalMoves);
    }

    @Override
    public Piece movePiece(Move move) {
        return new Pawn(move.getNewCoord(), move.getPiece().pieceTeam);
    }

    @Override
    public String toString() {
        return PieceType.PAWN.toString();
    }

    public PieceType getPieceType() {
        return this.pieceType;
    }

    public int getPiecePosition() {
        return this.position;
    }

    public Team getPieceTeam() {
        return this.pieceTeam;
    }

    public boolean firstMove() { //this is for Pawn - to know if they already made a jump
        return this.isFirstMove;
    }

    public Piece getPromotionPiece() {
        return new Queen(this.position, this.pieceTeam, false);
    }
}

