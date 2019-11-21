package GUI;

import Board.Board;
import Board.BoardUtils;
import Moves.MakingMove;
import Moves.Move;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * class that handles PGN files
 */
public abstract class PGNGame {

    /**
     * regexes to help to recognize type of move
     */
    private static final Pattern KING_SIDE_CASTLE = Pattern.compile("^O-O#?\\+?$");
    private static final Pattern QUEEN_SIDE_CASTLE = Pattern.compile("^O-O-O#?\\+?$");
    private static final Pattern PLAIN_PAWN_MOVE = Pattern.compile("^([a-h][0-8])(\\+)?(#)?$");
    private static final Pattern PAWN_ATTACK_MOVE = Pattern.compile("(^[a-h])(x)([a-h][0-8])(\\+)?(#)?$");
    private static final Pattern PLAIN_MAJOR_MOVE = Pattern.compile("^(B|N|R|Q|K)([a-h]|[1-8])?([a-h][0-8])(\\+)?(#)?$");
    private static final Pattern MAJOR_ATTACK_MOVE = Pattern.compile("^(B|N|R|Q|K)([a-h]|[1-8])?(x)([a-h][0-8])(\\+)?(#)?$");
    private static final Pattern PLAIN_PAWN_PROMOTION_MOVE = Pattern.compile("(.*?)=(.*?)");
    private static final Pattern ATTACK_PAWN_PROMOTION_MOVE = Pattern.compile("(.*?)x(.*?)=(.*?)");
    private static int counter = 0;



    private final List<String> moves;

    PGNGame(final List<String> moves) {
        this.moves = moves;
    }

    /**
     * creates history of moves from given PGN file
     * @param file
     * @return
     * @throws IOException
     */
    public static Table.MoveHistory getMoveHistory(File file) throws IOException {
        Board board = Board.createStandardBoard();
        Table.MoveHistory moveHistory = new Table.MoveHistory();
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        int ch = br.read();
        while (ch != -1) {
            if ((char) ch == '\n') {
                sb.append(' ');
            } else {
                sb.append((char) ch);
            }
            if ((char) ch == '.' ) {
                sb.append(' ');
            }
            ch = br.read();
        }
        String content = sb.toString();
        String[] moves = content.split(" ");
        MakingMove transition;
        for (String move : moves) {
            if (move.equals("1-0") || move.equals("0-1")) {
                String winner = calculateWinner(move);
                break;
            }
            Move newMove = createMove(board, move);
            transition = board.currentPlayer().makeMove(newMove);
            if (transition.getMoveStatus().isDone()) {
                moveHistory.addMove(newMove);
                counter++;
            }
            board = transition.getNewBoard();
        }

        return moveHistory;
    }

    private static String calculateWinner(final String gameOutcome) {
        if(gameOutcome.equals("1-0")) {
            return "White";
        }
        if(gameOutcome.equals("0-1")) {
            return "Black";
        }
        if(gameOutcome.equals("1/2-1/2")) {
            return "Tie";
        }
        return "None";
    }

    /**
     * function that
     * @param file
     * @return
     * @throws IOException
     */
    static Board processPGNFile(File file) throws IOException {
        Board board = Board.createStandardBoard();
        System.out.println("created standard board");
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder sb = new StringBuilder();
        int ch = br.read();
        while (ch != -1) {
            sb.append((char)ch);
            if ((char) ch == '.') {
                sb.append(' ');
            }
            ch = br.read();
        }
        String content = sb.toString();
        String[] moves = content.split(" ");
        MakingMove transition;
        for (String move : moves) {
            System.out.println("Move: '" + move.toString() + "'");
            if (move.equals("1-0") || move.equals("0-1")) {
                System.out.println("break");
                break;
            }
            Move newMove = createMove(board, move);
            transition = board.currentPlayer().makeMove(newMove);
            board = transition.getNewBoard();
        }
        final Move lastMove = getMoveHistory(file).removeMove(getMoveHistory(file).size() - 1);
        board = board.currentPlayer().unMakeMove(lastMove).getNewBoard();
        return board;
    }

    private static int extractFurther(final List<Move> candidateMoves,
                                      final String movedPiece,
                                      final String disambiguationFile) {

        final List<Move> currentCandidates = new ArrayList();

        for(final Move move : candidateMoves) {
            if(move.getMovedPiece().getPieceType().toString().equals(movedPiece)) {
                currentCandidates.add(move);
            }
        }

        if(currentCandidates.size() == 1) {
            return currentCandidates.iterator().next().movedPiece.position;
        }

        final List<Move> candidatesRefined = new ArrayList();

        for (final Move move : currentCandidates) {
            final String pos = BoardUtils.getPosAtCoord(move.movedPiece.position);
            if (pos.contains(disambiguationFile)) {
                candidatesRefined.add(move);
            }
        }

        if(candidatesRefined.size() == 1) {
            return candidatesRefined.iterator().next().movedPiece.position;
        }

        return -1;

    }

    private static Move createMove(Board board, String pgnText) {

        System.out.println(pgnText);


        final Matcher kingSideCastleMatcher = KING_SIDE_CASTLE.matcher(pgnText);
        final Matcher queenSideCastleMatcher = QUEEN_SIDE_CASTLE.matcher(pgnText);
        final Matcher plainPawnMatcher = PLAIN_PAWN_MOVE.matcher(pgnText);
        final Matcher attackPawnMatcher = PAWN_ATTACK_MOVE.matcher(pgnText);
        final Matcher pawnPromotionMatcher = PLAIN_PAWN_PROMOTION_MOVE.matcher(pgnText);
        final Matcher attackPawnPromotionMatcher = ATTACK_PAWN_PROMOTION_MOVE.matcher(pgnText);
        final Matcher plainMajorMatcher = PLAIN_MAJOR_MOVE.matcher(pgnText);
        final Matcher attackMajorMatcher = MAJOR_ATTACK_MOVE.matcher(pgnText);

        int currentCoordinate;
        int newCoord;

        if(kingSideCastleMatcher.matches()) {
            return extractCastleMove(board, "O-O");
        } else if (queenSideCastleMatcher.matches()) {
            return extractCastleMove(board, "O-O-O");
        } else if(plainPawnMatcher.matches()) {
            final String destinationSquare = plainPawnMatcher.group(1);
            newCoord = BoardUtils.getCoordAtPos(destinationSquare);
            currentCoordinate = deriveCurrentCoordinate(board, "P", destinationSquare, "");
            return Move.createMove(board, currentCoordinate, newCoord);
        } else if(attackPawnMatcher.matches()) {
            final String destinationSquare = attackPawnMatcher.group(3);
            newCoord = BoardUtils.getCoordAtPos(destinationSquare);
            final String disambiguationFile = attackPawnMatcher.group(1) != null ? attackPawnMatcher.group(1) : "";
            currentCoordinate = deriveCurrentCoordinate(board, "P", destinationSquare, disambiguationFile);
            return Move.createMove(board, currentCoordinate, newCoord);
        } else if (attackPawnPromotionMatcher.matches()) {
            final String destinationSquare = attackPawnPromotionMatcher.group(2);
            final String disambiguationFile = attackPawnPromotionMatcher.group(1) != null ? attackPawnPromotionMatcher.group(1) : "";
            newCoord = BoardUtils.getCoordAtPos(destinationSquare);
            currentCoordinate = deriveCurrentCoordinate(board, "P", destinationSquare, disambiguationFile);
            return Move.createMove(board, currentCoordinate, newCoord);
        } else if(pawnPromotionMatcher.find()) {
            final String destinationSquare = pawnPromotionMatcher.group(1);
            newCoord = BoardUtils.getCoordAtPos(destinationSquare);
            currentCoordinate = deriveCurrentCoordinate(board, "P", destinationSquare, "");
            return Move.createMove(board, currentCoordinate, newCoord);
        } else if (plainMajorMatcher.find()) {
            final String destinationSquare = plainMajorMatcher.group(3);
            newCoord = BoardUtils.getCoordAtPos(destinationSquare);
            final String disambiguationFile = plainMajorMatcher.group(2) != null ? plainMajorMatcher.group(2) : "";
            currentCoordinate = deriveCurrentCoordinate(board, plainMajorMatcher.group(1), destinationSquare, disambiguationFile);
            return Move.createMove(board, currentCoordinate, newCoord);
        } else if(attackMajorMatcher.find()) {
            final String destinationSquare = attackMajorMatcher.group(4);
            newCoord = BoardUtils.getCoordAtPos(destinationSquare);
            final String disambiguationFile = attackMajorMatcher.group(2) != null ? attackMajorMatcher.group(2) : "";
            currentCoordinate = deriveCurrentCoordinate(board, attackMajorMatcher.group(1), destinationSquare, disambiguationFile);
            return Move.createMove(board, currentCoordinate, newCoord);
        }

        return new Move.NullMove();

    }

    /**
     * helps to recognize where the destination tile of a piece is
     * @param board
     * @param movedPiece
     * @param destinationSquare
     * @param disambiguationFile
     * @return
     * @throws RuntimeException
     */
    private static int deriveCurrentCoordinate(final Board board,
                                               final String movedPiece,
                                               final String destinationSquare,
                                               final String disambiguationFile) throws RuntimeException {
        final List<Move> currentCandidates = new ArrayList();
        final int newCoord = BoardUtils.getCoordAtPos(destinationSquare);
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            if (move.getNewCoord() == newCoord && move.getMovedPiece().toString().equals(movedPiece)) {
                currentCandidates.add(move);
            }
        }
        if(currentCandidates.size() == 0) {
            return -1;
        }

        return currentCandidates.size() == 1
                ? currentCandidates.iterator().next().movedPiece.position
                : extractFurther(currentCandidates, movedPiece, disambiguationFile);
    }

    /**
     * help to recognize castle moves
     * @param board
     * @param castleMove
     * @return
     */
    private static Move extractCastleMove(final Board board,
                                          final String castleMove) {
        for (final Move move : board.currentPlayer().getLegalMoves()) {
            if (move.isCastlingMove() && move.toString().equals(castleMove)) {
                return move;
            }
        }
        return new Move.NullMove();
    }

}
