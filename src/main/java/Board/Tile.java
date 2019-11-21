package Board;

import Pieces.Piece;

import java.util.HashMap;
import java.util.Map;

public abstract class Tile {
    protected final Integer coord;

    private static final Map<Integer, EmptyTile> EMPTY_TILES = allEmptyTiles();

    private static Map<Integer, EmptyTile> allEmptyTiles() {
        final HashMap emptyTileMap = new HashMap();

        for (int i = 0; i < 64; ++i) {
            emptyTileMap.put(i, new EmptyTile(i));
        }

        return emptyTileMap;
    }

    public Tile(Integer coord) {
        this.coord = coord;
    }

    public int getTileCoord() { return this.coord; }

    public static Tile createTile(final Integer coord, final Piece piece) {
        if (piece == null) {
            return EMPTY_TILES.get(coord);
        } else {
            return new OccupiedTile(coord, piece);
        }
    }

    public abstract boolean empty();
    public abstract Piece getPiece();

    public static final class EmptyTile extends Tile {
        private EmptyTile(Integer coord) {
            super(coord);
        }

        @Override
        public boolean empty() {
            return true;
        }

        @Override
        public Piece getPiece() {
            return null;
        }

        @Override
        public String toString() {
            return "-";
        }
    }

    public static final class OccupiedTile extends Tile {
        private final Piece pieceOnTile;

        private OccupiedTile(Integer coord, Piece pieceOnTile) {
            super(coord);
            this.pieceOnTile = pieceOnTile;
        }

        @Override
        public boolean empty() {
            return false;
        }

        @Override
        public Piece getPiece() {
            return this.pieceOnTile;
        }

        @Override
        public String toString() {
            return getPiece().getPieceTeam().isBlack() ? getPiece().toString().toLowerCase() : getPiece().toString().toUpperCase();
        }

    }

}

