package GUI;

import Board.Board;
import Moves.Move;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


/**
 * panel on rigth where is history of current game
 */
class RightPanel extends JPanel {
    private final DataModel model;
    private final JScrollPane scrollPane;
    private static final Dimension RIGHT_PANEL_SIZE = new Dimension(150,400);

    /**
     * in right panel is table with all previously done moves
     */
    RightPanel() {
        this.setLayout(new BorderLayout());
        this.model = new DataModel();
        final JTable table = new JTable(model);
        table.setRowHeight(15);
        this.scrollPane = new JScrollPane(table);
        scrollPane.setColumnHeaderView(table.getTableHeader());
        scrollPane.setPreferredSize(RIGHT_PANEL_SIZE);
        this.add(scrollPane, BorderLayout.CENTER);
        this.setVisible(true);
    }

    void redraw(Board board, Table.MoveHistory moveHistory) {
        int currentRow = 0;
        this.model.clear();
        for (Move move : moveHistory.getMoves()) {
            final String moveText = move.toString();
            if (move.movedPiece.getPieceTeam().isWhite()) {
                this.model.setValueAt(moveText, currentRow, 0);
            } else if (move.movedPiece.getPieceTeam().isBlack()) {
                this.model.setValueAt(moveText, currentRow, 1);
                currentRow++;
            }
        }

        if (moveHistory.getMoves().size() > 0) {
            final Move lastMove = moveHistory.getMoves().get(moveHistory.size() - 1);
            final String moveText = lastMove.toString();

            if(lastMove.getMovedPiece().getPieceTeam().isWhite()) {
                this.model.setValueAt(moveText + calculateCheck(board), currentRow, 0);
            } else if (lastMove.getMovedPiece().getPieceTeam().isBlack()) {
                this.model.setValueAt(moveText + calculateCheck(board), currentRow -1, 1);
            }
        }

        final JScrollBar vertical = scrollPane.getVerticalScrollBar();
        vertical.setValue(vertical.getMaximum());
    }

    /**
     * adds extra char if king is in check or checkmate
     * @param board
     * @return
     */
    private String calculateCheck(Board board) {
        if (board.currentPlayer().isInCheckMate()) {
            return "#";
        } else if (board.currentPlayer().isInCheck()) {
            return "+";
        }
        return "";
    }

    /**
     * table format of the panel
     */
    private static class DataModel extends DefaultTableModel {

        private final List<Row> values;
        private static final String[] HEADER = {"White", "Black"};

        DataModel() {
            this.values = new ArrayList();
        }

        void clear() {
            this.values.clear();
            setRowCount(0);
        }

        @Override
        public int getRowCount() {
            if (this.values == null) {
                return 0;
            }
            return this.values.size();
        }

        @Override
        public int getColumnCount() {
            return HEADER.length;
        }

        @Override
        public Object getValueAt(int row, int column) {
            final Row currentRow = this.values.get(row);
            if (column == 0) {
                return currentRow.getWhiteMove();
            }
            if (column == 1) {
                return currentRow.getBlackMove();
            }
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int row, int column) {
            final Row currentRow;
            if (this.values.size() <= row) {
                currentRow = new Row();
                this.values.add(currentRow);
            } else {
                currentRow = this.values.get(row);
            }
            if (column == 0) {
                currentRow.setWhiteMove((String)aValue);
                fireTableRowsInserted(row, row);
            } else if (column == 1) {
                currentRow.setBlackMove((String)aValue);
                fireTableCellUpdated(row,column);
            }
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            return Move.class;
        }

        @Override
        public String getColumnName(int column) {
            return HEADER[column];
        }
    }

    private static class Row {
        private String whiteMove;
        private String blackMove;

        Row() {}

        public String getWhiteMove() {
            return whiteMove;
        }

        public void setWhiteMove(String whiteMove) {
            this.whiteMove = whiteMove;
        }

        public String getBlackMove() {
            return blackMove;
        }

        public void setBlackMove(String blackMove) {
            this.blackMove = blackMove;
        }
    }
}
