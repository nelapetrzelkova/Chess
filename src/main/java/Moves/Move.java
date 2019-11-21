package Moves;

import Board.Board;
import Board.BoardUtils;
import Pieces.King;
import Pieces.Pawn;
import Pieces.Piece;
import Pieces.Rook;

public abstract class Move {
    protected final Board board;
    public final Piece movedPiece;
    private final int newCoord;

    private Move(Board board, Piece movedPiece, int newCoord) {
        this.board = board;
        this.movedPiece = movedPiece;
        this.newCoord = newCoord;
    }

    public int getNewCoord() {
        return this.newCoord;
    }

    public Board getBoard() { return this.board; }

    private int getCurrentCoord() {
        assert this.movedPiece != null;
        return this.movedPiece.getPiecePosition(); }

    public Piece getPiece() { return this.movedPiece; }

    public boolean isAttack() { return false; }

    public boolean isCastlingMove() { return false; }

    public Piece getAttackedPiece() { return null; }

    public Piece getMovedPiece() { return this.movedPiece; }

    /**
     * sets all pieces to new board and sets player who should play next
     * @return
     */
    public Board execute() {

        final Board.Builder builder = new Board.Builder();

        for (Piece piece : this.board.currentPlayer().getActivePieces()) { //if it is not the one piece tha is moving
            if (!this.movedPiece.equals(piece)) {                       //set it on the same position
                builder.setPiece(piece);
            }
        }

        for (Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) { //set all opponents pieces to
            builder.setPiece(piece);                                                  //same position
        }

        builder.setPiece(this.movedPiece.movePiece(this));
        builder.setMoveMaker(this.board.currentPlayer().getOpponent().getTeam());
        return builder.build();
    }


    /**
     * returns algebraic notation
     * @return
     */
    String disambiguationFile() {
        for(final Move move : this.board.currentPlayer().getLegalMoves()) {
            if(move.getNewCoord() == this.newCoord && !this.equals(move) &&
                    this.movedPiece.getPieceType().equals(move.getMovedPiece().getPieceType())) {
                return BoardUtils.getPosAtCoord(this.movedPiece.getPiecePosition()).substring(0, 1);
            }
        }
        return "";
    }

    /**
     * returns previous board
     * @return
     */
    public Board undo() {
        final Board.Builder builder = new Board.Builder();
        for (final Piece piece : this.board.getAllPieces()) {
            builder.setPiece(piece);
        }
        builder.setMoveMaker(this.board.currentPlayer().getTeam());
        return builder.build();
    }

    /**
     * classes of moves
     */
    public static final class MajorMove extends Move { //when we move on an empty tile

        public MajorMove(Board board, Piece movedPiece, int newCoord) {
            super(board, movedPiece, newCoord);
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType().toString() + disambiguationFile() +
                    BoardUtils.getPosAtCoord(this.getNewCoord());
        }
    }

    public static class AttackMove extends Move { //during this move we "destroy" one of peices from enemy team

        final Piece attackedPiece;

        public AttackMove(Board board, Piece movedPiece, int newCoord, Piece attackedPiece) {
            super(board, movedPiece, newCoord);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public boolean isAttack() { return true; }

        @Override
        public Piece getAttackedPiece() { return this.attackedPiece; }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();

            for (Piece piece : this.board.currentPlayer().getActivePieces()) { //if it is not the one piece tha is moving
                if (!this.movedPiece.equals(piece)) {                       //set it on the same position
                    builder.setPiece(piece);
                }
            }

            for (Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) { //set all opponents pieces to
                builder.setPiece(piece);                                                  //same position
            }

            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getTeam());
            return builder.build();
        }

        @Override
        public String toString() {
            return movedPiece.getPieceType() + disambiguationFile() + "x" +
                    BoardUtils.getPosAtCoord(this.getNewCoord());
        }
    }

    public static class PawnMove extends Move { //during this move we "destroy" one of peices from enemy team


        public PawnMove(Board board, Piece movedPiece, int newCoord) {
            super(board, movedPiece, newCoord);
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();

            for (Piece piece : this.board.currentPlayer().getActivePieces()) { //if it is not the one piece tha is moving
                if (!this.movedPiece.equals(piece)) {                       //set it on the same position
                    builder.setPiece(piece);
                }
            }

            for (Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) { //set all opponents pieces to
                builder.setPiece(piece);                                                  //same position
            }

            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getTeam());
            return builder.build();
        }

        @Override
        public String toString() {
            return BoardUtils.getPosAtCoord(this.getNewCoord());
        }
    }

    public static class PawnAttackMove extends AttackMove { //during this move we "destroy" one of peices from enemy team

        final Piece attackedPiece;

        public PawnAttackMove(Board board, Piece movedPiece, int newCoord, Piece attackedPiece) {
            super(board, movedPiece, newCoord, attackedPiece);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();

            for (Piece piece : this.board.currentPlayer().getActivePieces()) { //if it is not the one piece tha is moving
                if (!this.movedPiece.equals(piece)) {                       //set it on the same position
                    builder.setPiece(piece);
                }
            }

            for (Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) { //set all opponents pieces to
                builder.setPiece(piece);                                                  //same position
            }

            builder.setPiece(this.movedPiece.movePiece(this));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getTeam());
            return builder.build();
        }

        @Override
        public String toString() {
            return BoardUtils.getPosAtCoord(this.movedPiece.getPiecePosition()).substring(0, 1) + "x" +
                    BoardUtils.getPosAtCoord(this.getNewCoord());
        }
    }

    public static final class PawnEnPassantMove extends PawnAttackMove { //during this move we "destroy" one of peices from enemy team

        final Piece attackedPiece;

        public PawnEnPassantMove(Board board, Piece movedPiece, int newCoord, Piece attackedPiece) {
            super(board, movedPiece, newCoord, attackedPiece);
            this.attackedPiece = attackedPiece;
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            for (Piece piece : this.board.currentPlayer().getActivePieces()) {
                if(!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                if(!piece.equals(this.getAttackedPiece())) {
                    builder.setPiece(piece);
                }
            }
            final Pawn movedPawn = (Pawn)this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setEnPassantPawn(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getTeam());
            return builder.build();
        }

        @Override
        public String toString() {
            return BoardUtils.getPosAtCoord(this.movedPiece.getPiecePosition()).substring(0, 1) + "x" +
                    BoardUtils.getPosAtCoord(this.getNewCoord());
        }
    }

    public static final class PawnJump extends PawnMove { //during this move we "destroy" one of peices from enemy team


        public PawnJump(Board board, Piece movedPiece, int newCoord) {
            super(board, movedPiece, newCoord);
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            for (Piece piece : this.board.currentPlayer().getActivePieces()) {
                if(!this.movedPiece.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            final Pawn movedPawn = (Pawn)this.movedPiece.movePiece(this);
            builder.setPiece(movedPawn);
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getTeam());
            return builder.build();
        }

        @Override
        public String toString() {
            return BoardUtils.getPosAtCoord(getNewCoord());
        }
    }

    public static class PawnPromotion extends Move {

        final Move move;
        final Pawn promotedPawn;

        public PawnPromotion(Move move) {
            super (move.getBoard(), move.getPiece(), move.getNewCoord());
            this.move = move;
            this.promotedPawn = (Pawn) move.getPiece();
        }

        @Override
        public Board execute() {

            final Board newBoard = this.move.execute();
            final Board.Builder builder = new Board.Builder();
            for (Piece piece : newBoard.currentPlayer().getActivePieces()) {
                if (!this.promotedPawn.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (Piece piece : newBoard.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            builder.setPiece(this.promotedPawn.getPromotionPiece().movePiece(this));
            builder.setMoveMaker(newBoard.currentPlayer().getTeam());
            return builder.build();
        }

        @Override
        public boolean isAttack() {
            return this.move.isAttack();
        }

        @Override
        public Piece getAttackedPiece() {
            return this.move.getAttackedPiece();
        }

        @Override
        public String toString() {
            return BoardUtils.getPosAtCoord(this.movedPiece.getPiecePosition()) + "-" +
                    BoardUtils.getPosAtCoord(this.getNewCoord()) + "=Q";
        }
    }

    public static class CastleMove extends Move { //during this move we "destroy" one of peices from enemy team

        final Rook castleRook;
        final int castleRookStart;
        final int castleRookDest;

        CastleMove(Board board, Piece movedPiece, int newCoord, Rook castleRook, int castleRookStart, int castleRookDest) {
            super(board, movedPiece, newCoord);
            this.castleRook = castleRook;
            this.castleRookStart = castleRookStart;
            this.castleRookDest = castleRookDest;
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            for (Piece piece : this.board.currentPlayer().getActivePieces()) {
                if(!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            builder.setPiece(this.movedPiece.movePiece(this)); //the king
            builder.setPiece(new Rook(this.castleRookDest, this.castleRook.getPieceTeam()));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getTeam());
            return builder.build();
        }

        public boolean isCastlingMove() {
            return true;
        }
    }

    public static class KingSideCastleMove extends CastleMove {

        public KingSideCastleMove(Board board, Piece movedPiece, int newCoord, Rook castleRook, int castleRookStart, int castleRookDest) {
            super(board, movedPiece, newCoord, castleRook, castleRookStart, castleRookDest);
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            for (Piece piece : this.board.currentPlayer().getActivePieces()) {
                if(!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            builder.setPiece(this.movedPiece.movePiece(this)); //the king
            builder.setPiece(new Rook(this.castleRookDest, this.castleRook.getPieceTeam()));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getTeam());
            return builder.build();
        }

        @Override
        public String toString() {
            return "O-O";
        }

        public boolean isCastlingMove() {
            return true;
        }
    }

    public static class QueenSideCastleMove extends CastleMove {


        public QueenSideCastleMove(Board board, Piece movedPiece, int newCoord, Rook castleRook, int castleRookStart, int castleRookDest) {
            super(board, movedPiece, newCoord, castleRook, castleRookStart, castleRookDest);
        }

        @Override
        public Board execute() {
            final Board.Builder builder = new Board.Builder();
            for (Piece piece : this.board.currentPlayer().getActivePieces()) {
                if(!this.movedPiece.equals(piece) && !this.castleRook.equals(piece)) {
                    builder.setPiece(piece);
                }
            }
            for (Piece piece : this.board.currentPlayer().getOpponent().getActivePieces()) {
                builder.setPiece(piece);
            }
            builder.setPiece(this.movedPiece.movePiece(this)); //the king
            builder.setPiece(new Rook(this.castleRookDest, this.castleRook.getPieceTeam()));
            builder.setMoveMaker(this.board.currentPlayer().getOpponent().getTeam());
            return builder.build();
        }

        @Override
        public String toString() {
            return "O-O-O";
        }

        public boolean isCastlingMove() {
            return true;
        }

    }

    public static class NullMove extends Move { //during this move we "destroy" one of peices from enemy team


        public NullMove() {
            super(null, null, -1);
        }

        @Override
        public Board execute() {
            throw new RuntimeException("Null move is not executable");
        }

        @Override
        public String toString() {
            return "null move";
        }
    }

    public static Move createMove(final Board board, final int currentCoord, final int newCoord) {
        Move ret = new NullMove();
        for (Move move : board.getAllLegalMoves()) {
            if (move.getNewCoord() == newCoord && move.getCurrentCoord() == currentCoord) {
                ret = move;
            }
        }
        return ret;
    }
}


