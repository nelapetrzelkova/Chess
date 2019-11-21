package GUI;

import Board.Board;
import Board.BoardUtils;
import Board.Tile;
import Moves.MakingMove;
import Moves.Move;
import Pieces.Piece;
import Players.Player;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Timer;
import java.util.logging.*;

import static javax.swing.SwingUtilities.*;


//Whole GUI in this class (except opening dialog)
class Table {

    private Board chessBoard;

    private Tile currentTile;
    private Tile newTile;
    private Piece movedPiece;
    private RightPanel rightPanel;
    private MoveHistory moveHistory;
    private BoardPanel boardPanel;
    public LowerBar lowerBar;
    private Clock clock;

    private boolean AIMode;
    private boolean highlightLegalMoves;

    private static final Dimension FRAME_SIZE = new Dimension(950,800);       // size of main frame
    private static final Dimension BOARD_SIZE = new Dimension(400,400);       // size of board
    private static final Dimension TILE_SIZE = new Dimension(20,20);          // size of each tile
    private static final Dimension LOWER_BAR_SIZE = new Dimension(800,25);    // size of lower bar where current player and time is displayed
    private static final boolean[] WHITE_TILES = BoardUtils.whiteTiles();                  // white tiles on board
    private static final int TIME_FOR_PLAYER = 1200;                                       // time for each player for playing

    private final static Logger log = Logger.getLogger(Table.class.getName());

    public static long STARTING_TIME = System.currentTimeMillis();
    public long whitePlayedTime = STARTING_TIME;
    public long blackPlayedTime = STARTING_TIME;

    // constructor - sets frame and all panels, clock runs here
    Table(String path, boolean AIMode) throws IOException {
        JFrame frame = new JFrame("Chess");
        frame.setLayout(new BorderLayout());
        final JMenuBar menuBar = createMenuBar();
        this.rightPanel = new RightPanel();
        frame.setJMenuBar(menuBar);
        frame.setSize(FRAME_SIZE);
        frame.setResizable(false);
        this.chessBoard = Board.createCustomBoard(path);
        this.highlightLegalMoves = true;
        this.AIMode = AIMode;
        this.moveHistory = new MoveHistory();
        frame.add(this.rightPanel, BorderLayout.EAST);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        this.lowerBar = new LowerBar(chessBoard);
        frame.add(lowerBar, BorderLayout.SOUTH);
        this.boardPanel = new BoardPanel();
        frame.add(boardPanel, BorderLayout.CENTER);
        Handler handler = new FileHandler("log.txt");
        log.addHandler(handler);
        handler.setFormatter(new SimpleFormatter());
        this.clock = new Clock(null, this);
        clock.start();
    }

    public Board getChessBoard() {
        return chessBoard;
    }

    private RightPanel getRightPanel() {
        return rightPanel;
    }

    private BoardPanel getBoardPanel() {
        return boardPanel;
    }

    private JMenuBar createMenuBar() {
        final JMenuBar tableMenuBar = new JMenuBar();
        tableMenuBar.add(createFileMenu());
        tableMenuBar.add(createPreferencesMenu());
        tableMenuBar.add(createCastleMovesMenu());
        tableMenuBar.add(createPGNMenu());
        tableMenuBar.add(createExitMenu());
        return tableMenuBar;
    }

    private JMenu createPGNMenu() {

        final JMenu PGNMenu = new JMenu("PGN");
        final JMenuItem loadPGN = new JMenuItem("Load PGN File");
        final JMenuItem savePGN = new JMenuItem("Save PGN File");

        savePGN.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                File file = new File("PGN.pgn");
                try {
                    if (file.createNewFile()) {
                        System.out.println("File was created.");
                    } else {
                        System.out.println("File with last saved PGN was overwritten.");
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                FileWriter writer;
                try {
                    writer = new FileWriter(file);
                    if (!AIMode) {
                        writer.write("[Event \"Custom Game\"] \n[Site \"Prague\"]\n[Date \"" + new Date().toString() + "\"]\n[Round \"-\"]\n[White \"Human\"]\n[Black \"Human\"]\n[Result \"*\"]\n\n");
                    } else {
                        writer.write("[Event \"Custom Game\"] \n[Site \"Prague\"]\n[Date \"" + new Date().toString() + "\"]\n[Round \"-\"]\n[White \"Human\"]\n[Black \"Computer\"]\n[Result \"*\"]\n\n");
                    }
                    int round = 1;
                    int counter = 0;
                    for (Move move : Table.this.getMoveHistory().getMoves()) {
                        if (counter % 2 == 0) {
                            writer.write(round++ + ". ");
                        }
                        writer.write(move.toString() + " ");
                        counter++;
                    }

                    writer.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

            }
        });
        loadPGN.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                final JFileChooser fc = new JFileChooser();
                int choice = fc.showOpenDialog(null);
                if (choice == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        Board board = PGNGame.processPGNFile(file);
                        File fileWithPng = new File("transformed_png.txt");
                        FileWriter writer = new FileWriter(fileWithPng);
                        writer.write(board.currentPlayer().toString() + "\n");
                        writer.write(board.toString());
                        writer.close();
                        moveHistory = PGNGame.getMoveHistory(file);
                        try {
                            chessBoard = Board.createCustomBoard(fileWithPng.getPath());
                            SwingUtilities.invokeLater(new Runnable() {
                                public void run() {
                                    try {
                                        boardPanel.drawBoard(chessBoard);
                                        whitePlayedTime = STARTING_TIME;
                                        blackPlayedTime = STARTING_TIME;
                                        lowerBar.drawLowerBar(chessBoard, whitePlayedTime, blackPlayedTime);
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            });
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                try {
                                    boardPanel.drawBoard(chessBoard);
                                    rightPanel.redraw(chessBoard, moveHistory);
                                    whitePlayedTime = STARTING_TIME;
                                    blackPlayedTime = STARTING_TIME;
                                    lowerBar.drawLowerBar(chessBoard, whitePlayedTime, blackPlayedTime);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        PGNMenu.add(loadPGN);
        PGNMenu.add(savePGN);

        return PGNMenu;

    }

    private JMenu createFileMenu() {
        final JMenu fileMenu = new JMenu("File");
        final JMenuItem newGame = new JMenuItem("New game");
        final JMenuItem saveGame = new JMenuItem("Save game");
        final JMenuItem loadGame = new JMenuItem("Load game");
        final JMenuItem undoLastMove = new JMenuItem("Undo last move");
        final JFileChooser fc = new JFileChooser();
        newGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new OpeningDialog();
            }
        });
        saveGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Table.this.saveGame(chessBoard);
                    JFrame frame = new JFrame();
                    Container pane = frame.getContentPane();
                    pane.setLayout(new FlowLayout());
                    JOptionPane.showMessageDialog(pane, "Game was saved.",
                            "Game over", JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        loadGame.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int choice = fc.showOpenDialog(null);
                if (choice == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        chessBoard = Board.createCustomBoard(file.getPath());
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                try {
                                    boardPanel.drawBoard(chessBoard);
                                    whitePlayedTime = STARTING_TIME;
                                    blackPlayedTime = STARTING_TIME;
                                    lowerBar.drawLowerBar(chessBoard, whitePlayedTime, blackPlayedTime);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });
        undoLastMove.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    Table.this.undoLastMove();
                    if (AIMode) {
                        Table.this.undoLastMove();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        fileMenu.add(undoLastMove);
        fileMenu.addSeparator();
        fileMenu.add(newGame);
        fileMenu.add(saveGame);
        fileMenu.add(loadGame);

        return fileMenu;
    }

    /**
     * saves game to text file
     * @param chessBoard
     * @throws IOException
     */
    private void saveGame(Board chessBoard) throws IOException {

        File file = new File("last_saved_game.txt");
        if (file.createNewFile())
        {
            System.out.println("File was created.");
        } else {
            System.out.println("File with last saved game was overwritten.");
        }

        FileWriter writer = new FileWriter(file);
        writer.write(chessBoard.currentPlayer().toString() + "\n");
        writer.write(chessBoard.toString());
        writer.close();
    }

    /**
     * do move and redraw the board
     * @param transition
     * @param move
     * @return
     */
    private boolean doMove(MakingMove transition, Move move) {
        boolean ret = false;
        System.out.println("tu");
        if (transition.getMoveStatus().isDone()) {
            chessBoard = transition.getNewBoard();
            moveHistory.addMove(move);
            ret = true;
        } else {
            log.info("leaves player in check");
        }
        currentTile = null;
        newTile = null;
        movedPiece = null;

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    rightPanel.redraw(chessBoard, moveHistory);
                    boardPanel.drawBoard(chessBoard);
                    if (chessBoard.currentPlayer().toString().equals("white")) {
                        whitePlayedTime = whitePlayedTime - 1;
                    } else {
                        blackPlayedTime = blackPlayedTime - 1;
                    }
                    lowerBar.drawLowerBar(chessBoard, clock.getWhitePlayedTime(), clock.getBlackPlayedTime());
                    log.info("redrawing board:\n" + chessBoard.toString());
                    //black that checks if king has any moves that does not lead to check
                    int counter = 0;
                    Collection<Move> kingMoves = chessBoard.currentPlayer().king.getPossibleMoves(chessBoard);
                    boolean noOtherMoves = kingMoves.size() == chessBoard.currentPlayer().legalMoves.size();
                    for (Move m : kingMoves) {
                        if (!Player.calculateAttacksOnTile(m.getNewCoord(), chessBoard.currentPlayer().getOpponent().getLegalMoves()).isEmpty()) {
                            counter++;
                        }
                    }
                    //if yes - game over
                    if (counter == kingMoves.size() && noOtherMoves) {
                        JFrame frame = new JFrame();
                        Container pane = frame.getContentPane();
                        pane.setLayout(new FlowLayout());
                        JOptionPane.showMessageDialog(pane, "Game over, " + chessBoard.currentPlayer().getOpponent() + " won the game, " +
                                        chessBoard.currentPlayer() + " has no other possible moves.",
                                "Game over", JOptionPane.INFORMATION_MESSAGE);
                        System.exit(0);
                    }

                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
        return ret;
    }

    private JMenu createCastleMovesMenu() {
        final JMenu castleMovesMenu = new JMenu("Castle moves");
        final JMenuItem kingSide = new JMenuItem("Kingside");
        final JMenuItem queenSide = new JMenuItem("Queenside");
        kingSide.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Collection<Move> legalMoves = chessBoard.currentPlayer().legalMoves;
                for (Move move : legalMoves) {
                    if (move instanceof Move.KingSideCastleMove) {
                        final MakingMove transition = chessBoard.currentPlayer().makeMove(move);
                        Table.this.doMove(transition, move);
                    }
                }
            }
        });
        queenSide.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Collection<Move> legalMoves = chessBoard.currentPlayer().legalMoves;
                for (Move move : legalMoves) {
                    if (move instanceof Move.QueenSideCastleMove) {
                        final MakingMove transition = chessBoard.currentPlayer().makeMove(move);
                        Table.this.doMove(transition, move);
                    }
                }
            }
        });
        castleMovesMenu.add(kingSide);
        castleMovesMenu.add(queenSide);
        return castleMovesMenu;
    }

    private JMenu createExitMenu() {
        final JMenu exitMenu = new JMenu("Exit");
        final JMenuItem noSave = new JMenuItem("Exit");
        noSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        exitMenu.add(noSave);
        return exitMenu;
    }

    private JMenu createPreferencesMenu() {
        final JMenu preferencesMenu = new JMenu("Preferences");
        final JCheckBoxMenuItem highlightItem = new JCheckBoxMenuItem("Highlight possible moves", true);
        highlightItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                highlightLegalMoves = highlightItem.isSelected();
                log.info("Switched higlighting legal moves, current state " + highlightLegalMoves);
            }
        });
        preferencesMenu.addSeparator();
        preferencesMenu.add(highlightItem);
        return preferencesMenu;
    }

    /**
     * function that redraws the boards and right panel when we undo move
     * @throws IOException
     */
    private void undoLastMove() throws IOException {
        final Move lastMove = getMoveHistory().removeMove(getMoveHistory().size() - 1);
        this.chessBoard = this.chessBoard.currentPlayer().unMakeMove(lastMove).getNewBoard();
        getMoveHistory().removeMove(lastMove);
        if (AIMode) {
            chessBoard.setCurrentPlayer(chessBoard.currentPlayer());
        }
        currentTile = null;
        newTile = null;
        movedPiece = null;
        getRightPanel().redraw(chessBoard, getMoveHistory());
        getBoardPanel().drawBoard(chessBoard);
    }

    private MoveHistory getMoveHistory() {
        return this.moveHistory;
    }

    public class LowerBar extends JPanel {
        Board board;

        LowerBar(Board board) {
            super(new BorderLayout());
            this.board = board;
            setPreferredSize(LOWER_BAR_SIZE);
            this.add(new JLabel(" Current player: " + chessBoard.currentPlayer().toString()), BorderLayout.WEST);
            this.add(new JLabel("W: " + hoursFormat(TIME_FOR_PLAYER) + " | B: " + hoursFormat(TIME_FOR_PLAYER) + " "), BorderLayout.EAST);
            //createThreads(board);
            validate();
        }

        /**
         * redraws lower bar, it is called every second from the Table constructor
         * @param board
         * @param curWhiteTime
         * @param curBlackTime
         */
        void drawLowerBar(Board board, long curWhiteTime, long curBlackTime) {
            log.info("Redrawing lower bar");
            removeAll();
            this.add(new JLabel(" Current player: " + board.currentPlayer().toString()), BorderLayout.WEST);
            this.add(new JLabel("W: " + hoursFormat(TIME_FOR_PLAYER - (STARTING_TIME - curWhiteTime)) +
                    " | B: " + hoursFormat(TIME_FOR_PLAYER - (STARTING_TIME - curBlackTime)) + " "), BorderLayout.EAST);
            validate();
            repaint();
            if (hoursFormat(TIME_FOR_PLAYER - (STARTING_TIME - curWhiteTime)).equals("00:00") ||
                    hoursFormat(TIME_FOR_PLAYER - (STARTING_TIME - curBlackTime)).equals("00:00")) {
                JFrame frame = new JFrame();
                Container pane = frame.getContentPane();
                pane.setLayout(new FlowLayout());
                JOptionPane.showMessageDialog(pane, "Game over, " + chessBoard.currentPlayer().getOpponent() + " won the game.",
                        "Game over", JOptionPane.INFORMATION_MESSAGE);
            }
        }

        private String hoursFormat(long i) {
            long minutes = (long)Math.floor(i/60);
            long seconds = i % 60;
            if (seconds < 10 && minutes < 10) {
                return "0" + minutes + ":0" + seconds;
            } if (minutes < 10) {
                return "0" + minutes + ":" + seconds;
            } if (seconds < 10) {
                return minutes + ":0" + seconds;
            } else {
                return minutes + ":" + seconds;
            }
        }
    }

    private class BoardPanel extends JPanel {

        final List<TilePanel> boardTiles;

        BoardPanel() throws IOException {
            super(new GridLayout(8,8));
            this.boardTiles = new ArrayList();
            for (int i = 0; i < 64; i++) {
                final TilePanel tilePanel = new TilePanel(this, i);
                this.boardTiles.add(tilePanel);
                add(tilePanel);
            }
            setPreferredSize(BOARD_SIZE);
            validate();
        }

        /**
         * draws board - tile after tile
         * @param board - current state of board
         * @throws IOException
         */
        void drawBoard(Board board) throws IOException {
            removeAll();
            for (TilePanel tile : boardTiles) {
                tile.drawTile(board);
                add(tile);
            }
            validate();
            repaint();
        }
    }


    private class TilePanel extends JPanel {

        private final int tileId;

        TilePanel(final BoardPanel boardPanel, final int tileId) throws IOException {
            super(new GridBagLayout());
            this.tileId = tileId;
            setPreferredSize(TILE_SIZE);
            assignColor();
            addPiece(chessBoard);

            addMouseListener(new MouseListener() {
                @Override
                public void mouseClicked(MouseEvent e) {


                    if (isRightMouseButton(e)) { //cancelling all selections

                        currentTile = null;
                        newTile = null;
                        movedPiece = null;
                        SwingUtilities.invokeLater(new Runnable() {
                            public void run() {
                                try {
                                    rightPanel.redraw(chessBoard, moveHistory);
                                    boardPanel.drawBoard(chessBoard);
                                    log.log(Level.INFO, "Right click - cancels the tile that has been clicked on previously.");

                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });

                    } else if (isLeftMouseButton(e)){                        //left mouse button

                        if (currentTile == null) {

                            //first click
                            currentTile = chessBoard.getTile(tileId);
                            movedPiece = currentTile.getPiece();

                            if (movedPiece == null) {
                                currentTile = null;
                            } else {
                                try {
                                    highlightLegalMoves(chessBoard);
                                } catch (IOException ex) {
                                    ex.printStackTrace();
                                }
                            }
                            SwingUtilities.invokeLater(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        rightPanel.redraw(chessBoard, moveHistory);
                                        boardPanel.drawBoard(chessBoard);
                                        log.log(Level.INFO, "Fist left click on tile " + BoardUtils.getPosAtCoord(currentTile.getTileCoord()));

                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            });

                        } else {

                            //second click
                            newTile = chessBoard.getTile(tileId);
                            final Move move = Move.createMove(chessBoard, currentTile.getTileCoord(), newTile.getTileCoord());
                            final MakingMove transition = chessBoard.currentPlayer().makeMove(move);
                            final String piece = currentTile.getPiece().toString();
                            log.log(Level.INFO, "Second left click on tile " + BoardUtils.getPosAtCoord(newTile.getTileCoord()) +
                                    " " + piece);
                            boolean moveMade = doMove(transition, move);
                            checkCheckMate();

                            if (AIMode && moveMade) {
                                System.out.println("AIMode on");
                                List<Move> possibleMoves = (List<Move>) chessBoard.currentPlayer().legalMoves;
                                if (chessBoard.currentPlayer().isInCheck()) {
                                    for (Move m : possibleMoves) {
                                        final MakingMove moving = chessBoard.currentPlayer().makeMove(m);
                                        if (moving.getMoveStatus().isDone()) {
                                            doMove(moving, m);
                                        }
                                    }
                                } else {
                                    List<Move> attackMoves;
                                    attackMoves = new ArrayList(Collections.singletonList(new Move.NullMove()));
                                    Move move2;
                                    for (Move m2 : possibleMoves) {
                                        if (m2 instanceof Move.AttackMove) {
                                            attackMoves.add(m2);
                                        }
                                    }
                                    if (attackMoves.size() > 1) {
                                        move2 = attackMoves.get(1);

                                    } else {
                                        int randInd = (int) (Math.random() * possibleMoves.size());
                                        move2 = possibleMoves.get(randInd);
                                    }
                                    System.out.println(move2.toString());
                                    final MakingMove transition2 = chessBoard.currentPlayer().makeMove(move2);
                                    doMove(transition2, move2);
                                }
                                checkCheckMate();
                            }
                        }


                    }
                }

                @Override
                public void mousePressed(MouseEvent e) {

                }

                @Override
                public void mouseReleased(MouseEvent e) {

                }

                @Override
                public void mouseEntered(MouseEvent e) {

                }

                @Override
                public void mouseExited(MouseEvent e) {

                }
            });

            validate();
        }

        /**
         * assigns color to each tile (dark/light)
         */
        private void assignColor() {
            if (WHITE_TILES[this.tileId]) {
                setBackground(Color.WHITE);
            } else {
                setBackground(Color.LIGHT_GRAY);

            }
        }

        /**
         * add piece to tile if there is any
         * @param board
         * @throws IOException
         */
        private void addPiece(Board board) throws IOException {
            this.removeAll();
            if (!board.getTile(this.tileId).empty()) {
                String piecesPath = "src/pieces/";
                final BufferedImage image = ImageIO.read(new File(piecesPath
                        + board.getTile(this.tileId).getPiece().getPieceTeam().toString().substring(0,1)
                        + board.getTile(this.tileId).getPiece().toString() + ".gif"));
                add(new JLabel(new ImageIcon(image)));
            }
        }

        void drawTile(Board board) throws IOException {
            assignColor();
            addPiece(board);
            highlightLegalMoves(board);
            validate();
            repaint();
        }

        /**
         * if checkbox higlight legal moves is checked add green dot to tile where are legal moves
         * @param board
         * @throws IOException
         */
        private void highlightLegalMoves(Board board) throws IOException {
            if (highlightLegalMoves) {
                for (Move move : legalMovesOfPiece(board)) {
                    if (move.getNewCoord() == this.tileId || (board.currentPlayer().isInCheck() && board.currentPlayer().makeMove(move).getMoveStatus().isDone())) {
                        add(new JLabel(new ImageIcon(ImageIO.read(new File("src/pieces/green_dot.png")))));
                    }
                }
            }
        }

        private Collection<Move> legalMovesOfPiece(Board board) {
            if (movedPiece != null && movedPiece.getPieceTeam() == board.currentPlayer().getTeam()) {
                return movedPiece.getPossibleMoves(board);
            }
            return Collections.emptyList();
        }
    }

    /**
     * if is player in check mate ends the game
     */
    private void checkCheckMate() {
        if (chessBoard.currentPlayer().isInCheckMate()) {
            JFrame frame = new JFrame();
            Container pane = frame.getContentPane();
            pane.setLayout(new FlowLayout());
            JOptionPane.showMessageDialog(pane, "Game over, " + chessBoard.currentPlayer().getOpponent() + " won the game.",
                    "Game over", JOptionPane.INFORMATION_MESSAGE);
        }
    }


    static class MoveHistory {

        private final List<Move> moves;

        MoveHistory() {
            this.moves = new ArrayList();
        }

        List<Move> getMoves() {
            return this.moves;
        }

        void addMove(Move move) {
            this.moves.add(move);
        }

        Move removeMove (int idx) {
            return this.moves.remove(idx);
        }

        void removeMove(Move move) {
            this.moves.remove(move);
        }

        int size() {
            return this.moves.size();
        }

    }

}

