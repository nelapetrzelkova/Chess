package Board;

import Moves.Move;
import Pieces.*;
import Players.BlackPlayer;
import Players.Player;
import Players.Team;
import Players.WhitePlayer;

import java.io.*;
import java.util.*;

public class Board {

    private final List<Tile> gameBoard;
    private final Collection<Piece> whites;
    private final Collection<Piece> blacks;

    private final WhitePlayer whitePlayer;
    private final BlackPlayer blackPlayer;
    private Player currentPlayer;

    private Board(Builder builder) {         //chessboard is represented as an one-dimensional array of size 64
        this.gameBoard = Arrays.asList(createGameBoard(builder));
        this.whites = currentBoardState(this.gameBoard, Team.WHITE);
        this.blacks = currentBoardState(this.gameBoard, Team.BLACK);

        final Collection<Move> whiteLegalMoves = getLegalMoves(this.whites);
        final Collection<Move> blackLegalMoves = getLegalMoves(this.blacks);

        this.whitePlayer = new WhitePlayer(this, whiteLegalMoves, blackLegalMoves);
        this.blackPlayer = new BlackPlayer(this, blackLegalMoves, whiteLegalMoves);
        this.currentPlayer = builder.nextMove.choosePlayer(this.whitePlayer, this.blackPlayer);
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 64; i++) {
            final String tileText = this.gameBoard.get(i).toString();
            builder.append(String.format("%3s", tileText));
            if ((i + 1) % 8 == 0) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }

    /**
     *
     * @return black pieces on current board
     */
    public Collection<Piece> getBlacks() {
        return this.blacks;
    }

    /**
     *
     * @return white pieces on current board
     */
    public Collection<Piece> getWhites() {
        return this.whites;
    }

    /**
     *
     * @return all (black and white) pieces on current board
     */
    public Iterable<Piece> getAllPieces() {
        List<Piece> pieces = new ArrayList();
        pieces.addAll(this.whites);
        pieces.addAll(this.blacks);
        return pieces;
    }

    /**
     *
     * @param pieces on current board
     * @return all legal moves of all pieces on current board
     */
    private Collection<Move> getLegalMoves(Collection<Piece> pieces) {
        final List<Move> legalMoves = new ArrayList();
        for (final Piece piece : pieces) {
            legalMoves.addAll(piece.getPossibleMoves(this));
        }
        return legalMoves;
    }

    /**
     * pieces of given team that are alive
     * @param gameBoard list of tiles
     * @param team
     * @return
     */
    private static Collection<Piece> currentBoardState(List<Tile> gameBoard, Team team) {
        final Collection<Piece> activePieces = new ArrayList();

        for (Tile tile : gameBoard) {
            if (!tile.empty()) {
                final Piece piece = tile.getPiece();
                if (piece.getPieceTeam() == team) {
                    activePieces.add(piece);
                }
            }
        }
        return activePieces;
    }

    public Tile getTile(int coords) {
        return gameBoard.get(coords);
    }

    /**
     * creates new chess board
     * @param builder
     * @return
     */
    private static Tile[] createGameBoard(final Builder builder) {
        final Tile[] tiles = new Tile[64];
        for (int i = 0; i < 64; i++) {
            tiles[i] = Tile.createTile(i, builder.boardConfig.get(i));
        }
        return tiles;
    }

    /**
     * standard starting position of pieces
     * @return
     */
    public static Board createStandardBoard() {
        final Builder builder = new Builder();
        // Black Layout
        builder.setPiece(new Rook(0, Team.BLACK));
        builder.setPiece(new Knight(1, Team.BLACK));
        builder.setPiece(new Bishop(2, Team.BLACK));
        builder.setPiece(new Queen(3, Team.BLACK));
        builder.setPiece(new King(4, Team.BLACK, true));
        builder.setPiece(new Bishop(5, Team.BLACK));
        builder.setPiece(new Knight(6, Team.BLACK));
        builder.setPiece(new Rook(7, Team.BLACK));
        builder.setPiece(new Pawn(8, Team.BLACK));
        builder.setPiece(new Pawn(9, Team.BLACK));
        builder.setPiece(new Pawn(10, Team.BLACK));
        builder.setPiece(new Pawn(11, Team.BLACK));
        builder.setPiece(new Pawn(12, Team.BLACK));
        builder.setPiece(new Pawn(13, Team.BLACK));
        builder.setPiece(new Pawn(14, Team.BLACK));
        builder.setPiece(new Pawn(15, Team.BLACK));
        // White Layout
        builder.setPiece(new Pawn(48, Team.WHITE));
        builder.setPiece(new Pawn(49, Team.WHITE));
        builder.setPiece(new Pawn(50, Team.WHITE));
        builder.setPiece(new Pawn(51, Team.WHITE));
        builder.setPiece(new Pawn(52, Team.WHITE));
        builder.setPiece(new Pawn(53, Team.WHITE));
        builder.setPiece(new Pawn(54, Team.WHITE));
        builder.setPiece(new Pawn(55, Team.WHITE));
        builder.setPiece(new Rook(56, Team.WHITE));
        builder.setPiece(new Knight(57, Team.WHITE));
        builder.setPiece(new Bishop(58, Team.WHITE));
        builder.setPiece(new Queen(59, Team.WHITE));
        builder.setPiece(new King(60, Team.WHITE, true));
        builder.setPiece(new Bishop(61, Team.WHITE));
        builder.setPiece(new Knight(62, Team.WHITE));
        builder.setPiece(new Rook(63, Team.WHITE));
        //white to move
        builder.setMoveMaker(Team.WHITE);
        //build the board
        return builder.build();
    }

    /**
     * pieces start differently than usual - user chooses how in a file
     * @param path
     * @return
     * @throws IOException
     */
    static public Board createCustomBoard(String path) throws IOException {

        File file = new File(path);

        final Builder builder = new Builder();

        BufferedReader br = new BufferedReader(new FileReader(file));

        String nextPlayer = br.readLine();
        if ("white".equals(nextPlayer)) {
            builder.setMoveMaker(Team.WHITE);
        } else if ("black".equals(nextPlayer)) {
            builder.setMoveMaker(Team.BLACK);
        } else {
            throw new NoSuchElementException("You can choose either white or black team");
        }
        System.out.println(nextPlayer);

        int i;
        int counter = 0;

        while ((i = br.read()) != -1) {
            char ch = (char) i;
            System.out.print(ch);
            if (ch == ' ' || ch == '\n') { continue; }
            switch (ch) {
                case '-':
                    break;
                case 'B':
                    builder.setPiece(new Bishop(counter, Team.WHITE));
                    break;
                case 'K':
                    builder.setPiece(new King(counter, Team.WHITE));
                    break;
                case 'N':
                    builder.setPiece(new Knight(counter, Team.WHITE));
                    break;
                case 'P':
                    builder.setPiece(new Pawn(counter, Team.WHITE));
                    break;
                case 'Q':
                    builder.setPiece(new Queen(counter, Team.WHITE));
                    break;
                case 'R':
                    builder.setPiece(new Rook(counter, Team.WHITE));
                    break;
                case 'b':
                    builder.setPiece(new Bishop(counter, Team.BLACK));
                    break;
                case 'k':
                    builder.setPiece(new King(counter, Team.BLACK));
                    break;
                case 'n':
                    builder.setPiece(new Knight(counter, Team.BLACK));
                    break;
                case 'p':
                    builder.setPiece(new Pawn(counter, Team.BLACK));
                    break;
                case 'q':
                    builder.setPiece(new Queen(counter, Team.BLACK));
                    break;
                case 'r':
                    builder.setPiece(new Rook(counter, Team.BLACK));
                    break;
                default:
                    throw new NoSuchElementException("Non-existing piece");
            }
            counter++;
        }


        return builder.build();
    }

    /**
     *
     * @return legal moves of both players
     */
    public Iterable<Move> getAllLegalMoves() {
        List<Move> moves = new ArrayList();
        moves.addAll(this.blackPlayer.getLegalMoves());
        moves.addAll(this.whitePlayer.getLegalMoves());
        return moves;
    }

    public Player blackPlayer() {
        return this.blackPlayer;
    }

    public Player whitePlayer() {
        return this.whitePlayer;
    }

    public Player currentPlayer() {
        return this.currentPlayer;
    }

    public Player setCurrentPlayer(Player player) {
        return this.currentPlayer = player;
    }



    public static class Builder {

        Map<Integer, Piece> boardConfig;
        Team nextMove;
        Pawn enPassantPawn;

        public Builder() {
            this.boardConfig = new HashMap();
        }

        /**
         * puts pieces on a given tile
         * @param piece
         * @return
         */
        public Builder setPiece(final Piece piece) {
            this.boardConfig.put(piece.getPiecePosition(), piece);
            return this;
        }

        public Builder setMoveMaker(final Team nextMove) {
            this.nextMove = nextMove;
            return this;
        }

        public Board build() {
            return new Board(this);
        }

        public void setEnPassantPawn(Pawn pawn) {
            this.enPassantPawn = pawn;
        }
    }
}

