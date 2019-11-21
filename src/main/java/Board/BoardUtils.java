package Board;


import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardUtils {

    BoardUtils() {
        throw new RuntimeException("Not instantiable");
    }

    public static final boolean[] FIRST_COLUMN = getColumn(0); //columns, where can be some exceptions (bishop, rook, knight)
    public static final boolean[] SECOND_COLUMN = getColumn(1);
    public static final boolean[] SEVENTH_COLUMN = getColumn(6);
    public static final boolean[] EIGHTH_COLUMN = getColumn(7);

    public static final boolean[] FIRST_ROW = getRow(0);
    public static final boolean[] SECOND_ROW = getRow(8); //I need second and seventh row for pawns - so I know whether I can make the jump
    public static final boolean[] FOURTH_ROW = getRow(24);
    public static final boolean[] FIFTH_ROW = getRow(32);
    public static final boolean[] SEVENTH_ROW = getRow(48);
    public static final boolean[] EIGHT_ROW = getRow(56);

    public static final List<String> ALGEBRAIC_NOTATION = Arrays.asList(algebraicNotation());
    public static final Map<String, Integer> POSITION_TO_COORDINATE = posToCoordMap();

    public static boolean isValidCoord(int coord) { //board is represented as 1D array and this checks if we don't go out from the chessboard
        return coord >= 0 && coord < 64;
    }

    private static boolean[] getColumn(int columnIdx) { //function that returns boolean array[64] with true on indexes when you are on desired column, false otherwise
        final boolean[] column = new boolean[64];

        while (columnIdx < 64) {
            column[columnIdx] = true;
            columnIdx += 8;
        }
        return column;
    }

    private static boolean[] getRow(int rowNumber) { //function that returns boolean array[64] with true on indexes when you are on desired row, false otherwise
        final boolean[] row = new boolean[64];
        do {
            row[rowNumber] = true;
            rowNumber++;
        } while(rowNumber % 8 != 0);
        return row;
    }

    public static String getPosAtCoord(int coord) {
        return ALGEBRAIC_NOTATION.get(coord);
    }

    private static String[] algebraicNotation() {
        return new String[]{
                "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8",
                "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7",
                "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6",
                "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5",
                "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4",
                "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3",
                "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2",
                "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"
        };
    }

    /**
     *  transforms algebraic notation to coords we use on our board
     * @param pos
     * @return
     */
    public static int getCoordAtPos(String pos) {
        return POSITION_TO_COORDINATE.get(pos);
    }

    /**
     * transform given position to algebraic notation
     * @return
     */
    private static Map<String, Integer> posToCoordMap() {
        final Map<String, Integer> positionToCoordinate = new HashMap();
        for (int i = 0; i < 64; i++) {
            positionToCoordinate.put(ALGEBRAIC_NOTATION.get(i), i);
        }
        return positionToCoordinate;
    }


    public static boolean[] whiteTiles() {
        final boolean[] whiteTiles = new boolean[64];
        int idx = 0;

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                whiteTiles[idx] = (i % 2 == 0 && j % 2 == 0) || (i % 2 == 1 && j % 2 == 1);
                idx++;
            }
        }
        return whiteTiles;
    }


}

