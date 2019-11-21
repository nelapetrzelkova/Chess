package GUI;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

/**
 * class with dialog that user see when he start the game
 */
public class OpeningDialog extends JDialog{

    private static final String TITLE = "Chess menu";
    private static final String HUMAN_TEXT = "Human";
    private static final String COMPUTER_TEXT = "Computer";
    private static final String DEFAULT_BOARD = "Default";
    private static final String CUSTOM_BOARD = "Custom";

    public OpeningDialog() {
        final JPanel myPanel = new JPanel(new GridLayout(0, 1));
        Border border = BorderFactory.createTitledBorder(TITLE);
        myPanel.setBorder(border);
        final JRadioButton humanButton = new JRadioButton(HUMAN_TEXT);
        final JRadioButton computerButton = new JRadioButton(COMPUTER_TEXT);
        final JRadioButton defaultBoardButton = new JRadioButton(DEFAULT_BOARD);
        final JRadioButton customBoardButton = new JRadioButton(CUSTOM_BOARD);
        humanButton.setActionCommand(HUMAN_TEXT);
        final ButtonGroup AIModeGroup = new ButtonGroup();
        AIModeGroup.add(humanButton);
        AIModeGroup.add(computerButton);
        humanButton.setSelected(true);

        final ButtonGroup chooseBoardGroup = new ButtonGroup();
        chooseBoardGroup.add(defaultBoardButton);
        chooseBoardGroup.add(customBoardButton);
        defaultBoardButton.setSelected(true);

        getContentPane().add(myPanel);
        myPanel.add(new JLabel("  Do you want to play against computer or human?  "));
        myPanel.add(humanButton);
        myPanel.add(computerButton);
        myPanel.add(new JLabel("  Do you want to start with default or custom board?  "));
        myPanel.add(defaultBoardButton);
        myPanel.add(customBoardButton);
        setLocationRelativeTo(null);

        final JButton okButton = new JButton("OK");
        final JButton cancelButton = new JButton("Cancel");

        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    if (computerButton.isSelected() && defaultBoardButton.isSelected()) {
                        new Table("src/defaultboard.txt", true);
                    } else if (humanButton.isSelected() && defaultBoardButton.isSelected()) {
                        new Table("src/defaultboard.txt", false);
                    } else if (computerButton.isSelected() && customBoardButton.isSelected()) {
                        final JFileChooser fc = new JFileChooser();
                        int choice = fc.showOpenDialog(null);
                        if (choice == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            new Table(file.getPath(), true);
                        }
                    } else {
                        final JFileChooser fc = new JFileChooser();
                        int choice = fc.showOpenDialog(null);
                        if (choice == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            new Table(file.getPath(), false);
                        }
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                OpeningDialog.this.setVisible(false);
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Cancel");
                OpeningDialog.this.setVisible(false);
            }
        });

        myPanel.add(okButton);
        myPanel.add(cancelButton);

        pack();
        setVisible(true);
    }
}
