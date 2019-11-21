package Pieces;

import Board.Board;
import Moves.Move;
import Players.Team;

import java.util.Collection;


public abstract class Piece { //class piece
    public final Integer position; //position on a chessboard
    final Team pieceTeam; //white x black
    public final boolean isFirstMove; //because of the pawn
    final PieceType pieceType;

    public Piece(Integer position, Team pieceTeam, PieceType pieceType) {
        this.position = position;
        this.pieceTeam = pieceTeam;
        this.pieceType = pieceType;
        this.isFirstMove = true;
    }

    public Piece(Integer position, Team pieceTeam, PieceType pieceType, boolean isFirstMove) {
        this.position = position;
        this.pieceTeam = pieceTeam;
        this.pieceType = pieceType;
        this.isFirstMove = isFirstMove;
    }


    /**
     * returns list of possible moves that can be made by given piece
     * @param board = current state of board
     * @return
     */
    public abstract Collection<Move> getPossibleMoves(final Board board); //mandatory function for all pieces - possible moves

    /**
     * transfer piece to new position on board
     * @param move
     * @return
     */
    public abstract Piece movePiece(Move move);

    public abstract PieceType getPieceType();

    public abstract int getPiecePosition();

    public abstract Team getPieceTeam();

    /**
     *
     * @return true if this is first move of piece, false otherwise
     */
    public abstract boolean firstMove();

    public enum PieceType {

        PAWN("P") {public boolean isKing() {
            return false;
        } public boolean isRook() {
            return false;
        } public boolean isPawn() { return true; }},
        ROOK("R") {public boolean isKing() { return false; } public boolean isRook() { return true; } public boolean isPawn() { return false; }},
        KNIGHT("N") {public boolean isKing() { return false; } public boolean isRook() { return false; } public boolean isPawn() { return false; }},
        BISHOP("B") {public boolean isKing() { return false; } public boolean isRook() { return false; } public boolean isPawn() { return false; }},
        QUEEN("Q") {public boolean isKing() { return false; } public boolean isRook() { return false; } public boolean isPawn() { return false; }},
        KING ("K") {public boolean isKing() { return true; } public boolean isRook() { return false; } public boolean isPawn() { return false; }};

        private String pieceName;

        PieceType(String pieceName) {
            this.pieceName = pieceName;
        }

        @Override
        public String toString() {
            return this.pieceName;
        }

        public abstract boolean isKing();
        public abstract boolean isRook();

        public abstract boolean isPawn();
    }

}

